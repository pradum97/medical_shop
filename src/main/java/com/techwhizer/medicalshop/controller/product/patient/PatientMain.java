package com.techwhizer.medicalshop.controller.product.patient;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.model.PatientModel;
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
import java.util.Properties;
import java.util.ResourceBundle;

public class PatientMain implements Initializable {

    private int rowsPerPage = 15;
    public TableView<PatientModel> tableView;
    public TableColumn<PatientModel, Integer> colSrNo;
    public TableColumn<PatientModel, String> colCId;
    public TableColumn<PatientModel, String> colName;
    public TableColumn<PatientModel, String> colPhone;
    public TableColumn<PatientModel, String> colAddress;
    public TableColumn<PatientModel, String> colDate;
    public TableColumn<PatientModel, String> colIdNum;
    public TableColumn<PatientModel, String> colGender;
    public TableColumn<PatientModel, String> colAge;
    public TableColumn<PatientModel, String> colDiscount;
    public TableColumn<PatientModel, String> colCareOf;
    public TableColumn<PatientModel, String> colWeight;
    public TableColumn<PatientModel, String> colBp;
    public TableColumn<PatientModel, String> colPulse;
    public TableColumn<PatientModel, String> colSugar;

    public Pagination pagination;
    public TextField searchTf;
    private Properties propRead;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private FilteredList<PatientModel> filteredData;
    private Method method;

    private ObservableList<PatientModel> patientList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();
        PropertiesLoader propLoader = new PropertiesLoader();
        propRead = propLoader.getReadProp();

        callThread();

    }

    private void onColumnEdit(TableColumn<PatientModel, String> col, String updateColumnName) {

        col.setCellFactory(TextFieldTableCell.forTableColumn());

        col.setOnEditCommit(e -> {
            String value = e.getNewValue();

            if (col == colName || col == colGender){

                if (value.isEmpty()) {
                    callThread();
                    customDialog.showAlertBox("Failed", "Empty Value Not Accepted");
                    return;
                }
            }

            if (col == colDiscount){
                if (!value.isEmpty()) {

                    double dis  = 0.0;
                    try {
                      dis = Double.parseDouble(value);
                    } catch (NumberFormatException ex) {
                        callThread();
                        customDialog.showAlertBox("Invalid", "Please Enter valid discount");
                        return;
                    }

                    if (dis >100){
                        callThread();
                        customDialog.showAlertBox("", "Please Enter discount less then 100");
                        return;
                    }
                }
            }

            if (e.getTableColumn().equals(colPhone)) {

               if (!value.isEmpty()){
                   try {
                       long num = Long.parseLong(e.getNewValue());
                   } catch (NumberFormatException ex) {
                       customDialog.showAlertBox("", "Please Enter valid phone number");
                       callThread();
                       return;
                   }

                   int length = e.getNewValue().length();
                   if (length != 10) {
                       customDialog.showAlertBox("", "Enter 10-digit phone number");
                       callThread();
                       return;
                   }
               }

            }
            int id = e.getTableView().getItems().get(e.getTablePosition().getRow()).getPatientId();
            update(e.getNewValue(), updateColumnName, id);

        });
    }

    private void update(String newValue, String columnName, int id) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {

            connection = dbConnection.getConnection();

            String query = "UPDATE tbl_patient SET " + columnName + " = ?  where patient_id = ?";

            ps = connection.prepareStatement(query);

            if (columnName.equalsIgnoreCase("discount")){
                ps.setDouble(1, Double.parseDouble(newValue));
            }else {
                ps.setString(1, newValue);
            }

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


    private void callThread() {
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg = "";

        @Override
        public void onPreExecute() {
            tableView.setPlaceholder(method.getProgressBar(40,40));
        }

        @Override
        public Boolean doInBackground(String... params) {

            Map<String, Object> status = getPatient();
            boolean isSuccess = (boolean) status.get("is_success");
            msg = (String) status.get("message");
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

    private Map<String, Object> getPatient() {
        Map<String, Object> map = new HashMap<>();

        if (null != patientList) {
            patientList.clear();
        }
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = dbConnection.getConnection();
            ps = connection.prepareStatement(propRead.getProperty("READ_ALL_CUSTOMER"));
            rs = ps.executeQuery();

            int res = 0;

            while (rs.next()) {

                int patient_id = rs.getInt("PATIENT_ID");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String gender = rs.getString("gender");
                String idNum = rs.getString("id_number");
                String age = rs.getString("age");
                double discount = rs.getDouble("discount");
                String careOf = rs.getString("care_of");
                String weight = rs.getString("weight");
                String bp = rs.getString("bp");
                String pulse = rs.getString("pulse");
                String sugar = rs.getString("sugar");
                String registeredDate = rs.getString("registered_date");

                PatientModel pm = new PatientModel(patient_id,name, method.rec(phone), method.rec(address), method.rec(idNum),
                        method.rec(String.valueOf(discount)), method.rec(gender), method.rec(age), method.rec(careOf), method.rec(weight), method.rec(bp),
                        method.rec(pulse), method.rec(sugar), method.rec(registeredDate));
                patientList.add(pm);
                res++;
            }
            if (null != patientList) {
                if (patientList.size() > 0) {
                    pagination.setVisible(true);
                    search_Item();
                }
            }

            if (res > 0) {
                map.put("message", "success");
                map.put("is_success", true);
            } else {
                map.put("message", "Item not available");
                map.put("is_success", false);
            }

        } catch (SQLException e) {
            map.put("message", "An error occurred while fetching the item");
            map.put("is_success", false);
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

        return map;
    }

    private void search_Item() {

        filteredData = new FilteredList<>(patientList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(patient -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(patient.getPhone()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return patient.getName().toLowerCase().contains(lowerCaseFilter);
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
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colIdNum.setCellValueFactory(new PropertyValueFactory<>("idNumber"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colCareOf.setCellValueFactory(new PropertyValueFactory<>("careOf"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colBp.setCellValueFactory(new PropertyValueFactory<>("bp"));
        colPulse.setCellValueFactory(new PropertyValueFactory<>("pulse"));
        colSugar.setCellValueFactory(new PropertyValueFactory<>("sugar"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("registered_date"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

        onColumnEdit(colName, "name");
        onColumnEdit(colAddress, "addressTf");
        onColumnEdit(colPhone, "phone");
        onColumnEdit(colIdNum, "id_number");
        onColumnEdit(colGender, "gender");
        onColumnEdit(colAge, "age");

        onColumnEdit(colDiscount, "discount");

        onColumnEdit(colCareOf, "care_of");
        onColumnEdit(colWeight, "weight");
        onColumnEdit(colBp, "bp");
        onColumnEdit(colPulse, "pulse");
        onColumnEdit(colSugar, "sugar");

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, patientList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<PatientModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    public void addPatient(ActionEvent event) {
        customDialog.showFxmlDialog2("product/patient/addPatient.fxml", "Add New Patient");
        callThread();
    }
}
