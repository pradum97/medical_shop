package com.techwhizer.medicalshop.controller.product;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.TaskSample;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.ManufacturerModal;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManufactureMain implements Initializable {
    public TextField searchTf;
    public TableColumn<ManufacturerModal,String> colCreatedDate;
    private int rowsPerPage = 8;
    public TextField manufactureNameTf;
    public TableView<ManufacturerModal> tableView;
    public TableColumn<ManufacturerModal,Integer> colSrNo;
    public TableColumn<ManufacturerModal,String> colManufactureName;
    public Pagination pagination;
    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private ObservableList<ManufacturerModal> manufactureList = FXCollections.observableArrayList();
    private FilteredList<ManufacturerModal> filteredData;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();

        callThread();
        manufactureNameTf.setFocusTraversable(false);
    }

    public void submit(ActionEvent event) {
        addManufacture();
    }
    public void enterPress(KeyEvent keyEvent) {
        if (keyEvent. getCode() ==KeyCode.ENTER){
            addManufacture();
        }
    }

    private void addManufacture() {
        String manufactureName = manufactureNameTf.getText();

        if (manufactureName.isEmpty()){
            method.show_popup("Enter manufacture name ",manufactureNameTf);
            return;
        }

        Connection connection = null;
        PreparedStatement ps= null;
        try {
            connection = dbConnection.getConnection();
            String qry = "INSERT INTO tbl_manufacturer_list(manufacturer_name)VALUES (?)";
            ps = connection.prepareStatement(qry);
            ps.setString(1,manufactureName);
            int res = ps.executeUpdate();

            if (res>0){
                manufactureNameTf.setText("");
                searchTf.setText("");
                callThread();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBConnection.closeConnection(connection,ps,null);
        }
    }

    private void callThread() {

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }
    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg;

        @Override
        public void onPreExecute() {
            msg = "";
            if (null != tableView){
                tableView.setItems(null);
            }
            assert tableView != null;
            tableView.setPlaceholder(method.getProgressBar(40,40));
        }

        @Override
        public Boolean doInBackground(String... params) {
          getManufacture();

            return true;

        }

        @Override
        public void onPostExecute(Boolean success) {

            tableView.setPlaceholder(new Label("Not Available"));

        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private void getManufacture() {

        if (null != manufactureList){
            manufactureList.clear();
        }

        Connection connection = null;
        PreparedStatement ps= null;
        ResultSet rs = null;
        try {
            connection = dbConnection.getConnection();
            String qry = "select tml.mfr_id,tml.manufacturer_name,(TO_CHAR(tml.created_date, 'DD-MM-YYYY')) as created_date" +
                    " from tbl_manufacturer_list tml order by mfr_id desc ";
            ps = connection.prepareStatement(qry);
            rs = ps.executeQuery();
            while (rs.next()){
                int mfr_id = rs.getInt("mfr_id");
                String mfrName = rs.getString("manufacturer_name");
                String createdDate = rs.getString("created_date");

                manufactureList.add(new ManufacturerModal(mfr_id,mfrName,createdDate));
            }

            if (manufactureList.size() > 0) {
                pagination.setVisible(true);
                search_Item();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBConnection.closeConnection(connection,ps,rs);
        }
    }

    private void search_Item() {

        filteredData = new FilteredList<>(manufactureList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(manufactureList -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (manufactureList.getManufacturer_name().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return String.valueOf(manufactureList.getCreatedDate()).toLowerCase().contains(lowerCaseFilter);
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
        colManufactureName.setCellValueFactory(new PropertyValueFactory<>("manufacturer_name"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));

        onColumnEdit(colManufactureName,"manufacturer_name");
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, manufactureList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<ManufacturerModal> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    private void onColumnEdit(TableColumn<ManufacturerModal, String> col, String updateColumnName) {

        col.setCellFactory(TextFieldTableCell.forTableColumn());

        col.setOnEditCommit(e -> {

            String value = e.getNewValue();

            if (value.isEmpty()) {
                callThread();
                customDialog.showAlertBox("Empty", "Empty Value Not Accepted");
                return;
            }
            int id = e.getTableView().getItems().get(e.getTablePosition().getRow()).getManufacturer_id();
            update(e.getNewValue(), updateColumnName, id);
        });
    }
    private void update(String newValue, String columnName, int id) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = dbConnection.getConnection();
            if (null == connection) {
                return;
            }

            String query = "UPDATE tbl_manufacturer_list SET " + columnName + " = ?  where mfr_id = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, newValue);
            ps.setInt(2, id);

            int res = ps.executeUpdate();

            if (res > 0) {
                callThread();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }
    }
}
