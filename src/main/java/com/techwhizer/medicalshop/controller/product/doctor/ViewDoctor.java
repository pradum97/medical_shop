package com.techwhizer.medicalshop.controller.product.doctor;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.DealerModel;
import com.techwhizer.medicalshop.model.DoctorModel;
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
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewDoctor implements Initializable {
    private int rowsPerPage = 12;
    public TextField searchTf;
    public Pagination pagination;
    public TableColumn<DoctorModel, Integer> colSrNo;
    public TableColumn<DoctorModel, String> colName;
    public TableColumn<DoctorModel, String> colReg;
    public TableView<DoctorModel> tableView;
    public TableColumn<DoctorModel, String> colPhone;
    public TableColumn<DoctorModel, String> colSpec;
    public TableColumn<DoctorModel, String> colQly;
    public TableColumn<DoctorModel, String> colAddress;
    public TableColumn<DoctorModel, String> colDate;
    private Method method;
    private FilteredList<DoctorModel> filteredData;
    private ObservableList<DoctorModel> doctorList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        callThread();
    }

    public void addDr(ActionEvent event) {
        new CustomDialog().showFxmlDialog2("product/doctor/add_doctor.fxml","Add doctor");
        callThread();
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

            if (null != tableView) {
                tableView.setItems(null);
            }
            assert tableView != null;
            tableView.setPlaceholder(method.getProgressBar(40, 40));

        }

        @Override
        public Boolean doInBackground(String... params) {
            getDoctor();
            return false;

        }

        @Override
        public void onPostExecute(Boolean success) {
            tableView.setPlaceholder(new Label("Doctor not available"));
            if (null != doctorList) {
                if (doctorList.size() > 0) {
                    pagination.setVisible(true);
                    search_Item();
                }
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private void getDoctor() {
        if (null != doctorList){
            doctorList.clear();
        }

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = new DBConnection().getConnection();

            String query = """
                    SELECT doctor_id,dr_name,coalesce(dr_phone,'-')as dr_phone,coalesce(dr_address,'-') as dr_address,
                    coalesce(speciality,'-') as speciality ,
                    coalesce(dr_reg_num,'-') as dr_reg_num,coalesce(qualification,'-') as qualification 
                    ,(TO_CHAR(created_date, 'DD-MM-YYYY')) as created_date FROM tbl_doctor order by doctor_id asc
                    """;
            ps = connection.prepareStatement(query);

            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("DOCTOR_ID");
                String name = rs.getString("DR_NAME");
                String phone = rs.getString("DR_PHONE");
                String address = rs.getString("DR_ADDRESS");
                String spec = rs.getString("SPECIALITY");
                String reg = rs.getString("DR_REG_NUM");
                String qly = rs.getString("QUALIFICATION");
                String date = rs.getString("CREATED_DATE");
                doctorList.add(new DoctorModel(id, name, method.rec(phone), method.rec(address), method.rec(reg),
                        method.rec(spec), method.rec(qly), date));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

    }

    private void search_Item() {

        filteredData = new FilteredList<>(doctorList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(patient -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(patient.getDrName()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(patient.getDrPhone()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return patient.getDrRegNo().toLowerCase().contains(lowerCaseFilter);
            });

            changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
        });

        pagination.setCurrentPageIndex(0);
        changeTableView(0, rowsPerPage);
        Platform.runLater(() -> {
            pagination.currentPageIndexProperty().addListener(
                    (observable1, oldValue1, newValue1) -> {
                        changeTableView(newValue1.intValue(), rowsPerPage);
                    });
        });

    }

    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(filteredData.size() * 1.0 / rowsPerPage));
        Platform.runLater(() -> pagination.setPageCount(totalPage));

        colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                tableView.getItems().indexOf(cellData.getValue()) + 1));
        colName.setCellValueFactory(new PropertyValueFactory<>("drName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("drPhone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("drAddress"));
        colSpec.setCellValueFactory(new PropertyValueFactory<>("drSpeciality"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colQly.setCellValueFactory(new PropertyValueFactory<>("qualification"));
        colReg.setCellValueFactory(new PropertyValueFactory<>("drRegNo"));

        onColumnEdit(colName, "DR_NAME");
        onColumnEdit(colPhone, "DR_PHONE");
        onColumnEdit(colAddress, "DR_ADDRESS");
        onColumnEdit(colSpec, "SPECIALITY");
        onColumnEdit(colQly, "QUALIFICATION");
        onColumnEdit(colReg, "DR_REG_NUM");

        tableView.setItems(doctorList);
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, doctorList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<DoctorModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    private void onColumnEdit(TableColumn<DoctorModel, String> col, String updateColumnName) {

        col.setCellFactory(TextFieldTableCell.forTableColumn());

        col.setOnEditCommit(e -> {
            String value = e.getNewValue();

            if (col.equals(colName)) {
                if (value.isEmpty()) {
                    callThread();
                    new CustomDialog().showAlertBox("Failed", "Empty Value Not Accepted");
                    return;
                }
            }
            int dealerId = e.getTableView().getItems().get(e.getTablePosition().getRow()).getDoctorId();

            update(e.getNewValue(), updateColumnName, dealerId);
        });
    }

    private void update(String newValue, String columnName, int dealerId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = new DBConnection().getConnection();

            if (null == connection) {
                return;
            }

            String query = "UPDATE tbl_doctor SET " + columnName + " = ?  where doctor_id = ?";

            ps = connection.prepareStatement(query);
            ps.setString(1, newValue);
            ps.setInt(2, dealerId);

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
