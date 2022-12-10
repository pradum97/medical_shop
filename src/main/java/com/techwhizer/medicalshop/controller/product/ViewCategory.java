package com.techwhizer.medicalshop.controller.product;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.CategoryModel;
import com.techwhizer.medicalshop.model.CompanyModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewCategory implements Initializable {
    private int rowsPerPage = 8;
    public Pagination pagination;
    public TextField categoryNameTF;
    public TableView<CategoryModel> tableView;
    public TableColumn<CategoryModel, String> colCName;
    public TableColumn<CategoryModel , String> colAction;
    public TableColumn<CategoryModel,Integer> colSrNo;
    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private ObservableList<CategoryModel> categoryList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();

        getCategory();
        categoryNameTF.setFocusTraversable(false);
    }

    private void onColumnEdit(TableColumn<CategoryModel, String> col, String updateColumnName) {

        col.setCellFactory(TextFieldTableCell.forTableColumn());

        col.setOnEditCommit(e -> {

            String value = e.getNewValue();

            if (value.isEmpty()) {
                getCategory();
                customDialog.showAlertBox("Empty", "Empty Value Not Accepted");
                return;
            }
            int companyId = e.getTableView().getItems().get(e.getTablePosition().getRow()).getCategoryId();
            update(e.getNewValue(), updateColumnName, companyId);
        });
    }
    private void update(String newValue, String columnName, int categoryId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = dbConnection.getConnection();

            if (null == connection) {
                return;
            }

            String query = "UPDATE tbl_category SET " + columnName + " = ?  where category_id = ?";

            ps = connection.prepareStatement(query);
            ps.setString(1, newValue);
            ps.setInt(2, categoryId);

            int res = ps.executeUpdate();

            if (res > 0) {
                getCategory();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }
    }

    private void getCategory() {

        if (null != categoryList){
            categoryList.clear();
        }

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = dbConnection.getConnection();
            if (null == connection){
                return;
            }

            ps = connection.prepareStatement("SELECT * FROM tbl_category order by category_id asc");
            rs = ps.executeQuery();

            while (rs.next()){
                int categoryId = rs.getInt("category_id");
                String categoryName = rs.getString("category_name");
                categoryList.add(new CategoryModel(categoryId , categoryName));
            }

            if (categoryList.size()>0){
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

    public void addCategory(ActionEvent event) {

        String cName = categoryNameTF.getText();

        if (cName.isEmpty()){
            method.show_popup("Enter category name",categoryNameTF);
            return;
        }

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();;
            if (null == dbConnection){
                return;
            }

            ps = connection.prepareStatement("INSERT INTO tbl_category(CATEGORY_NAME) VALUES (?)");
            ps.setString(1,cName);

            int res = ps.executeUpdate();

            if (res >0){
                getCategory();
                categoryNameTF.setText("");
            }

        } catch (SQLException e) {
            customDialog.showAlertBox("Failed...","Duplicate value not allow ");
        }finally {
            DBConnection.closeConnection(connection,ps,null);
        }
    }

    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(categoryList.size() * 1.0 / rowsPerPage));
        pagination.setPageCount(totalPage);

        colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                tableView.getItems().indexOf(cellData.getValue()) + 1));
        colCName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        onColumnEdit(colCName,"category_name");

        Callback<TableColumn<CategoryModel, String>, TableCell<CategoryModel, String>>
                cellFactory = (TableColumn<CategoryModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    ImageView iv_delete;
                    String path = "img/icon/";
                    ImageLoader loader = new ImageLoader();

                    iv_delete = new ImageView(loader.load(path+"delete_ic.png"));
                    iv_delete.setFitHeight(17);
                    iv_delete.setFitWidth(17);
                    iv_delete.setPreserveRatio(true);

                    iv_delete.managedProperty().bind(iv_delete.visibleProperty());
                    iv_delete.setVisible(Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase()));

                    iv_delete.setStyle(
                            " -fx-cursor: hand ;"

                                    + "-fx-fill:#ff0000;"
                    );

                    HBox managebtn = new HBox(iv_delete);

                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(iv_delete, new Insets(0, 3, 0, 20));
                    setGraphic(managebtn);
                    setText(null);

                }
            }

        };


        colAction.setCellFactory(cellFactory);

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, categoryList.size());

        int minIndex = Math.min(toIndex, categoryList.size());
        SortedList<CategoryModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(categoryList.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);

    }

    public void enterPress(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER){

            addCategory(null);
        }
    }
}
