package com.techwhizer.medicalshop.controller.product.purchase;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.PriceTypeModel;
import com.techwhizer.medicalshop.model.PurchaseItemsTemp;
import com.techwhizer.medicalshop.model.chooserModel.ItemChooserModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddPurchaseItems implements Initializable {
    public Label productNameL;
    public VBox expireDateContainer;
    public ComboBox<String> monthCom;
    public ComboBox<Integer> yearCom;
    public TextField batchTf;
    public TextField lotNumTf;
    public TextField packingTf;
    public TextField quantityTf;
    public ComboBox<String> quantityUnitCom;
    public TextField purchaseRateTf;
    public TextField mrpTf;
    public TextField saleRateTf;
    public ComboBox<String> unitCom;
    public VBox stripTabContainer;
    public Label stripTabLabel;
    public TextField stripTabTf;
    private Method method;
    private CustomDialog customDialog;
    private DBConnection dbConnection;
    private StaticData staticData;
    private ItemChooserModel icm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();
        staticData = new StaticData();
        stripTabContainer.setDisable(true);
        setData();

    }


    private void setData() {

        monthCom.setItems(staticData.getMonth());
        yearCom.setItems(staticData.getYear());
        unitCom.valueProperty().addListener((observableValue, s, t1) -> {

            if (t1.equalsIgnoreCase("STRIP")){
                quantityUnitCom.setItems(staticData.tabUnit);
                stripTabLabel.setText("Tab per strip :");
                stripTabContainer.setDisable(false);
               stripTabTf.setText(String.valueOf(icm.getTabPerStrip()));
            }else if (t1.equalsIgnoreCase("TAB")){
                quantityUnitCom.setItems(FXCollections.observableArrayList("TAB"));
                quantityUnitCom.getSelectionModel().select("TAB");
                stripTabLabel.setText("");
                stripTabContainer.setDisable(true);
                stripTabTf.setText("");

            } else {
                quantityUnitCom.setItems(staticData.getQuantityUnit());
                quantityUnitCom.getSelectionModel().select("PCS");
                stripTabLabel.setText("");
                stripTabContainer.setDisable(true);
                stripTabTf.setText("");
            }
        });
    }
    public void cancelClick(ActionEvent actionEvent) {
    }

    public void submitClick(ActionEvent actionEvent) {
        uploadData();
    }

    public void enterKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            uploadData();
        }
    }

    private void uploadData() {

        String batchNumber = batchTf.getText();
        String lotNum = lotNumTf.getText();
        String packing = packingTf.getText();
        String quantity = quantityTf.getText();

        String purchaseRate = purchaseRateTf.getText();
        String mrp = mrpTf.getText();
        String saleRate = saleRateTf.getText();
        String stripTab = stripTabTf.getText();
        int stripTabInt = 0;
        double saleRateD = 0.0, purchasePriceD = 0.0,mrpD = 0.0;

        if (null == icm) {
            method.show_popup("Please select product", productNameL);
            return;
        } else if (batchNumber.isEmpty()) {
            method.show_popup("Please enter batch", batchTf);
            return;
        } else if (monthCom.getSelectionModel().isEmpty()) {
            method.show_popup("Please select expiry month", monthCom);
            return;
        } else if (yearCom.getSelectionModel().isEmpty()) {
            method.show_popup("Please select expiry year", yearCom);
            return;
        } else if (unitCom.getSelectionModel().isEmpty()) {
            method.show_popup("Please select unit", unitCom);
            return;
        } else if (unitCom.getSelectionModel().getSelectedItem().equals("STRIP")) {
            try {
                 stripTabInt  = Integer.parseInt(stripTab);
            } catch (NumberFormatException e) {
                method.show_popup("Please enter number only", stripTabTf);
                return;
            }

            if (stripTab.isEmpty() || stripTabInt < 1) {
                method.show_popup("Please enter tab per strip", stripTabTf);
                return;
            }
        }else if (packing.isEmpty()){
            method.show_popup("Please enter packing", packingTf);
            return;
        }
        if (quantity.isEmpty()) {
            method.show_popup("Please enter quantity", quantityTf);
            return;
        }
        try {
            Double.parseDouble(quantity);
        } catch (NumberFormatException e) {
            method.show_popup("Please enter valid quantity", quantityTf);
            return;
        }

        if (Double.parseDouble(quantity) < 1 ){
            method.show_popup("Enter quantity more then 0", quantityTf);
            return;

        }else if (quantityUnitCom.getSelectionModel().isEmpty()) {
            method.show_popup("Please select quantity unit", quantityUnitCom);
            return;
        } else if (purchaseRate.isEmpty()) {
            method.show_popup("Please enter purchase price", purchaseRateTf);
            return;
        }


        try {
        purchasePriceD =  Double.parseDouble(purchaseRate);
        } catch (NumberFormatException e) {
            method.show_popup("Please enter valid purchase price", purchaseRateTf);
            return;
        }
        if (purchasePriceD <1){
            method.show_popup("Enter valid rate", purchaseRateTf);
            return;
        }else if (mrp.isEmpty()) {
            method.show_popup("Please enter mrp", mrpTf);
            return;
        }
        try {
         mrpD = Double.parseDouble(mrp);
        } catch (NumberFormatException e) {
            method.show_popup("Please enter valid mrp", mrpTf);
            return;
        }

        if (mrpD <0){
            method.show_popup("Enter valid rate", mrpTf);
            return;
        }else if (!saleRate.isEmpty()){
            try {
                saleRateD =  Double.parseDouble(saleRate);
            } catch (NumberFormatException e) {
                method.show_popup("Please enter valid sale rate", saleRateTf);
                return;
            }

            if (saleRateD <0){
                method.show_popup("Enter valid rate", saleRateTf);
                return;
            }
        }

        String expiryMonth = monthCom.getSelectionModel().getSelectedItem();
        int expiryYear = yearCom.getSelectionModel().getSelectedItem();
        String quantityUnit = quantityUnitCom.getSelectionModel().getSelectedItem();
        String unit = unitCom.getSelectionModel().getSelectedItem();

        String expiryDate = expiryMonth+"/"+expiryYear;

        double noOfPcs = 0;
        String qtyUnit = "";
        if (quantityUnit.equalsIgnoreCase("strip")){
            //noOfTab =
            qtyUnit = "TAB";
            noOfPcs =Double.parseDouble(quantity)*stripTabInt ;
        }else {
            qtyUnit = quantityUnit;
            noOfPcs = Double.parseDouble(quantity);
        }

        PurchaseItemsTemp pit = new PurchaseItemsTemp(icm.getItemId(), icm.getItemName(), batchNumber,expiryDate,unit,stripTabInt,
                  packing,lotNum,noOfPcs,qtyUnit,Double.parseDouble(purchaseRate),
                Double.parseDouble(mrp),saleRateD);
       if (null != Main.primaryStage.getUserData()){
           Main.primaryStage.setUserData(null);
       }

       Main.primaryStage.setUserData(pit);

        Stage stage =(Stage) productNameL.getScene().getWindow();

        if (null != stage && stage.isShowing()){
            stage.close();
        }
    }

    public void chooseProduct(MouseEvent actionEvent) {
        customDialog.showFxmlDialog2("chooser/itemChooser.fxml", "SELECT PRODUCT");

        if (Main.primaryStage.getUserData() instanceof ItemChooserModel icm) {
            this.icm = icm;
            productNameL.setText(icm.getItemName());
            packingTf.setText(icm.getPacking());

            if (method.isItemAvailableInStock(icm.getItemId())){

                String stockUnit = method.getStockUnit(icm.getItemId());

                if (stockUnit.equalsIgnoreCase("TAB") || stockUnit.equalsIgnoreCase("STRIP")){
                    unitCom.setItems(staticData.tabUnit);
                    unitCom.getSelectionModel().select(icm.getUnit());
                    if (icm.getUnit().equalsIgnoreCase("strip")) {
                        stripTabTf.setText(String.valueOf(icm.getTabPerStrip()));
                    }
                }else {
                    unitCom.setItems(staticData.pcsUnit);
                    unitCom.getSelectionModel().select("PCS");
                }

                PriceTypeModel ptm = method.getLastPrice(icm.getItemId());
                purchaseRateTf.setText(method.removeZeroAfterDecimal(ptm.getPurchaseRate()));
                mrpTf.setText(method.removeZeroAfterDecimal(ptm.getMrp()));
                saleRateTf.setText(method.removeZeroAfterDecimal(ptm.getSaleRate()));

            }else {
                if (icm.getUnit().equalsIgnoreCase("strip")) {
                    stripTabTf.setText(String.valueOf(icm.getTabPerStrip()));
                }
                unitCom.setItems(staticData.getUnit());
                unitCom.getSelectionModel().select(icm.getUnit());
            }
        }
    }
}
