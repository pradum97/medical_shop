package com.techwhizer.medicalshop.controller.dashboard;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.SaleMainModel;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SaleReport implements Initializable {
    int rowsPerPage = 12;
    public TableColumn<SaleMainModel, Integer> col_sno;
    public TableColumn<SaleMainModel, String> colCheck;
    public TableColumn<SaleMainModel, String> colPatientName;
    public TableColumn<SaleMainModel, String> colPhone;
    public TableColumn<SaleMainModel, String> colAddress;
    public TableColumn<SaleMainModel, String> colNetAmount;
    public TableColumn<SaleMainModel, String> colSellerName;
    public TableColumn<SaleMainModel, String> colBillType;
    public TableColumn<SaleMainModel, String> colDate;
    public TableColumn<SaleMainModel, String> colAddiDisc;
    public TableColumn<SaleMainModel, String> colTotTax;
    public TableColumn<SaleMainModel, String> colPaymentMode;

    public TextField searchTf;
    public TableView<SaleMainModel> tableView;
    public TableColumn<SaleMainModel, String> colInvoiceNumber;
    public Pagination pagination;
    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    public DatePicker fromDateP;
    public DatePicker toDateP;
    public Button searchReportBn;
    public Label totalNetAmountL;
    private ObservableList<SaleMainModel> reportList = FXCollections.observableArrayList();

    private FilteredList<SaleMainModel> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();
        convertDateFormat(fromDateP, toDateP);
        callThread(false);
    }

    private void callThread(boolean isDateFilter) {

        MyAsyncTask myAsyncTask = new MyAsyncTask(isDateFilter);
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg = "";
        private boolean isDateFilter;

        public MyAsyncTask(boolean isDateFilter) {
            this.isDateFilter = isDateFilter;
        }
        @Override
        public void onPreExecute() {
            if (null != tableView) {
                tableView.setItems(null);
                tableView.refresh();
            }
            assert tableView != null;
            tableView.setPlaceholder(method.getProgressBar(40, 40));
        }

        @Override
        public Boolean doInBackground(String... params) {
            Map<String, Object> status = getSaleItems(isDateFilter);
            boolean isSuccess = (boolean) status.get("is_success");
            msg = (String) status.get("message");
            return isSuccess;
        }

        @Override
        public void onPostExecute(Boolean success) {
            tableView.setPlaceholder(new Label("Item not available"));

        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private Map<String, Object> getSaleItems(boolean isDateFilter) {
        Map<String, Object> map = new HashMap<>();
        if (null != reportList) {
            reportList.clear();
        }
        if (null != tableView) {
            tableView.setItems(null);
            tableView.refresh();
        }

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = dbConnection.getConnection();

            String query = """
                    select tsm.sale_main_id ,tc.patient_id,tsm.seller_id,tsm.additional_discount,
                           tsm.tot_tax_amount,tsm.net_amount,tsm.payment_mode,tsm.invoice_number,
                           tsm.bill_type, (TO_CHAR(tsm.sale_date , 'YYYY-MM-DD HH12:MI:SS AM')) as saleDate,tc.name,tc.phone,tc.address ,
                           tu.first_name,tu.last_name ,(select sum(tsi.net_amount) as netAmount from tbl_sale_items tsi
                                                                       where tsi.sale_main_id = tsm.sale_main_id group by tsm.sale_main_id)
                    from tbl_sale_main tsm
                             LEFT JOIN tbl_patient tc on (tsm.patient_id = tc.patient_id)
                             LEFT JOIN tbl_users tu on (tsm.seller_id = tu.user_id)""";


            if (isDateFilter) {
                String q = query.concat(" where TO_CHAR(tsm.sale_date, 'YYYY-MM-DD') between ? and ? order by sale_main_id desc  ");

                ps = connection.prepareStatement(q);
                ps.setString(1, fromDateP.getValue().toString());
                ps.setString(2, toDateP.getValue().toString());

            } else {
                query = query.concat("  order by sale_main_id desc");
                ps = connection.prepareStatement(query);
            }
            rs = ps.executeQuery();
            double totalNetAmount = 0;

            int res = 0;

            while (rs.next()) {

                int saleMainId = rs.getInt("sale_main_id");
                int patientId = rs.getInt("patient_id");
                int sellerId = rs.getInt("seller_id");
                double additionalDisc = rs.getDouble("additional_discount");
                double totTaxAmt = rs.getDouble("tot_tax_amount");
                double netAmount = rs.getDouble("net_amount");
                String paymentMode = rs.getString("payment_mode");
                String invoiceNumber = rs.getString("invoice_number");
                String billType = rs.getString("bill_type");
                String sale_date = rs.getString("saleDate");
                String patientName = rs.getString("name");
                String patientPhone = rs.getString("phone");
                String patientAddress = rs.getString("addressTf");
                String sellerName = rs.getString("first_name") + " " + rs.getString("last_name");
                double totNetAmount = rs.getDouble("netAmount");

                reportList.add(new SaleMainModel(saleMainId, patientId, sellerId, patientName, patientPhone, patientAddress, additionalDisc,
                        Double.parseDouble(method.decimalFormatter(totTaxAmt)), Double.parseDouble(method.decimalFormatter(netAmount)),
                        paymentMode, billType, invoiceNumber, sellerName, sale_date));

                totalNetAmount = totalNetAmount + totNetAmount;

                res++;

            }

            double finalTotalNetAmount = totalNetAmount;
            Platform.runLater(() -> totalNetAmountL.setText(String.valueOf(Double.parseDouble(method.decimalFormatter(finalTotalNetAmount)))));

            if (reportList.size() > 0) {
                pagination.setVisible(true);
                search_Item();
            } else {
                changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
            }

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

    private void customColumn(TableColumn<SaleMainModel, String> columnName) {

        columnName.setCellFactory(tc -> {
            TableCell<SaleMainModel, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("-fx-font-size: 14");
            cell.setGraphic(text);
            text.setStyle("-fx-text-alignment: CENTER ; -fx-padding: 10");
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(columnName.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    public void bnRefresh(MouseEvent event) {

        callThread(false);
        changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
        setOptional();

        if (null != fromDateP) {
            fromDateP.setValue(null);
        }

        if (null != toDateP) {
            toDateP.setValue(null);
        }
    }

    private void search_Item() {

        filteredData = new FilteredList<>(reportList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(saleMain -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (saleMain.getPatientName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;

                } else if (String.valueOf(saleMain.getPatientPhone()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return saleMain.getInvoiceNumber().toLowerCase().contains(lowerCaseFilter);
            });
            changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
        });

        pagination.setCurrentPageIndex(0);
        changeTableView(0, rowsPerPage);
        pagination.currentPageIndexProperty().addListener(
                (observable1, oldValue1, newValue1) -> changeTableView(newValue1.intValue(), rowsPerPage));
    }

    private void changeTableView(int index, int limit) {

        if (null == filteredData) {
            return;
        }
        int totalPage = (int) (Math.ceil(filteredData.size() * 1.0 / rowsPerPage));
        Platform.runLater(() -> pagination.setPageCount(totalPage));

        col_sno.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                tableView.getItems().indexOf(cellData.getValue()) + 1));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("patientPhone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("patientAddress"));
        colAddiDisc.setCellValueFactory(new PropertyValueFactory<>("additionalDisc"));
        colTotTax.setCellValueFactory(new PropertyValueFactory<>("totalTaxAmount"));
        colNetAmount.setCellValueFactory(new PropertyValueFactory<>("netAmount"));
        colInvoiceNumber.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        colPaymentMode.setCellValueFactory(new PropertyValueFactory<>("paymentMode"));
        colBillType.setCellValueFactory(new PropertyValueFactory<>("billType"));
        colSellerName.setCellValueFactory(new PropertyValueFactory<>("sellerName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("sellingDate"));

        setOptional();

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, reportList.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<SaleMainModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    private void setOptional() {
        Callback<TableColumn<SaleMainModel, String>, TableCell<SaleMainModel, String>>
                checkItems = (TableColumn<SaleMainModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {
                    Label bnChecItem = new Label("ITEMS");

                    bnChecItem.getStyleClass().add("checkItem");
                    bnChecItem.setMinWidth(90);


                    bnChecItem.setOnMouseClicked(event -> {
                        SaleMainModel saleMain = tableView.getSelectionModel().getSelectedItem();
                        if (null == tableView) {
                            return;
                        }

                        Main.primaryStage.setUserData(saleMain.getSale_main_id());
                        customDialog.showFxmlFullDialog("dashboard/viewSellItems.fxml", "All Items - " + saleMain.getPatientName() + " / " + saleMain.getSellingDate() + " / Invoice- " +
                                saleMain.getInvoiceNumber());
                    });

                    HBox container = new HBox(bnChecItem);
                    container.setStyle("-fx-alignment:center");
                    HBox.setMargin(bnChecItem, new Insets(2, 20, 2, 20));
                    setGraphic(container);

                    setText(null);
                }
            }

        };


        colCheck.setCellFactory(checkItems);
        customColumn(colPatientName);
        customColumn(colAddress);
        customColumn(colDate);
    }

    private void convertDateFormat(DatePicker... date) {

        for (DatePicker datePicker : date) {

            datePicker.setConverter(new StringConverter<>() {

                private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                @Override
                public String toString(LocalDate localDate) {
                    if (localDate == null)
                        return "";
                    return dateTimeFormatter.format(localDate);
                }

                @Override
                public LocalDate fromString(String dateString) {
                    if (dateString == null || dateString.trim().isEmpty()) {
                        return null;
                    }
                    return LocalDate.parse(dateString, dateTimeFormatter);
                }
            });
        }


    }

    public void searchReportBn(ActionEvent event) {

        if (null == fromDateP.getValue()) {
            method.show_popup("SELECT START DATE", fromDateP);
            return;
        } else if (null == toDateP.getValue()) {
            method.show_popup("SELECT END DATE", toDateP);
            return;
        }


        callThread(true);

    }
}
