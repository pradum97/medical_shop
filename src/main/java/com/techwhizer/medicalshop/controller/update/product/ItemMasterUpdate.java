package com.techwhizer.medicalshop.controller.update.product;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
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
import javafx.event.EventHandler;
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

public class ItemMasterUpdate implements Initializable {
    public Button updateButton;
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
    public ProgressIndicator progressBarMain;
    public VBox mainContainer;
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
    private ItemsModel icm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        staticData = new StaticData();
        dbConnection = new DBConnection();
        method.hideElement(progressBar);
        stripTabContainer.setDisable(true);
        mainContainer.setDisable(true);

        callInitializeThread();

        if (Main.primaryStage.getUserData() instanceof ItemsModel icm) {
            this.icm = icm;
        } else {
            customDialog.showAlertBox("", "Something went wrong...");
            return;
        }

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

    public void clearDiscount(ActionEvent event) {
        discountCom.getSelectionModel().clearSelection();
    }

    public void clearMfr(ActionEvent event) {
        if (null != manufacturerModal) {
            manufacturerModal = null;
            mfrL.setText("Click to select mfr");
        }
    }

    public void clearCompany(ActionEvent event) {
        if (null != companyModel) {
            companyModel = null;
            companyNameL.setText("Click to select company");
        }
    }

    public void clearMr(ActionEvent event) {
        if (null != mrModel) {
            mrModel = null;
            mrL.setText("Click to select mr");
        }
    }

    private class InitializeDataSetTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        public void onPreExecute() {
            comboBoxConfig();
        }

        @Override
        public Boolean doInBackground(String... params) {
            getGst();
            Platform.runLater(ItemMasterUpdate.this::setComboBoxData);
            return true;
        }

        @Override
        public void onPostExecute(Boolean success) {
            mainContainer.setDisable(false);
            method.hideElement(progressBarMain);
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private void setComboBoxData() {

        hsnCom.getItems().add(method.getSpecificGst(icm.getGstId()));
        hsnCom.getSelectionModel().selectFirst();

        if (icm.getDiscount_id() > 0) {
                discountCom.getItems().add(method.getSpecificDiscount(icm.getDiscount_id()));
            discountCom.getSelectionModel().selectFirst();
        }else {
            discountCom.setItems((ObservableList<DiscountModel>) method.getDiscount().get("data"));
        }



        setData(icm);
        if (icm.getMr_id() > 0) {
            mrModel = method.getSpecificMr(icm.getMr_id());
            mrL.setText(mrModel.getName());
        }
        if (icm.getMfr_id() > 0) {
            manufacturerModal = method.getManufacture(icm.getMfr_id());
            mfrL.setText(manufacturerModal.getManufacturer_name());
        }
        if (icm.getCompany_id() > 0) {
            companyModel = method.getSpecificCompany(icm.getCompany_id());
            companyNameL.setText(companyModel.getCompanyName());
        }
        discountCom.setOnMouseClicked(mouseEvent ->
                discountCom.setItems((ObservableList<DiscountModel>) method.getDiscount().get("data")));

        hsnCom.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hsnCom.setItems(new GetTax().getGst());
            }
        });
    }

    private void comboBoxConfig() {
        unitCom.valueProperty().addListener((observableValue, s, newValue) -> {
            stripTabTf.setText("");
            if (newValue.equalsIgnoreCase("STRIP")) {
                stripTabLabel.setText("TAB PER STRIP :");
                stripTabContainer.setDisable(false);
                stripTabTf.setText(String.valueOf(icm.getTabPerStrip()));
            } else {
                stripTabLabel.setText("");
                stripTabContainer.setDisable(true);
            }

        });
    }

    private class UpdateDataTask extends AsyncTask<String, Integer, Boolean> {

        private Map<String, Object> status;
        private ItemsModel itemsModel;

        public UpdateDataTask(ItemsModel itemsModel) {
            this.itemsModel = itemsModel;
        }

        @Override
        public void onPreExecute() {
            progressBar.setVisible(true);
            method.hideElement(updateButton);
        }

        @Override
        public Boolean doInBackground(String... params) {
            status = uploadData(itemsModel);
            return (boolean) status.get("is_success");
        }

        @Override
        public void onPostExecute(Boolean success) {
            updateButton.setVisible(true);
            method.hideElement(progressBar);

            if (success) {
                Stage stage = (Stage) updateButton.getScene().getWindow();
                if (null != stage && stage.isShowing()) {
                    stage.close();
                }

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
            String qry = "UPDATE TBL_ITEMS_MASTER SET ITEMS_NAME = ?, UNIT= ?, PACKING= ?, COMPANY_ID= ?, mfr_id= ?, DISCOUNT_ID= ?, mr_id= ?, GST_ID= ?,\n" +
                    "                             TYPE= ?, NARCOTIC= ?, ITEM_TYPE= ?, STATUS= ? ,STRIP_TAB= ? WHERE item_id = ?";

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
            ps.setLong(13, im.getTabPerStrip());
            ps.setInt(14, icm.getItemId());

            int res = ps.executeUpdate();
            if (res > 0) {
                map.put("is_success", true);
                map.put("message", "Item successfully updated.");
            } else {
                map.put("is_success", true);
                map.put("message", "Item not updated. Please try again.");
            }
        } catch (SQLException e) {
            map.put("is_success", true);
            map.put("message", "An error occurred while creating the product");
            updateButton.setVisible(true);
            method.hideElement(progressBar);
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }
        return map;
    }

    private void setData(ItemsModel icm) {

        productNameTf.setText(icm.getProductName());
        packingTf.setText(icm.getPacking());

        typeCom.setItems(typeList);
        typeCom.getSelectionModel().select(icm.getType());
        narcoticCom.setItems(narcoticList);
        narcoticCom.getSelectionModel().select(icm.getNarcotic());
        itemTypeCom.setItems(itemTypeList);
        itemTypeCom.getSelectionModel().select(icm.getItemType());
        statusCom.setItems(statusList);

        String status = "";

        switch (icm.getStatus()) {
            case 1 -> status = "CONTINUE";
            case 0 -> status = "CLOSE";

        }
        statusCom.getSelectionModel().select(status);

        if (method.isItemAvailableInStock(icm.getItemId())) {
            String stockUnit = method.getStockUnit(icm.getItemId());

            if (stockUnit.equalsIgnoreCase("TAB") || stockUnit.equalsIgnoreCase("STRIP")) {
                unitCom.setItems(staticData.tabUnit);
                unitCom.getSelectionModel().select(icm.getUnit());
            } else {
                unitCom.setItems(staticData.pcsUnit);
                unitCom.getSelectionModel().select("PCS");
            }


        } else {
            unitCom.setItems(staticData.getUnit());
            unitCom.getSelectionModel().select(icm.getUnit());
        }
    }

    public void updateBn(ActionEvent actionEvent) {
        update();
    }

    public void closeBn(ActionEvent actionEvent) {
        Stage stage = (Stage) updateButton.getScene().getWindow();

        if (null != stage && stage.isShowing()) {
            stage.close();
        }

    }

    private void getGst() {

    }


    public void enterKeyPress(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            update();
        }
    }

    private void update() {

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


        UpdateDataTask task = new UpdateDataTask(itemsModel);
        task.setDaemon(false);
        task.execute();
    }
}
