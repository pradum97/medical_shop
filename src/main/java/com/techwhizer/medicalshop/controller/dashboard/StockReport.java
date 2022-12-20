package com.techwhizer.medicalshop.controller.dashboard;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.StockModel;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StockReport implements Initializable {

    public Label typeL;
    public Label itemType;
    public Label narcoticL;
    public Label createdDate;
    public Label statusL;
    int rowsPerPage = 15;

    public TextField searchTf;
    public TableView<StockModel> tableView;
    public TableColumn<StockModel, Integer> colSrNo;
    public TableColumn<StockModel, String> colProductName;
    public TableColumn<StockModel, String> colPack;
    public TableColumn<StockModel, String> colAction;
    public TableColumn<StockModel, String> colBatch;
    public TableColumn<StockModel, String> colExpiryDate;
    public TableColumn<StockModel, String> colPurchaseRate;
    public TableColumn<StockModel, String> colMrp;
    public TableColumn<StockModel, String> colSale;
    public TableColumn<StockModel, String> colQty;
    public Pagination pagination;

    private Method method;
    private CustomDialog customDialog;
    private ObservableList<StockModel> itemList = FXCollections.observableArrayList();
    private FilteredList<StockModel> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        colAction.setVisible(false);

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();

    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg;

        @Override
        public void onPreExecute() {
            msg = "";
            tableView.setPlaceholder(method.getProgressBar(40, 40));
        }

        @Override
        public Boolean doInBackground(String... params) {
            getStock();

            return false;
        }

        @Override
        public void onPostExecute(Boolean success) {
            tableView.setPlaceholder(new Label("Not Available"));
            if (itemList.size() > 0) {
                pagination.setVisible(true);
                search_Item();
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private void getStock() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();

            String qry = "select ts.stock_id,tim.strip_tab, tim.items_name,tim.packing,tpi.batch,tpi.expiry_date,tpi.purchase_rate,tpi.mrp,tpi.sale_price,\n" +
                    "       ts.quantity,ts.quantity_unit from tbl_stock ts\n" +
                    "left join tbl_items_master tim on tim.item_id = ts.item_id\n" +
                    "left join tbl_purchase_items tpi on ts.purchase_items_id = tpi.purchase_items_id order by quantity asc";
            ps = connection.prepareStatement(qry);
            rs = ps.executeQuery();

            while (rs.next()) {
                int stockId = rs.getInt("stock_id");
                String productName = rs.getString("ITEMS_NAME");
                String packing = rs.getString("PACKING");
                int quantity = rs.getInt("quantity");
                String quantityUnit = rs.getString("quantity_unit");
                String batch = rs.getString("batch");
                String expiry = rs.getString("expiry_date");
                double purRate =rs.getDouble("purchase_rate");
                double mrp =rs.getDouble("mrp");
                double saleRate =rs.getDouble("sale_price");
                int stripTab = rs.getInt("strip_tab");

                String qty = method.tabToStrip(quantity,stripTab,quantityUnit);

                StockModel im = new StockModel(stockId,productName,packing,quantity,
                        quantityUnit,batch,expiry,purRate,mrp,saleRate,qty);
                itemList.add(im);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    private void search_Item() {

        filteredData = new FilteredList<>(itemList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(products -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (products.getItemName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (products.getExpiry().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (products.getPacking().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return String.valueOf(products.getBatch()).toLowerCase().contains(lowerCaseFilter);
            });

            changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
        });

        pagination.setCurrentPageIndex(0);
        changeTableView(0, rowsPerPage);
        pagination.currentPageIndexProperty().addListener(
                (observable1, oldValue1, newValue1) -> {
                    changeTableView(newValue1.intValue(), rowsPerPage);
                });

    }


    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(filteredData.size() * 1.0 / rowsPerPage));
        Platform.runLater(() -> pagination.setPageCount(totalPage));

        colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                tableView.getItems().indexOf(cellData.getValue()) + 1));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colPack.setCellValueFactory(new PropertyValueFactory<>("packing"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("fullQty"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batch"));

        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiry"));
        colPurchaseRate.setCellValueFactory(new PropertyValueFactory<>("purchaseRate"));
        colMrp.setCellValueFactory(new PropertyValueFactory<>("mrp"));
        colSale.setCellValueFactory(new PropertyValueFactory<>("saleRate"));


        setOptionalCell();
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, itemList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<StockModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    private void setOptionalCell() {

        Callback<TableColumn<StockModel, String>, TableCell<StockModel, String>>
                cellFactory = (TableColumn<StockModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    Button editBn = new Button();


                    ImageView ivEdit = new ImageView(new ImageLoader().load("img/icon/update_ic.png"));
                    ivEdit.setFitHeight(17);
                    ivEdit.setFitWidth(17);


                    editBn.setGraphic(ivEdit);
                    editBn.setStyle("-fx-cursor: hand ; -fx-background-color: #06a5c1 ; -fx-background-radius: 3 ");

                    editBn.setOnAction((event) -> {
                        method.selectTable(getIndex(), tableView);
                        StockModel icm = tableView.getSelectionModel().getSelectedItem();


                    });

                    HBox managebtn = new HBox(editBn);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(editBn, new Insets(10, 10, 10, 10));

                    setGraphic(managebtn);

                    setText(null);

                }
            }

        };

        colAction.setCellFactory(cellFactory);
    }

}
