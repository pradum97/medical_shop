package com.techwhizer.medicalshop.controller.product;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.GetTax;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.*;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AddProduct implements Initializable {
    public Button submitButton;
    public TextField productNameTf;
    public ComboBox<GstModel> hsnCom;
    public ComboBox<String> typeCom;
    public ComboBox<String> narcoticCom;
    public ComboBox<String> itemTypeCom;
    public ComboBox<String> statusCom;
    public ComboBox<DiscountModel> discountCom;
    public TextField packingTf, stripTabTf;
    public ComboBox<String> unitCom;
    public ProgressIndicator progressBar;
    public Label mrL;
    public Label mfrL;
    public Label companyNameL;
    private Method method;
    public Label stripTabLabel;
    public VBox stripTabContainer;
    private CustomDialog customDialog;
    private StaticData staticData;
    private DBConnection dbConnection;
    private ObservableList<DealerModel> dealerList = FXCollections.observableArrayList();
    private ObservableList<String> typeList = FXCollections.observableArrayList("NORMAL", "PROHIBIT");
    private ObservableList<String> narcoticList = FXCollections.observableArrayList("NO", "YES");
    private ObservableList<String> itemTypeList = FXCollections.observableArrayList("NORMAL", "COSTLY ITEMS", "8° STORAGE", "24° STORAGE");
    private ObservableList<String> statusList = FXCollections.observableArrayList("CONTINUE", "CLOSE");
    private CompanyModel companyModel;
    private MrModel mrModel;
    private ManufacturerModal manufacturerModal;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        staticData = new StaticData();
        dbConnection = new DBConnection();
        method.hideElement(progressBar);
        stripTabContainer.setDisable(true);
        callInitializeThread();

        selectionChooser();

    }

    private void selectionChooser() {
    }

    private void callInitializeThread() {

        InitializeDataSetTask task = new InitializeDataSetTask();
        task.setDaemon(false);
        task.execute();
    }

    public void chooseMr(MouseEvent mouseEvent) {
        customDialog.showFxmlDialog2("chooser/mrChooser.fxml", "SELECT MR");
        if (Main.primaryStage.getUserData() instanceof MrModel mm) {
            this.mrModel = mm;
            mrL.setText(mm.getName());
        }
    }

    public void chooseMfr(MouseEvent mouseEvent) {
        customDialog.showFxmlDialog2("chooser/mfrChooser.fxml", "SELECT MANUFACTURE");
        if (Main.primaryStage.getUserData() instanceof ManufacturerModal mm) {
            this.manufacturerModal = mm;
            mfrL.setText(mm.getManufacturer_name());
        }
    }

    public void chooseCompany(MouseEvent mouseEvent) {
        customDialog.showFxmlDialog2("chooser/companyChooser.fxml", "SELECT COMPANY");
        if (Main.primaryStage.getUserData() instanceof CompanyModel cm) {
            this.companyModel = cm;
            companyNameL.setText(cm.getCompanyName());
        }
    }

    private class InitializeDataSetTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        public void onPreExecute() {
            //Background Thread will start
        }

        @Override
        public Boolean doInBackground(String... params) {
            setData();
            getGst();

            getDiscount();
            return true;
        }

        @Override
        public void onPostExecute(Boolean success) {
            if (success) {
                comboBoxConfig();
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private void comboBoxConfig() {

        unitCom.valueProperty().addListener((observableValue, s, newValue) -> {
            stripTabTf.setText("");
            if (newValue.equalsIgnoreCase("STRIP")) {
                stripTabLabel.setText("TAB PER STRIP :");
                stripTabContainer.setDisable(false);
            } else {
                stripTabLabel.setText("");
                stripTabContainer.setDisable(true);
            }

        });
    }

    private class SubmitDataTask extends AsyncTask<String, Integer, Boolean> {

        private Map<String, Object> status;
        private ItemsModel itemsModel;

        public SubmitDataTask(ItemsModel itemsModel) {
            this.itemsModel = itemsModel;
        }

        @Override
        public void onPreExecute() {
            progressBar.setVisible(true);
            method.hideElement(submitButton);
        }

        @Override
        public Boolean doInBackground(String... params) {
            status = uploadData(itemsModel);
            return (boolean) status.get("is_success");
        }

        @Override
        public void onPostExecute(Boolean success) {
            submitButton.setVisible(true);
            method.hideElement(progressBar);

            if (success) {
                productNameTf.setText("");
                packingTf.setText("");
                stripTabTf.setText("");
                discountCom.getSelectionModel().clearSelection();
                companyModel = null;
                manufacturerModal = null;
                mrModel = null;
                companyNameL.setText("Click to choose company");
                mfrL.setText("Click to choose mfr");
                mrL.setText("Click to choose mr");

            }

            Platform.runLater(() -> {
                customDialog.showAlertBox("", (String) status.get("message"));
                // clearAllBn some filed
            });
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private Map<String, Object> uploadData(ItemsModel im) {
        Map<String, Object> map = new HashMap<>();

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();
            String qry = "INSERT INTO TBL_ITEMS_MASTER(ITEMS_NAME, UNIT, PACKING, COMPANY_ID, mfr_id, DISCOUNT_ID, mr_id, GST_ID,\n" +
                    "                             TYPE, NARCOTIC, ITEM_TYPE, STATUS,created_by,STRIP_TAB)\n" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            ps = connection.prepareStatement(qry);

            ps.setString(1, im.getProductName());
            ps.setString(2, im.getUnit());
            ps.setString(3, im.getPacking());

            if (null == companyModel) {
                ps.setNull(4, Types.NULL);
            } else {
                ps.setInt(4, companyModel.getCompany_id());
            }

            if (null == manufacturerModal) {
                ps.setNull(5, Types.NULL);
            } else {
                ps.setInt(5, manufacturerModal.getManufacturer_id());
            }

            if (discountCom.getSelectionModel().isEmpty()) {
                ps.setNull(6, Types.NULL);
            } else {
                ps.setInt(6, im.getDiscount_id());
            }

            if (null == mrModel) {
                ps.setNull(7, Types.NULL);
            } else {
                ps.setInt(7, mrModel.getMr_id());
            }

            ps.setInt(8, im.getGstId());
            ps.setString(9, im.getType());
            ps.setString(10, im.getNarcotic());
            ps.setString(11, im.getItemType());
            ps.setInt(12, im.getStatus());
            ps.setInt(13, Login.currentlyLogin_Id);
            ps.setLong(14, im.getTabPerStrip());

            int res = ps.executeUpdate();
            if (res > 0) {
                map.put("is_success", true);
                map.put("message", "Item successfully created.");
            } else {
                map.put("is_success", true);
                map.put("message", "Item not created. Please try again.");
            }
        } catch (SQLException e) {
            map.put("is_success", true);
            map.put("message", "An error occurred while creating the product");
            submitButton.setVisible(true);
            method.hideElement(progressBar);
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }
        return map;
    }

    private void setData() {
        Platform.runLater(() -> {
            unitCom.setItems(staticData.getUnit());
            typeCom.setItems(typeList);
            typeCom.getSelectionModel().selectFirst();
            narcoticCom.setItems(narcoticList);
            narcoticCom.getSelectionModel().selectFirst();
            itemTypeCom.setItems(itemTypeList);
            itemTypeCom.getSelectionModel().selectFirst();
            statusCom.setItems(statusList);
            statusCom.getSelectionModel().selectFirst();
        });
    }

    public void submit_bn(ActionEvent actionEvent) {
        submit();
    }

    public void closeBn(ActionEvent actionEvent) {
        Stage stage = (Stage) submitButton.getScene().getWindow();

        if (null != stage && stage.isShowing()) {
            stage.close();
        }

    }

    public void addGst(ActionEvent actionEvent) {
        customDialog.showFxmlDialog2("product/gst/addGst.fxml", "Add Gst");
        getGst();
    }

    private void getGst() {
        hsnCom.setItems(new GetTax().getGst());
    }

    public void addDiscount(ActionEvent actionEvent) {
        customDialog.showFxmlDialog2("product/viewCompany.fxml", "Create new discount");
        getDiscount();
    }

    private void getDiscount() {

        discountCom.setItems((ObservableList<DiscountModel>) method.getDiscount().get("data"));
    }

    public void enterKeyPress(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            submit();
        }
    }

    private void submit() {
        String productName = productNameTf.getText();

        String packing = packingTf.getText();
        String stripTab = stripTabTf.getText();

        long stripTabL = 0;
        double purchaseMrpD = 0, mrpD = 0, saleRateD = 0;

        if (productName.isEmpty()) {
            method.show_popup("Please enter product name", productNameTf);
            return;
        } else if (unitCom.getSelectionModel().isEmpty()) {
            method.show_popup("Please select unit", unitCom);
            return;
        } else if (unitCom.getSelectionModel().getSelectedItem().equals("STRIP")) {
            if (stripTab.isEmpty()) {
                method.show_popup("Please enter tab per strip", stripTabTf);
                return;
            }
            try {
                stripTabL = Long.parseLong(stripTab);
            } catch (NumberFormatException e) {
                method.show_popup("Please enter number only", stripTabTf);
                return;
            }
        }

        if (packing.isEmpty()) {
            method.show_popup("Please enter packing", packingTf);
            return;
        } else if (hsnCom.getSelectionModel().isEmpty()) {
            method.show_popup("Please select hsn code", hsnCom);
            return;
        }

        String type = typeCom.getSelectionModel().getSelectedItem();
        String narcotic = narcoticCom.getSelectionModel().getSelectedItem();
        String itemType = itemTypeCom.getSelectionModel().getSelectedItem();
        int status = 0;
        switch (statusCom.getSelectionModel().getSelectedItem()) {
            case "CONTINUE" -> status = 1;
            case "CLOSE" -> status = 0;
        }

        String unit = unitCom.getSelectionModel().getSelectedItem();
        int gstId = hsnCom.getSelectionModel().getSelectedItem().getTaxID();

        int discountId = 0;
        if (!discountCom.getSelectionModel().isEmpty()) {
            discountId = discountCom.getSelectionModel().getSelectedItem().getDiscount_id();
        }

        ItemsModel itemsModel = new ItemsModel(productName, unit, packing, discountId, gstId,
                purchaseMrpD, mrpD, saleRateD, type, narcotic, itemType, status, stripTabL);


        SubmitDataTask task = new SubmitDataTask(itemsModel);
        task.setDaemon(false);
        task.execute();
    }
}
