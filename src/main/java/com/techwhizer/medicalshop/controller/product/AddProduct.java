package com.techwhizer.medicalshop.controller.product;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.GetTax;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.*;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddProduct implements Initializable {
    public ComboBox<String>  monthCom;
    public ComboBox<Integer>  yearCom;
    public ComboBox<DealerModel>  dealerCom;
    public Button submitButton;
    public TextField quantityTf;
    public ComboBox<String> quantityUnitCom;
    public VBox expireDateContainer;
    public TextField productNameTf;
    public ComboBox<GstModel> hsnCom;
    public TextField purchaseMrpTf;
    public TextField mrTf;
    public TextField mfrName;
    public TextField batchNumTf;
    public TextField lotNumTf;
    public TextField invoiceNumberTf;
    public TextArea productDescriptionTf;
    public ComboBox<CategoryModel> categoryCom;
    public ComboBox<CompanyModel> companyCom;
    public ComboBox<String> typeCom;
    public ComboBox<String> narcoticCom;
    public ComboBox<String> itemTypeCom;
    public ComboBox<String> statusCom;
    public ComboBox<DiscountModel> discountCom;
    public TextField mrpTf;
    public TextField saleRate;
    private Method method;
    private CustomDialog customDialog;
    private StaticData staticData;
    private DBConnection dbConnection;
    private ObservableList<DealerModel> dealerList = FXCollections.observableArrayList();
    private ObservableList<String> typeList = FXCollections.observableArrayList("NORMAL","PROHIBIT");
    private ObservableList<String> narcoticList = FXCollections.observableArrayList("NO","YES");
    private ObservableList<String> itemTypeList = FXCollections.observableArrayList("NORMAL","COSTLY ITEMS","8° STORAGE","24° STORAGE");
    private ObservableList<String> statusList = FXCollections.observableArrayList("CONTINUE","CLOSE");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        staticData = new StaticData();
        dbConnection = new DBConnection();

        setData();
        getDealer();
        getGst();
        getCompany();
        getDiscount();
        getCategory();
    }


    private void setData() {
        yearCom.setItems(staticData.getYear());
        monthCom.setItems(staticData.getMonth());
        quantityUnitCom.setItems(staticData.getQuantityUnit());


        typeCom.setItems(typeList);
        typeCom.getSelectionModel().selectFirst();
        narcoticCom.setItems(narcoticList);
        narcoticCom.getSelectionModel().selectFirst();
        itemTypeCom.setItems(itemTypeList);
        itemTypeCom.getSelectionModel().selectFirst();
        statusCom.setItems(statusList);
        statusCom.getSelectionModel().selectFirst();
    }

    private void getDealer() {
        Map<String, Object> map = new HashMap<>();

        if (null != dealerList) {
            dealerList.clear();
        }

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = dbConnection.getConnection();

            String query = "SELECT * ,(TO_CHAR(ADDED_DATE, 'DD-MM-YYYY')) as date FROM tbl_dealer order by dealer_id asc";
            ps = connection.prepareStatement(query);

            rs = ps.executeQuery();

            while (rs.next()) {

                int dealerId = rs.getInt("dealer_id");
                String dealerName = rs.getString("dealer_name");
                String dealerPhone = rs.getString("dealer_phone");
                String dealerEmail = rs.getString("dealer_email");
                String dealerGstNum = rs.getString("dealer_gstNo");
                String dealerAddress = rs.getString("ADDRESS");
                String dealerState = rs.getString("STATE");
                String date = rs.getString("date");
                String dealerDl = rs.getString("dealer_dl");


                if (null == dealerName || dealerName.isEmpty()) {
                    dealerName = "-";
                }
                if (null == dealerPhone || dealerPhone.isEmpty()) {
                    dealerPhone = "-";
                }
                if (null == dealerEmail || dealerEmail.isEmpty()) {
                    dealerEmail = "-";
                }
                if (null == dealerAddress || dealerAddress.isEmpty()) {
                    dealerAddress = "-";
                }
                if (null == dealerState || dealerState.isEmpty()) {
                    dealerState = "-";
                }
                if (null == date || date.isEmpty()) {
                    date = "-";
                }
                if (dealerDl.equals("")) {
                    dealerDl = "-";
                }

                if (Objects.equals(dealerGstNum, null) || dealerGstNum.isEmpty()) {
                    dealerGstNum = "-";
                }

                dealerList.add(new DealerModel(dealerId, dealerName, dealerPhone, dealerEmail,
                        dealerGstNum, dealerAddress, dealerState, date, dealerDl));
            }

            dealerCom.setItems(dealerList);

        } catch (SQLException e) {
            map.put("message", "An error occurred while fetching the item");
            map.put("is_success", false);
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

    }

    public void enterPress(KeyEvent keyEvent) {
    }

    public void addTax(ActionEvent actionEvent) {

    }

    public void submit_bn(ActionEvent actionEvent) {

    }

    public void closeBn(ActionEvent actionEvent){
        Stage stage = (Stage) submitButton.getScene().getWindow();

        if (null != stage && stage.isShowing()){
            stage.close();
        }

    }

    public void addDealer(ActionEvent actionEvent) {
        customDialog.showFxmlDialog2("product/dealer/addDealer.fxml","Add dealer");
        getDealer();
    }

    public void addGst(ActionEvent actionEvent){
        customDialog.showFxmlDialog2("product/gst/addGst.fxml","Add Gst");
        getGst();
    }

    private void getGst(){
        hsnCom.setItems(new GetTax().getGst());
    }

    public void addCategory(ActionEvent actionEvent) {
        customDialog.showFxmlDialog2("product/viewCategory.fxml","Categories list");
        getCategory();
    }

    private void getCategory() {
        categoryCom.setItems(method.getCategory());
    }

    public void addCompany(ActionEvent actionEvent) {
        customDialog.showFxmlDialog2("product/viewCompany.fxml","Companies list");
        getCompany();
    }

    private void getCompany() {
        companyCom.setItems(method.getCompany());
    }

    public void addDiscount(ActionEvent actionEvent) {
        customDialog.showFxmlDialog2("product/viewCompany.fxml","Create new discount");
        getDiscount();
    }

    private void getDiscount() {
        discountCom.setItems(method.getDiscount());
    }
}
