package com.techwhizer.medicalshop.controller.chooser;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.DealerModel;
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
import javafx.scene.input.MouseEvent;
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

public class DealerChooser implements Initializable {
    public ProgressIndicator progressBar;
    private int rowsPerPage = 7;
    public TextField searchTf;
    public TableColumn<DealerModel, Integer> colSrNo;
    public TableColumn<DealerModel, String> colDealerName;
    public TableColumn<DealerModel, String> colAction;
    public TableView<DealerModel> tableView;
    public Pagination pagination;
    private Method method;
    private CustomDialog customDialog;
    private DBConnection dbConnection;
    private ObservableList<DealerModel> dealerList = FXCollections.observableArrayList();
    private FilteredList<DealerModel> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();

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
            //Background Thread will start
            method.hideElement(tableView);
            msg = "";
        }

        @Override
        public Boolean doInBackground(String... params) {
            /* Background Thread is running */

            Map<String, Object> status = getItems();
            msg = (String) status.get("message");

            return (boolean) status.get("is_success");
        }

        @Override
        public void onPostExecute(Boolean success) {
            tableView.setVisible(true);
            method.hideElement(progressBar);
            tableView.setPlaceholder(new Label(msg));
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private Map<String, Object> getItems() {

        if (null != dealerList) {
            dealerList.clear();
        }
        Map<String, Object> map = new HashMap<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = dbConnection.getConnection();

            String qry = "SELECT * ,(TO_CHAR(ADDED_DATE, 'DD-MM-YYYY')) as date FROM tbl_dealer order by dealer_id asc";
            ps = connection.prepareStatement(qry);
            rs = ps.executeQuery();

            int count = 0;

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
                count++;
                dealerList.add(new DealerModel(dealerId, dealerName, dealerPhone, dealerEmail,
                        dealerGstNum, dealerAddress, dealerState, date, dealerDl));
            }

            if (dealerList.size() > 0) {
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

        filteredData = new FilteredList<>(dealerList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(products -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (products.getDealerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (products.getDealerPhone().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (products.getDealerState().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return String.valueOf(products.getDealerEmail()).toLowerCase().contains(lowerCaseFilter);
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
        colDealerName.setCellValueFactory(new PropertyValueFactory<>("dealerName"));

        setOptionalCell();
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, dealerList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<DealerModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
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

                    Button selectBn = new Button();

                    ImageView iv = new ImageView(new ImageLoader().load("img/icon/rightArrow_ic_white.png"));
                    iv.setFitHeight(17);
                    iv.setFitWidth(17);

                    selectBn.setGraphic(iv);
                    selectBn.setStyle("-fx-cursor: hand ; -fx-background-color: #06a5c1 ; -fx-background-radius: 3 ");

                    selectBn.setOnAction((event) -> {
                        method.selectTable(getIndex(), tableView);
                        DealerModel icm = tableView.getSelectionModel().getSelectedItem();

                        if (null != icm) {
                            Main.primaryStage.setUserData(icm);
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

    public void addDealer(MouseEvent actionEvent) {
        customDialog.showFxmlDialog("product/dealer/addDealer.fxml", "Add new dealer");
        searchTf.setText("");
        callThread();

    }
}
