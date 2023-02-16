package com.techwhizer.medicalshop.controller.chooser;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.chooserModel.BatchChooserModel;
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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class BatchChooser implements Initializable {

    public Label productNameL;
    private int rowsPerPage = 7;
    public TextField searchTf;
    public TableColumn<BatchChooserModel, Integer> colSrNo;
    public TableColumn<BatchChooserModel, String> colBatch;
    public TableColumn<BatchChooserModel, String> colExpiryDate;
    public TableColumn<BatchChooserModel, String> colQty;
    public TableColumn<BatchChooserModel, String> colAction;
    public TableView<BatchChooserModel> tableView;
    public Pagination pagination;
    private Method method;
    private CustomDialog customDialog;
    private DBConnection dbConnection;
    private ObservableList<BatchChooserModel> itemList = FXCollections.observableArrayList();
    private FilteredList<BatchChooserModel> filteredData;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();

        if (Main.primaryStage.getUserData() instanceof Map<?,?> map){
            productNameL.setText((String) map.get("item_name"));
            callThread((int)map.get("item_id"));
        }else {
            customDialog.showAlertBox("Item not found","Something went wrong");
        }
    }

    private void callThread(int itemId) {
        MyAsyncTask myAsyncTask = new MyAsyncTask(itemId);
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }
    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
         int itemId;

        public MyAsyncTask(int itemId) {
            this.itemId = itemId;
        }

        @Override
        public void onPreExecute() {
            if (null != tableView) {
                tableView.setItems(null);
            }
            assert tableView != null;
            tableView.setPlaceholder(method.getProgressBar(40, 40));
        }

        @Override
        public Boolean doInBackground(String... params) {
            Map<String, Object> status = getItems(itemId);
            return (boolean) status.get("is_success");
        }

        @Override
        public void onPostExecute(Boolean success) {
            tableView.setPlaceholder(new Label("Not available"));
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private Map<String, Object> getItems(int itemId) {
        if (null != itemList) {
            itemList.clear();
        }
        Map<String, Object> map = new HashMap<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = dbConnection.getConnection();

            String qry = """
                    select  stock_id,tpi.purchase_items_id ,tim.items_name,tim.strip_tab ,tpi.batch , tpi.expiry_date , ts.quantity , ts.quantity_unit  from tbl_stock ts
                    left join tbl_purchase_items tpi on tpi.purchase_items_id = ts.purchase_items_id
                    left join tbl_items_master tim on tim.item_id = ts.item_id
                    where tpi.item_id =?  and ts.quantity>0 order by expiry_date asc
                                        
                    """;
            ps = connection.prepareStatement(qry);
            ps.setInt(1,itemId);
            rs = ps.executeQuery();

            int count = 0;

            while (rs.next()) {

                int stockId = rs.getInt("stock_id");
                String itemName = rs.getString("items_name");
                String batch = rs.getString("batch");
                String expiryDate = rs.getString("expiry_date");
                int quantity = rs.getInt("quantity");
                int strip_tab = rs.getInt("strip_tab");
                int purchase_items_id = rs.getInt("purchase_items_id");
                String quantityUnit = rs.getString("quantity_unit");
                count++;

                String qty = method.tabToStrip(quantity, strip_tab, quantityUnit);

                BatchChooserModel bcm = new BatchChooserModel(stockId, itemName, batch,
                        expiryDate, quantity, quantityUnit, qty,strip_tab,purchase_items_id);
                itemList.add(bcm);
            }

            if (itemList.size() > 0) {
                pagination.setVisible(true);
                search_Item();
            }

            if (count > 0) {
                map.put("is_success", true);
                map.put("message", "Many item find");
            } else {
                map.put("is_success", false);
                map.put("message", "Item not available");
            }

        } catch (SQLException e) {
            map.put("is_success", false);
            map.put("message", "Something went wrong ");
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
        return map;
    }

    private void search_Item() {

        filteredData = new FilteredList<>(itemList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(products -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (products.getBatch().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return String.valueOf(products.getExpiryDate()).toLowerCase().contains(lowerCaseFilter);
            });

            changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
        });

        pagination.setCurrentPageIndex(0);
        changeTableView(0, rowsPerPage);
        pagination.currentPageIndexProperty().addListener((observable1, oldValue1, newValue1) -> {
            changeTableView(newValue1.intValue(), rowsPerPage);
        });
    }

    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(filteredData.size() * 1.0 / rowsPerPage));
        Platform.runLater(() -> pagination.setPageCount(totalPage));

        colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(cellData.getValue()) + 1));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batch"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("fullQty"));

        setOptionalCell();
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, itemList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<BatchChooserModel> sortedData = new SortedList<>(FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    private void setOptionalCell() {

        Callback<TableColumn<BatchChooserModel, String>, TableCell<BatchChooserModel, String>> cellFactory = (TableColumn<BatchChooserModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    Button selectBn = new Button();
                    ImageView iv = new ImageView(new ImageLoader().load("img/icon/rightArrow_ic_white.png"));
                    iv.setFitHeight(17);
                    iv.setFitWidth(17);
                    selectBn.setGraphic(iv);
                    selectBn.setStyle("-fx-cursor: hand ; -fx-background-color: #06a5c1 ; -fx-background-radius: 3 ");

                    selectBn.setOnAction((event) -> {
                        method.selectTable(getIndex(), tableView);
                        BatchChooserModel bcm = tableView.getSelectionModel().getSelectedItem();

                        if (null != bcm) {
                            Main.primaryStage.setUserData(bcm);
                            Stage stage = (Stage) searchTf.getScene().getWindow();
                            if (null != stage && stage.isShowing()) {
                                stage.close();
                            }
                        }
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

}
