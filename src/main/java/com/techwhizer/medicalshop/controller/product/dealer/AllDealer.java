package com.techwhizer.medicalshop.controller.product.dealer;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.DealerModel;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllDealer implements Initializable {
    public TableView<DealerModel> tableView;
    public TableColumn<DealerModel, Integer> colSrNo;
    public TableColumn<DealerModel, String> colName;
    public TableColumn<DealerModel, String> colPhone;
    public TableColumn<DealerModel, String> colEmail;
    public TableColumn<DealerModel, String> colGstNum;
    public TableColumn<DealerModel, String> colAddress;
    public TableColumn<DealerModel, String> colState;
    public TableColumn<DealerModel, String> colDate;
    public TableColumn<DealerModel, String> colAction;
    public TableColumn<DealerModel, String> colDl;
    public VBox contentContainer;
    public VBox progressBar;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private Method method;

    private ObservableList<DealerModel> supplierList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();
        callThread();
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
            //Background Thread will start
            method.hideElement(contentContainer);
            progressBar.setVisible(true);
        }

        @Override
        public Boolean doInBackground(String... params) {
            /* Background Thread is running */
            Map<String, Object> status = getDealer();
            boolean isSuccess = (boolean) status.get("is_success");
            msg = (String) status.get("message");

            return isSuccess;
        }

        @Override
        public void onPostExecute(Boolean success) {
            method.hideElement(progressBar);
            contentContainer.setVisible(true);
            if (!success) {
                tableView.setPlaceholder(new Label(msg));
                // new CustomDialog().showAlertBox("Failed", msg);
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private Map<String, Object> getDealer() {
        Map<String, Object> map = new HashMap<>();

        if (null != supplierList) {
            supplierList.clear();
        }

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = dbConnection.getConnection();

            String query = "SELECT * ,(TO_CHAR(ADDED_DATE, 'DD-MM-YYYY')) as date FROM tbl_dealer order by dealer_id asc";
            ps = connection.prepareStatement(query);

            rs = ps.executeQuery();

            int res = 0;

            while (rs.next()) {

                int dealerId = rs.getInt("dealer_id");
                String dealerName = rs.getString("dealer_name");
                String dealerPhone = rs.getString("dealer_phone");
                String dealerEmail = rs.getString("dealer_email");
                String dealerGstNum = rs.getString("dealer_gstNo");
                String dealerAddress = rs.getString("ADDRESS");
                String dealerState = rs.getString("STATE");
                String date = rs.getString("date");
                String dealerDl = rs.getString("dealer_dl");

                if (null == dealerName || dealerName.isEmpty()) {
                    dealerName = "-";
                }
                if (null == dealerPhone || dealerPhone.isEmpty()) {
                    dealerPhone = "-";
                }
                if (null == dealerEmail || dealerEmail.isEmpty()) {
                    dealerEmail = "-";
                }
                if (null == dealerAddress || dealerAddress.isEmpty()) {
                    dealerAddress = "-";
                }
                if (null == dealerState || dealerState.isEmpty()) {
                    dealerState = "-";
                }
                if (null == date || date.isEmpty()) {
                    date = "-";
                }
                if (dealerDl.equals("")) {
                    dealerDl = "-";
                }

                if (Objects.equals(dealerGstNum, null) || dealerGstNum.isEmpty()) {
                    dealerGstNum = "-";
                }

                supplierList.add(new DealerModel(dealerId, dealerName, dealerPhone, dealerEmail,
                        dealerGstNum, dealerAddress, dealerState, date, dealerDl));
                res++;
            }


            colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                    tableView.getItems().indexOf(cellData.getValue()) + 1));
            colName.setCellValueFactory(new PropertyValueFactory<>("dealerName"));
            colPhone.setCellValueFactory(new PropertyValueFactory<>("dealerPhone"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("dealerEmail"));
            colGstNum.setCellValueFactory(new PropertyValueFactory<>("dealerGstNum"));
            colAddress.setCellValueFactory(new PropertyValueFactory<>("dealerAddress"));
            colState.setCellValueFactory(new PropertyValueFactory<>("dealerState"));
            colDate.setCellValueFactory(new PropertyValueFactory<>("addedSate"));
            colDl.setCellValueFactory(new PropertyValueFactory<>("dealerDl"));

            onColumnEdit(colName, "dealer_name");
            onColumnEdit(colPhone, "dealer_phone");
            onColumnEdit(colEmail, "dealer_email");
            onColumnEdit(colDl, "dealer_dl");
            onColumnEdit(colGstNum, "dealer_gstNo");
            onColumnEdit(colAddress, "ADDRESS");
            onColumnEdit(colState, "STATE");

            setOptionalCell();
            tableView.setItems(supplierList);

            if (res > 0) {
                map.put("message", "Many items found");
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


    private void onColumnEdit(TableColumn<DealerModel, String> col, String updateColumnName) {

        col.setCellFactory(TextFieldTableCell.forTableColumn());

        col.setOnEditCommit(e -> {

            if (col.equals(colEmail)) {
                String email = e.getNewValue();
                Pattern pattern = Pattern.compile(new StaticData().emailRegex);
                Matcher matcher = pattern.matcher(email);
                if (!email.isEmpty()) {
                    if (!matcher.matches()) {
                        callThread();
                        customDialog.showAlertBox("Failed", "Enter Valid Email Address");
                        return;
                    }
                }
            } else {
                String value = e.getNewValue();

                if (col.equals(colName) || col.equals(colPhone) || col.equals(colAddress) || col.equals(colState)) {
                    if (value.isEmpty()) {
                        callThread();
                        customDialog.showAlertBox("Failed", "Empty Value Not Accepted");
                        return;
                    }
                }
            }
            int dealerId = e.getTableView().getItems().get(e.getTablePosition().getRow()).getDealerId();

            update(e.getNewValue(), updateColumnName, dealerId);
        });
    }

    private void update(String newValue, String columnName, int dealerId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = dbConnection.getConnection();

            if (null == connection) {
                return;
            }

            String query = "UPDATE tbl_dealer SET " + columnName + " = ?  where dealer_id = ?";

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

    private void deleteSupplier(DealerModel sm) {

        int sullpierId = sm.getDealerId();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning ");
        alert.setHeaderText("Are You Sure You Want to Delete This Supplier ( " + sm.getDealerName() + " )");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Main.primaryStage);
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
            Connection con = null;
            PreparedStatement ps = null;

            try {

                con = dbConnection.getConnection();

                if (null == con) {
                    return;
                }

                ps = con.prepareStatement("DELETE FROM tbl_dealer WHERE dealer_id = ?");
                ps.setInt(1, sullpierId);

                int res = ps.executeUpdate();

                if (res > 0) {

                    customDialog.showAlertBox("", "Successfully Deleted");
                    alert.close();
                    callThread();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {

                DBConnection.closeConnection(con, ps, null);
            }

        } else {
            alert.close();
        }
    }

    private void setOptionalCell() {


        Callback<TableColumn<DealerModel, String>, TableCell<DealerModel, String>>
                cellFactory = (TableColumn<DealerModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    ImageView ivDelete = new ImageView();

                    ivDelete.setFitWidth(20);
                    ivDelete.setFitHeight(20);

                    ivDelete.setImage(new ImageLoader().load("img/icon/delete_ic.png"));

                    ivDelete.managedProperty().bind(ivDelete.visibleProperty());
                    ivDelete.setVisible(Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase()));

                    HBox managebtn = new HBox(ivDelete);

                    ivDelete.setOnMouseClicked(mouseEvent -> {

                        DealerModel sm = tableView.getSelectionModel().getSelectedItem();

                        deleteSupplier(sm);
                    });

                    ivDelete.setStyle("-fx-cursor: hand");
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(ivDelete, new Insets(2, 10, 2, 5));

                    setGraphic(managebtn);
                    setText(null);
                }
            }

        };

        colAction.setCellFactory(cellFactory);

    }

    public void addSupplier(ActionEvent event) {
        customDialog.showFxmlDialog2("product/dealer/addDealer.fxml", "ADD DEALER");
        callThread();
    }

}
