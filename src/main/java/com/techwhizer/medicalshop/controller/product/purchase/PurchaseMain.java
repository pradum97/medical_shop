package com.techwhizer.medicalshop.controller.product.purchase;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.GenerateBillNumber;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.DealerModel;
import com.techwhizer.medicalshop.model.PurchaseItemsTemp;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.application.Platform;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        method.hideElement(progressBar);

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

        if (null == dealerModel) {
            method.show_popup("Please select dealer", dealerNameL);
            return;
        } else if (itemList.size() < 1) {
            customDialog.showAlertBox("Items not found", "Please enter item");
            return;
        }

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();

    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg;

        @Override
        public void onPreExecute() {
            msg = "";
            method.hideElement(buttonContainer);
            progressBar.setVisible(true);

        }

        @Override
        public Boolean doInBackground(String... params) {
            uploadData();
            return false;

        }

        @Override
        public void onPostExecute(Boolean success) {
            method.hideElement(progressBar);
            buttonContainer.setVisible(true);
            if (!success) {

            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private void uploadData() {

        String billNum = new GenerateBillNumber().generatePurchaseBillNum();
        String billDate = billDateDp.getEditor().getText();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);
            String purMainQry = "INSERT INTO TBL_PURCHASE_MAIN(DEALER_ID, BILL_NUM, DEALER_BILL_NUM, BILL_DATE)VALUES (?,?,?,?)";
            ps = connection.prepareStatement(purMainQry, new String[]{"purchase_main_id"});
            ps.setInt(1, dealerModel.getDealerId());
            ps.setString(2, billNum);
            ps.setString(3, dealerBillNumTf.getText());
            ps.setString(4, billDate);
            int res = ps.executeUpdate();

            if (res > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int purMainId = rs.getInt(1);
                    ps = null;
                    rs = null;

                    String purItemsQry = "INSERT INTO TBL_PURCHASE_ITEMS(purchase_main_id, item_id, batch," +
                            " expiry_date, lot_number, quantity, quantity_unit,purchase_rate,mrp,sale_price)" +
                            " VALUES (?,?,?,?,?,?,?,?,?,?)";

                    String itemUpdateQry = "UPDATE TBL_ITEMS_MASTER SET UNIT = ?,STRIP_TAB= ?,PACKING = ? WHERE ITEM_ID = ?";

                    ps = connection.prepareStatement(purItemsQry, new String[]{"purchase_items_id"});
                    ps.setInt(1, purMainId);

                    ObservableList<PurchaseItemsTemp> itemsTemp = tableView.getItems();

                    int count = 0;
                    PreparedStatement psItemMasterUpdate = connection.prepareStatement(itemUpdateQry);
                    PreparedStatement psStock;
                    ResultSet rsPItem;

                    for (PurchaseItemsTemp pt : itemsTemp) {

                        int qty = pt.getQuantity();
                        String unit = pt.getQuantityUnit();

                        int noOfPcs = 0;
                        String qtyUnit = "";
                        if (pt.getQuantityUnit().equalsIgnoreCase("STRIP")) {
                            //noOfTab =
                            qtyUnit = "TAB";
                            noOfPcs = qty * pt.getTabPerStrip();
                        } else {
                            qtyUnit = unit;
                            noOfPcs = qty;
                        }

                        ps.setInt(2, pt.getItemId());
                        ps.setString(3, pt.getBatch());
                        ps.setString(4, pt.getExpiryDate());
                        ps.setString(5, pt.getLotNum());
                        ps.setInt(6, noOfPcs);
                        ps.setString(7, qtyUnit);
                        ps.setDouble(8, pt.getPurchasePrice());
                        ps.setDouble(9, pt.getMrp());
                        ps.setDouble(10, pt.getSalePrice());
                        int resPItems = ps.executeUpdate();
                        if (resPItems > 0) {
                            rsPItem = ps.getGeneratedKeys();
                            if (rsPItem.next()) {
                                int purchaseItemId = rsPItem.getInt(1);

                                psItemMasterUpdate.setString(1, pt.getUnit());
                                psItemMasterUpdate.setInt(2, pt.getTabPerStrip());
                                psItemMasterUpdate.setString(3, pt.getPacking());
                                psItemMasterUpdate.setInt(4, pt.getItemId());
                                psItemMasterUpdate.executeUpdate();


                                if (method.isBatchAvailableInStock(pt.getBatch())) {
                                    String stockQryUpdateQry = "UPDATE TBL_STOCK SET PURCHASE_MAIN_ID=?, PURCHASE_ITEMS_ID=?,QUANTITY = QUANTITY+?,\n" +
                                            "QUANTITY_UNIT=?,UPDATE_DATE= ? WHERE item_id = ?";
                                    psStock = connection.prepareStatement(stockQryUpdateQry);

                                    psStock.setInt(1, purMainId);
                                    psStock.setInt(2, purchaseItemId);
                                    psStock.setInt(3, noOfPcs);
                                    psStock.setString(4, qtyUnit);
                                    psStock.setString(5, method.getCurrentDate());
                                    psStock.setInt(6, pt.getItemId());

                                } else {
                                    String stockQryInsertQry = "INSERT INTO TBL_STOCK(ITEM_ID, PURCHASE_MAIN_ID, PURCHASE_ITEMS_ID, QUANTITY," +
                                            " QUANTITY_UNIT,UPDATE_DATE)VALUES(?,?,?,?,?,?)";
                                    psStock = connection.prepareStatement(stockQryInsertQry);
                                    psStock.setInt(1, pt.getItemId());
                                    psStock.setInt(2, purMainId);
                                    psStock.setInt(3, purchaseItemId);
                                    psStock.setInt(4, noOfPcs);
                                    psStock.setString(5, qtyUnit);
                                    psStock.setString(6, method.getCurrentDate());

                                }

                                    psStock.executeUpdate();
                                    count++;
                                }

                            }
                    }

                    if (count > 0) {
                        connection.commit();
                        if (null != itemList) {
                            itemList.clear();
                        }
                        if (null != dealerModel) {
                            dealerModel = null;
                        }

                        dealerBillNumTf.setText("");
                        Platform.runLater(() -> dealerNameL.setText("SELECT DEALER"));
                        tableView.setItems(itemList);
                        tableView.refresh();

                        customDialog.showAlertBox("Success","Successfully added");

                    }


                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            method.hideElement(progressBar);
            buttonContainer.setVisible(true);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }
}
