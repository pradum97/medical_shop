package com.techwhizer.medicalshop.controller.product.mr;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.MrModel;
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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MrMain implements Initializable {
    public ProgressIndicator progressBar;
    public TableColumn<MrModel,String> colGender;
    private int rowsPerPage = 10;
    public TextField searchTf;
    public TableView<MrModel> tableView;
    public TableColumn<MrModel,Integer> colSrNo;
    public TableColumn<MrModel,String> colName;
    public TableColumn<MrModel,String> colPhone;
    public TableColumn<MrModel,String> colEmail;
    public TableColumn<MrModel,String> colCompany;
    public TableColumn<MrModel,String> colAddress;
    public TableColumn<MrModel,String> colDate;

    public Pagination pagination;
    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private ObservableList<MrModel> mrList = FXCollections.observableArrayList();
    private FilteredList<MrModel> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();
       // tableView.setDisable(true);
        callThread();
    }

    private void callThread() {
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(true);
        myAsyncTask.execute();
    }
    public void addMr(ActionEvent event) {
        customDialog.showFxmlDialog2("product/mr/addMr.fxml","Add new mr");
        callThread();
    }

    private Map<String, Object> getMr() {
        Map<String,Object> map = new HashMap<>();

        if (null != mrList){
            mrList.clear();
        }

        Connection connection = null;
        PreparedStatement ps= null;
        ResultSet rs = null;
        try {
            connection = dbConnection.getConnection();
            String qry = "select *,(TO_CHAR(tml.created_date, 'DD-MM-YYYY')) as created_date" +
                    " from tbl_mr_list tml order by mr_id desc ";
            ps = connection.prepareStatement(qry);
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()){
                int mr_id = rs.getInt("mr_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String company = rs.getString("company");
                String address = rs.getString("address");
                String createdDate = rs.getString("created_date");
                String gender = rs.getString("gender");

                mrList.add(new MrModel(mr_id,name,phone,company,email,address,createdDate,gender));
                count++;
            }

            if (count>0){
                map.put("is_success",true);
                map.put("message","success");
            }else {
                map.put("is_success",false);
                map.put("message","Not available");
            }
        } catch (SQLException e) {
            map.put("is_success",false);
            map.put("message","Something went wrong");
            throw new RuntimeException(e);
        }finally {
            DBConnection.closeConnection(connection,ps,rs);
        }
        return map;
    }
    private void search_Item() {

        filteredData = new FilteredList<>(mrList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(manufactureList -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (manufactureList.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (manufactureList.getPhone().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (null != manufactureList.getCompany() && manufactureList.getCompany().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else return null != manufactureList.getEmail() && String.valueOf(manufactureList.getEmail()).toLowerCase().contains(lowerCaseFilter);
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
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("company"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

        onColumnEdit(colName,"name");
        onColumnEdit(colPhone,"phone");
        onColumnEdit(colEmail,"email");
        onColumnEdit(colCompany,"company");
        onColumnEdit(colAddress,"address");
        onColumnEdit(colGender,"gender");


        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, mrList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<MrModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    private void onColumnEdit(TableColumn<MrModel, String> col, String updateColumnName) {

        col.setCellFactory(TextFieldTableCell.forTableColumn());

        col.setOnEditCommit(e -> {

            String value = e.getNewValue();

            if (col.equals(colName) || col.equals(colGender) || col.equals(colPhone) || col.equals(colAddress)){
                if (value.isEmpty()) {
                    callThread();
                    customDialog.showAlertBox("Empty", "Empty Value Not Accepted");
                    return;
                }
            }


            int id = e.getTableView().getItems().get(e.getTablePosition().getRow()).getMr_id();
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

            String query = "UPDATE tbl_mr_list SET " + columnName + " = ?  where mr_id = ?";
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

    public void refresh(ActionEvent event) {
        callThread();
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

            Map<String, Object> status = getMr();
            boolean isSuccess = (boolean) status.get("is_success");
            msg = (String) status.get("message");
            if (mrList.size() > 0) {
                pagination.setVisible(true);
                search_Item();
            }
            return isSuccess;
        }
        @Override
        public void onPostExecute(Boolean success) {
            if (!success) {
                tableView.setPlaceholder(new Label(msg));
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }
}
