package com.techwhizer.medicalshop.controller.dashboard;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.ItemsModel;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class All_Products implements Initializable {

    public Label typeL;
    public Label itemType;
    public Label narcoticL;
    public Label createdDate;
    public Label statusL;
    int rowsPerPage = 15;

    public TextField searchTf;
    public TableView<ItemsModel> tableView;
    public TableColumn<ItemsModel, Integer> colSrNo;
    public TableColumn<ItemsModel, String> colProductName;
    public TableColumn<ItemsModel, String> colPack;
    public TableColumn<ItemsModel, String> colUnit;
    public TableColumn<ItemsModel, String> colHsn;
    public TableColumn<ItemsModel, String> colDiscount;
    public TableColumn<ItemsModel, String> colAction;
    public TableColumn<ItemsModel, String> colStripTab;
    public TableColumn<ItemsModel, String> colMfr;
    public TableColumn<ItemsModel, String> mrName;
    public TableColumn<ItemsModel, String> colCompany;
    public Pagination pagination;

    private Method method;
    private CustomDialog customDialog;
    private ObservableList<ItemsModel> itemList = FXCollections.observableArrayList();
    private FilteredList<ItemsModel> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();

        colAction.setVisible(Login.currentRoleName.equalsIgnoreCase("admin"));

        callThread();

    }

    private void callThread() {
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }

    public void refresh(ActionEvent event) {

        callThread();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg;

        @Override
        public void onPreExecute() {
            msg = "";
            if (null != tableView) {
                tableView.setItems(null);
                tableView.refresh();
            }
            assert tableView != null;
            tableView.setPlaceholder(method.getProgressBar(40, 40));
        }

        @Override
        public Boolean doInBackground(String... params) {
            getAllProduct();

            return false;
        }

        @Override
        public void onPostExecute(Boolean success) {
            tableView.setPlaceholder(new Label("Not Available"));
            if (itemList.size() > 0) {
                pagination.setVisible(true);
                search_Item();
            }
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

    private void getAllProduct() {

        if (null != itemList){
            itemList.clear();
        }

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();

            String qry = "select tim.item_id,tim.items_name,tim.PACKING,tim.unit,tim.company_id,coalesce(tc.company_name,'-')as company_name,\n" +
                    "       tim.mfr_id ,coalesce( tml.manufacturer_name,'-') as manufacturer_name,tim.discount_id,\n" +
                    "       coalesce(td.discount,0) as discount, tim.mr_id,coalesce(t.name,'-') as mr_name, tim.gst_id,coalesce(tpt.gstname,'-')as gstname,\n" +
                    "       coalesce(tpt.igst,0)as igst,coalesce(tpt.sgst,0) as sgst,coalesce(tpt.cgst,0) as cgst,\n" +
                    "      tim.type,tim.narcotic,tim.item_type,tim.status,(TO_CHAR(tim.created_date, 'DD-MM-YYYY')) as created_date ,tim.strip_tab,\n" +
                    "      tpt.hsn_sac as hsn from tbl_items_master tim\n" +
                    "left join tbl_company tc on tim.company_id = tc.company_id\n" +
                    "left join tbl_manufacturer_list tml on tml.mfr_id = tim.mfr_id\n" +
                    "left join tbl_mr_list t on t.mr_id = tim.mr_id\n" +
                    "left join tbl_product_tax tpt on tpt.tax_id = tim.gst_id\n" +
                    "left join tbl_discount td on tim.discount_id = td.discount_id order by item_id desc";
            ps = connection.prepareStatement(qry);
            rs = ps.executeQuery();

            while (rs.next()) {
                int itemID = rs.getInt("ITEM_ID");
                String productName = rs.getString("ITEMS_NAME");
                String unit = rs.getString("UNIT");
                String packing = rs.getString("PACKING");
                int companyId = rs.getInt("COMPANY_ID");
                String companyName = rs.getString("company_name");
                String manufacturerName = rs.getString("manufacturer_name");
                String mrName = rs.getString("mr_name");
                int mfrId = rs.getInt("MFR_ID");
                int discountId = rs.getInt("DISCOUNT_ID");
                int mrId = rs.getInt("MR_ID");
                double discount = rs.getDouble("discount");
                int gstId = rs.getInt("GST_ID");
                int iGst = rs.getInt("igst");
                int cGst = rs.getInt("cgst");
                int sGst = rs.getInt("sgst");
                String type = rs.getString("TYPE");
                String narcotic = rs.getString("NARCOTIC");
                String itemType = rs.getString("ITEM_TYPE");
                int status = rs.getInt("STATUS");
                String createdDate = rs.getString("created_date");
                long tabPerStrip = rs.getLong("STRIP_TAB");
                String hsn = rs.getString("hsn");
                String fullUnit = unit;
                if (tabPerStrip > 0) {
                    fullUnit.concat("-" + (tabPerStrip));
                }

                ItemsModel im = new ItemsModel(itemID, method.rec(productName), method.rec(unit), method.rec(packing),
                        companyId, companyName, mfrId, discountId, discount,
                        mrId, mrName, manufacturerName, gstId, cGst, sGst, iGst, type,
                        narcotic, itemType, status, createdDate, tabPerStrip, hsn, method.rec(fullUnit));
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

                if (products.getProductName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (products.getMfrName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (products.getPacking().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return String.valueOf(products.getCreatedDate()).toLowerCase().contains(lowerCaseFilter);
            });

            changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
        });

        pagination.setCurrentPageIndex(0);
        changeTableView(0, rowsPerPage);
        pagination.currentPageIndexProperty().addListener(
                (observable1, oldValue1, newValue1) -> {
                    changeTableView(newValue1.intValue(), rowsPerPage);
                });

        tableSelected();
    }

    private void tableSelected() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, items) -> {

            if (null != items) {
                typeL.setText(items.getType());
                itemType.setText(items.getItemType());
                narcoticL.setText(items.getNarcotic());
                createdDate.setText(items.getCreatedDate());

                String status = "";
                switch (items.getStatus()) {
                    case 1 -> {
                        status = "Active";
                        statusL.setStyle("-fx-text-fill: #014901");
                    }
                    case 0 -> {
                        status = "Inactive";
                        statusL.setStyle("-fx-text-fill: red");
                    }
                }

                statusL.setText(status.toUpperCase());
            }else {
                typeL.setText("");
                itemType.setText("");
                narcoticL.setText("");
                createdDate.setText("");
                statusL.setText("");
            }
        });
    }

    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(filteredData.size() * 1.0 / rowsPerPage));
        Platform.runLater(() -> pagination.setPageCount(totalPage));

        colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                tableView.getItems().indexOf(cellData.getValue()) + 1));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPack.setCellValueFactory(new PropertyValueFactory<>("packing"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("fullUnit"));
        colHsn.setCellValueFactory(new PropertyValueFactory<>("hsn"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

        colStripTab.setCellValueFactory(new PropertyValueFactory<>("tabPerStrip"));
        colMfr.setCellValueFactory(new PropertyValueFactory<>("mfrName"));
        mrName.setCellValueFactory(new PropertyValueFactory<>("mrName"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("companyName"));

        if (Login.currentRoleName.equalsIgnoreCase("admin")) {
            setOptionalCell();
        }

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, itemList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<ItemsModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    private void setOptionalCell() {

        Callback<TableColumn<ItemsModel, String>, TableCell<ItemsModel, String>>
                cellFactory = (TableColumn<ItemsModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    Button editBn = new Button();
                    Button deleteBbn = new Button();

                    ImageView ivEdit = new ImageView(new ImageLoader().load("img/icon/update_ic.png"));
                    ivEdit.setFitHeight(17);
                    ivEdit.setFitWidth(17);

                    ImageView ivDelete = new ImageView(new ImageLoader().load("img/icon/delete_ic_white.png"));
                    ivDelete.setFitHeight(17);
                    ivDelete.setFitWidth(17);

                    editBn.setGraphic(ivEdit);
                    deleteBbn.setGraphic(ivDelete);
                    editBn.setStyle("-fx-cursor: hand ; -fx-background-color: #06a5c1 ; -fx-background-radius: 3 ");
                    deleteBbn.setStyle("-fx-cursor: hand ; -fx-background-color: red ; -fx-background-radius: 3 ");

                    editBn.setOnAction((event) -> {
                        method.selectTable(getIndex(), tableView);
                        ItemsModel icm = tableView.getSelectionModel().getSelectedItem();
                        Main.primaryStage.setUserData(icm);
                        if (null != tableView){
                            tableView.getSelectionModel().clearSelection();
                        }
                        customDialog.showFxmlDialog2("update/product/itemMasterUpdate.fxml", "UPDATE MASTER");
                        callThread();
                    });
                    deleteBbn.setOnAction((event) -> {
                        method.selectTable(getIndex(), tableView);
                        ItemsModel icm = tableView.getSelectionModel().getSelectedItem();
                        ImageView image = new ImageView(new ImageLoader().load("img/icon/warning_ic.png"));
                        image.setFitWidth(45);
                        image.setFitHeight(45);
                        Alert alert = new Alert(Alert.AlertType.NONE);
                        alert.setAlertType(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Warning ");
                        alert.setGraphic(image);
                        alert.setHeaderText("Are you sure you want to delete this item?");
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initOwner(Main.primaryStage);
                        Optional<ButtonType> result = alert.showAndWait();
                        ButtonType button = result.orElse(ButtonType.CANCEL);
                        if (button == ButtonType.OK) {
                           deleteItem(icm.getItemId());
                        } else {
                            alert.close();
                        }
                    });

                    HBox managebtn = new HBox(editBn, deleteBbn);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(editBn, new Insets(10, 10, 10, 10));

                    setGraphic(managebtn);

                    setText(null);

                }
            }

        };

        colAction.setCellFactory(cellFactory);
    }

    private void deleteItem(int itemId){

        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = new DBConnection().getConnection();
            String qry = "delete from tbl_items_master where item_id = ?";
            ps = connection.prepareStatement(qry);
            ps.setInt(1,itemId);
            int res = ps.executeUpdate();

            if (res>0){
                customDialog.showAlertBox("success","Item successfully deleted");
                callThread();
            }

        } catch (SQLException e) {
            customDialog.showAlertBox("Failed","You cannot delete this item. because this item is linked to another");
            throw new RuntimeException(e);
        }finally {
            DBConnection.closeConnection(connection,ps,null);
        }
    }

}
