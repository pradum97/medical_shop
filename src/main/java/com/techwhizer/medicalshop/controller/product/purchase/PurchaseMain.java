package com.techwhizer.medicalshop.controller.product.purchase;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.TaskSample;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.DealerModel;
import com.techwhizer.medicalshop.model.PurchaseItemsTemp;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PurchaseMain implements Initializable {
    public Label dealerNameL;
    public TextField dealerBillNumTf;
    public DatePicker billDateDp;
    public Button addPurchaseItem;
    public TableView<PurchaseItemsTemp> tableView;
    public TableColumn<PurchaseItemsTemp,Integer> colSrNo;
    public TableColumn<PurchaseItemsTemp,String> colProductName;
    public TableColumn<PurchaseItemsTemp,String> colPack;
    public TableColumn<PurchaseItemsTemp,String> colQty;
    public TableColumn<PurchaseItemsTemp,String> colBatch;
    public TableColumn<PurchaseItemsTemp,String> colPurchaseRate;
    public TableColumn<PurchaseItemsTemp,String> colLotNum;
    public TableColumn<PurchaseItemsTemp,String> colQtyUnit;
    public TableColumn<PurchaseItemsTemp,String> colMrp;
    public TableColumn<PurchaseItemsTemp,String> colExpiryDate;
    public TableColumn<PurchaseItemsTemp,String> colAction;
    public Button submitButton;
    public HBox buttonContainer;
    public ProgressIndicator progressBar;

    private Method method;
    private CustomDialog customDialog;
    private DBConnection dbConnection;
    private ObservableList<PurchaseItemsTemp> itemList = FXCollections.observableArrayList();
    private DealerModel dealerModel;
    private PurchaseItemsTemp pit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();

        setData();
    }

    private void setData() {
        setOptionalCell();
        method.convertDateFormat(billDateDp);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();
        billDateDp.getEditor().setText(dtf.format(now));
    }

    public void addPurchaseItem(ActionEvent actionEvent) {
        customDialog.showFxmlDialog2("product/purchase/addPurchaseItems.fxml","ADD PURCHASE ITEM");

        if (Main.primaryStage.getUserData() instanceof PurchaseItemsTemp pit){

            boolean isExists = false;
            for (PurchaseItemsTemp purchaseItemsTemp : itemList) {
                if (purchaseItemsTemp.getItemId() == pit.getItemId()) {
                    isExists = true;
                    break;
                }
            }

            Main.primaryStage.setUserData(null);
            if (isExists){
                customDialog.showAlertBox("Exists","Item already exists");
                return;
            }

            itemList.add(pit);
            tableView.setItems(itemList);

            colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                    tableView.getItems().indexOf(cellData.getValue()) + 1));
            colProductName.setCellValueFactory(new PropertyValueFactory<>("itemsName"));
            colPack.setCellValueFactory(new PropertyValueFactory<>("packing"));
            colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            colQtyUnit.setCellValueFactory(new PropertyValueFactory<>("quantityUnit"));
            colPurchaseRate.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
            colMrp.setCellValueFactory(new PropertyValueFactory<>("mrp"));
            colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
            colBatch.setCellValueFactory(new PropertyValueFactory<>("batch"));
            colLotNum.setCellValueFactory(new PropertyValueFactory<>("lotNum"));
        }


    }
    private void setOptionalCell() {

        Callback<TableColumn<PurchaseItemsTemp, String>, TableCell<PurchaseItemsTemp, String>>
                cellFactory = (TableColumn<PurchaseItemsTemp, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    Button selectBn = new Button();

                    ImageView iv = new ImageView(new ImageLoader().load("img/icon/delete_ic_white.png"));
                    iv.setFitHeight(17);
                    iv.setFitWidth(17);

                    selectBn.setGraphic(iv);
                    selectBn.setStyle("-fx-cursor: hand ; -fx-background-color: #06a5c1 ; -fx-background-radius: 3 ");

                    selectBn.setOnAction((event) -> {
                        method.selectTable(getIndex(), tableView);
                        tableView.getItems().remove(getIndex());
                    });

                    HBox managebtn = new HBox(selectBn);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(selectBn, new Insets(0, 0, 0, 0));

                    setGraphic(managebtn);

                    setText(null);

                }
            }

        };

        colAction.setCellFactory(cellFactory);
    }

    public void chooseDealer(MouseEvent mouseEvent) {

        customDialog.showFxmlDialog2("chooser/dealerChooser.fxml", "SELECT PRODUCT");

        if (Main.primaryStage.getUserData() instanceof DealerModel dm){
            this.dealerModel = dm;
           dealerNameL.setText(dm.getDealerName());
        }
    }

    public void cancelButton(ActionEvent event) {

        Stage stage = (Stage) submitButton.getScene().getWindow();
        if (null != stage && stage.isShowing()){
            stage.close();
        }
    }

    public void submitButtonClick(ActionEvent event) {

       /* if (null == dealerModel){
          method.show_popup("Please select dealer",dealerNameL);
            return;
        } else if (itemList.size()<1) {
            customDialog.showAlertBox("Items not found","Please enter item");
            return;
        }*/


        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();

    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg;

        @Override
        public void onPreExecute() {
            msg = "";
            submitButton.setText("");
            submitButton.setGraphic(method.getProgressBar(20,20));
            submitButton.setDisable(true);

        }

        @Override
        public Boolean doInBackground(String... params) {
            /* Background Thread is running */

           /* Map<String, Object> status = login(input, password);
            boolean isSuccess = (boolean) status.get("is_success");
            msg = (String) status.get("message");
            return isSuccess;*/

            return false;

        }

        @Override
        public void onPostExecute(Boolean success) {
            if (success) {

            } else {
//                loginButton.setVisible(true);
//                hideElement(progressBar);
//                customDialog.showAlertBox("Login failed", msg);
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }
}
