package com.techwhizer.medicalshop.controller.product;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.CompanyModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

public class ViewCompany implements Initializable {
    private int rowsPerPage = 8;
    public TextField companyAddressTf;
    public TextField companyNameTF;
    public TableColumn<CompanyModel , String> colAddress;
    public Pagination pagination;
    public TableView<CompanyModel> tableView;
    public TableColumn<CompanyModel , String> colCName;
    public TableColumn<CompanyModel,Integer> colSrNo;
    public TableColumn<CompanyModel , String> colType;
    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private ObservableList<CompanyModel> companyList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();

        getCompany();
    }

    private void onColumnEdit(TableColumn<CompanyModel, String> col, String updateColumnName) {

        col.setCellFactory(TextFieldTableCell.forTableColumn());

        col.setOnEditCommit(e -> {

            String value = e.getNewValue();

            if (value.isEmpty()) {
                getCompany();
                customDialog.showAlertBox("Empty", "Empty Value Not Accepted");
                return;
            }
            int companyId = e.getTableView().getItems().get(e.getTablePosition().getRow()).getCompany_id();
            update(e.getNewValue(), updateColumnName, companyId);
        });
    }

    private void update(String newValue, String columnName, int companyId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = dbConnection.getConnection();

            if (null == connection) {
                return;
            }

            String query = "UPDATE tbl_company SET " + columnName + " = ?  where company_id = ?";

            ps = connection.prepareStatement(query);
            ps.setString(1, newValue);
            ps.setInt(2, companyId);

            int res = ps.executeUpdate();

            if (res > 0) {
                getCompany();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }
    }

    private void getCompany() {
        if (null != companyList){
            companyList.clear();
        }
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = dbConnection.getConnection();
            if (null == connection){
                return;
            }
            ps = connection.prepareStatement("SELECT * FROM tbl_company order by company_id asc");
            rs = ps.executeQuery();

            while (rs.next()){
                int categoryId = rs.getInt("company_id");
                String categoryName = rs.getString("company_name");
                String categoryAddress = rs.getString("company_address");
                String createdDate = rs.getString("created_date");
                companyList.add(new CompanyModel(categoryId , categoryName , categoryAddress,createdDate));
            }

            if (companyList.size()>0){
                pagination.setVisible(true);
                pagination.setCurrentPageIndex(0);
                changeTableView(0, rowsPerPage);
                changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
                pagination.currentPageIndexProperty().addListener(
                        (observable1, oldValue1, newValue1) -> changeTableView(newValue1.intValue(), rowsPerPage));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBConnection.closeConnection(connection,ps,rs);
        }
    }

    public void addCompany(ActionEvent event) {

        String cName = companyNameTF.getText();
        String cAddress = companyAddressTf.getText();

        if (cName.isEmpty()){
            method.show_popup("Enter company name",companyNameTF);
            return;
        }else  if (cAddress.isEmpty()){
            method.show_popup("Enter company address",companyAddressTf);
            return;
        }

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();;
            if (null == dbConnection){
                return;
            }

            ps = connection.prepareStatement("INSERT INTO tbl_company(company_name , company_address) VALUES (? , ?)");
            ps.setString(1,cName);
            ps.setString(2,cAddress);

            int res = ps.executeUpdate();

            if (res >0){
                getCompany();
                companyNameTF.setText("");
            }

        } catch (SQLException e) {
            customDialog.showAlertBox("Failed...","Duplicate value not allow ");
        }finally {
            DBConnection.closeConnection(connection,ps,null);
        }
    }

    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(companyList.size() * 1.0 / rowsPerPage));
        pagination.setPageCount(totalPage);

        colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                tableView.getItems().indexOf(cellData.getValue()) + 1));
        colCName.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("companyAddress"));

        onColumnEdit(colAddress,"company_address");
        onColumnEdit(colCName,"company_name");

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, companyList.size());

        int minIndex = Math.min(toIndex, companyList.size());
        SortedList<CompanyModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(companyList.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

    }

    public void enterPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER){

            addCompany(null);
        }
    }
}
