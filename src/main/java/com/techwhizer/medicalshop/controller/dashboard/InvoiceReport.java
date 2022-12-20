package com.techwhizer.medicalshop.controller.dashboard;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.GenerateInvoice;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.InvoiceModel;
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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class InvoiceReport implements Initializable {
    private final int rowsPerPage = 11;
    public DatePicker fromDateP;
    public DatePicker toDateP;
    public TextField searchTf;
    public ComboBox<String> searchTypeC;
    public TableView<InvoiceModel> tableView;
    public TableColumn<InvoiceModel, Integer> colSrNo;
    public TableColumn<InvoiceModel, String> colCusName;
    public TableColumn<InvoiceModel, String> colCusPhone;
    public TableColumn<InvoiceModel, String> colTotItems;
    public TableColumn<InvoiceModel, String> colBillType;
    public TableColumn<InvoiceModel, String> colDate;
    public TableColumn<InvoiceModel, String> colInvoice;
    public TableColumn<InvoiceModel, String> colAction;
    public Pagination pagination;
    public VBox contentContainer;
    public VBox progressBar;
    private Method method;
    private DBConnection dbConnection;
    private ObservableList<InvoiceModel> invoiceList = FXCollections.observableArrayList();
    private FilteredList<InvoiceModel> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        method = new Method();
        dbConnection = new DBConnection();

        callThread(false);
        comboBoxConfig();
        convertDateFormat(fromDateP, toDateP);

    }

    private void comboBoxConfig() {
        searchTypeC.setItems(FXCollections.observableArrayList("INV", "CUSTOM"));
        searchTypeC.getSelectionModel().selectFirst();

        searchTypeC.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setAlignment(Pos.CENTER);
                    Insets old = getPadding();
                    setPadding(new Insets(old.getTop(), 0, old.getBottom(), 0));
                }
            }
        });

        searchTypeC.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new ListCell<String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            setAlignment(Pos.CENTER);
                            setPadding(new Insets(3, 3, 3, 0));
                        }
                    }
                };
            }
        });
        String searchType = searchTypeC.getSelectionModel().getSelectedItem();

        switch (searchType) {

            case "INV" -> {
                searchTf.setPromptText("Enter invoice number");
            }
            case "CUSTOM" -> {
                searchTf.setPromptText("Enter your value");
            }
        }

        searchTypeC.valueProperty().addListener((observableValue, s, sType) -> {

            switch (sType) {

                case "INV" -> searchTf.setPromptText("Enter invoice number");

                case "CUSTOM" -> searchTf.setPromptText("Enter your value");

            }
            changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
            searchTf.setText("");


        });
    }

    private void callThread(boolean isDateFilter) {

        MyAsyncTask myAsyncTask = new MyAsyncTask(isDateFilter,null);
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg = "";
        private boolean isDateFilter;
        private Map<String, Object> map;
        private Map<String, Object> statusMap;
        private Label button;

        public MyAsyncTask(boolean isDateFilter, Map<String, Object> map) {
            this.isDateFilter = isDateFilter;
            this.map = map;
            if (null != map){
                button = (Label) map.get("button");
            }
        }

        @Override
        public void onPreExecute() {
            //Background Thread will start

            ProgressIndicator pi = new ProgressIndicator();
            pi.indeterminateProperty();
            pi.setPrefHeight(25);
            pi.setPrefWidth(25);
            pi.setStyle("-fx-progress-color: white;");

            if (null != map) {
                button.setGraphic(pi);
            }else {
                method.hideElement(contentContainer);
                progressBar.setVisible(true);
            }
        }

        @Override
        public Boolean doInBackground(String... params) {
            /* Background Thread is running */
            Map<String, Object> status = new HashMap<>();

            if (null != map){
                boolean isReportPrint = (boolean) map.get("isReportPrint");
                boolean isDownloadable =  (boolean) map.get("isDownloadable");
                int saleMainId = (int) map.get("saleMainId");
                String fullPath = (String) map.get("path");
                String billType = (String) map.get("billType");

                switch (billType){
                    case "REGULAR" -> {
                        if (isDownloadable){

                        new GenerateInvoice().regularInvoice(saleMainId, true, fullPath,button);

                        }else if(isReportPrint){
                            new GenerateInvoice().regularInvoice(saleMainId, false, fullPath,button);
                        }
                    }
                    case "GST" -> {

                        /*if (isDownloadable){
                            status =  new GenerateInvoice().gstInvoice(saleMainId, true, fullPath, billType);

                        }else if(isReportPrint){

                            status =  new GenerateInvoice().gstInvoice(saleMainId, false, fullPath, billType);
                        }*/
                    }
                }

            }else {
                status = getSaleItems(isDateFilter);
            }

            status.get("is_success");
            return true;
        }

        @Override
        public void onPostExecute(Boolean success) {
            method.hideElement(progressBar);
            contentContainer.setVisible(true);
            if (!success) {
                tableView.setPlaceholder(new Label(msg));
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private Map<String, Object> getSaleItems(boolean isDateFilter) {
        Map<String, Object> map = new HashMap<>();

        if (null != invoiceList) {
            invoiceList.clear();
        }
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = dbConnection.getConnection();

            String query = "select tp.name, coalesce(tp.phone, '-')as phone,  tsm.bill_type,\n" +
                    "       (TO_CHAR(tsm.sale_date, 'DD-MM-YYYY HH12:MI:SS AM')) as sale_date,tsm.sale_main_id,tsm.invoice_number,\n" +
                    "       (select count(*) from tbl_sale_items tsi where tsm.sale_main_id = tsi.sale_main_id)as totalItem\n" +
                    "from tbl_sale_main tsm left join tbl_patient tp on tsm.patient_id = tp.patient_id";

            if (isDateFilter) {
                String q = query.concat(" where TO_CHAR(tsm.sale_date, 'YYYY-MM-DD') between ? and ? order by sale_main_id asc  ");
                ps = connection.prepareStatement(q);
                ps.setString(1, fromDateP.getValue().toString());
                ps.setString(2, toDateP.getValue().toString());

            } else {
                query = query.concat("  order by sale_main_id desc");
                ps = connection.prepareStatement(query);
            }
            rs = ps.executeQuery();

            int res = 0;
            while (rs.next()) {

                int saleMainId = rs.getInt("sale_main_id");
                String patientName = rs.getString("name");
                String phone = rs.getString("phone");
                String billType = rs.getString("bill_type");
                String saleDate = rs.getString("sale_date");
                String invoiceNum = rs.getString("invoice_number");

                int totalItem = rs.getInt("totalItem");

                invoiceList.add(new InvoiceModel(saleMainId, totalItem,
                        patientName, phone, billType, saleDate, invoiceNum));
                res++;
            }


            if (invoiceList.size() > 0) {
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

    private void search_Item() {

        filteredData = new FilteredList<>(invoiceList, p -> true);

        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(invoice -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = null;
                String searchType = searchTypeC.getSelectionModel().getSelectedItem();
                switch (searchType) {

                    case "INV" -> {
                        lowerCaseFilter = (searchTypeC.getSelectionModel().getSelectedItem() + newValue).toLowerCase();
                    }
                    case "CUSTOM" -> {
                        lowerCaseFilter = newValue.toLowerCase();
                    }
                }

                assert lowerCaseFilter != null;
                if (invoice.getInvoiceNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;

                } else if (invoice.getCustomerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;

                } else if (invoice.getInvoiceDate().toLowerCase().contains(lowerCaseFilter)) {
                    return true;

                } else return invoice.getCustomerPhone().toLowerCase().contains(lowerCaseFilter);
            });

            changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
        });

        pagination.setCurrentPageIndex(0);
        changeTableView(0, rowsPerPage);
        pagination.currentPageIndexProperty().addListener(
                (observable1, oldValue1, newValue1) -> changeTableView(newValue1.intValue(), rowsPerPage));

    }

    private void changeTableView(int index, int limit) {

        Platform.runLater(() -> {
            if (null == filteredData) {
                return;
            }

            int totalPage = (int) (Math.ceil(filteredData.size() * 1.0 / rowsPerPage));
            pagination.setPageCount(totalPage);

            colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                    tableView.getItems().indexOf(cellData.getValue()) + 1));
            colCusName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            colCusPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
            colTotItems.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
            colBillType.setCellValueFactory(new PropertyValueFactory<>("billType"));
            colInvoice.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));

            colDate.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));

            setOptionalCell();

            int fromIndex = index * limit;
            int toIndex = Math.min(fromIndex + limit, invoiceList.size());

            int minIndex = Math.min(toIndex, filteredData.size());
            SortedList<InvoiceModel> sortedData = new SortedList<>(
                    FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());

            tableView.setItems(sortedData);
        });

    }

    private void setOptionalCell() {

        Callback<TableColumn<InvoiceModel, String>, TableCell<InvoiceModel, String>>
                cellFactory = (TableColumn<InvoiceModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    Label bnDownload = new Label("DOWNLOAD");
                    Label bnPrint = new Label("PRINT");

                    bnDownload.setMinWidth(60);
                    bnPrint.setMinWidth(60);
                    bnPrint.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    bnDownload.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                    bnDownload.setStyle("-fx-background-color: #030c3d; -fx-background-radius: 3 ;  " +
                            "-fx-padding: 4 11 4 11 ;-fx-font-family: Arial; -fx-text-fill: white;-fx-font-weight: bold; -fx-alignment: center;-fx-cursor: hand");

                    bnPrint.setStyle("-fx-background-color: #670283; -fx-background-radius: 3 ;-fx-font-weight: bold ;-fx-font-family: Arial; " +
                            "-fx-padding: 4 11 4 11 ; -fx-text-fill: white; -fx-alignment: center;-fx-cursor: hand");
                    ImageView down_iv = new ImageView();
                    ImageView print_iv = new ImageView();

                    String path = "img/icon/";

                    down_iv.setFitHeight(18);
                    down_iv.setFitWidth(18);

                    print_iv.setFitHeight(18);
                    print_iv.setFitWidth(18);

                    ImageLoader loader = new ImageLoader();

                    down_iv.setImage(loader.load(path.concat("download_ic.png")));
                    print_iv.setImage(loader.load(path.concat("print_ic.png")));

                    bnDownload.setGraphic(down_iv);
                    bnPrint.setGraphic(print_iv);

                    bnDownload.setOnMouseClicked(mouseEvent -> {

                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        File selectedPath = directoryChooser.showDialog(Main.primaryStage);

                        if (selectedPath != null) {

                            int saleMainId = invoiceList.get(getIndex()).getSale_main_id();
                            String billType = invoiceList.get(getIndex()).getBillType();
                            String invoiceNmber = invoiceList.get(getIndex()).getInvoiceNumber();

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            LocalDateTime now = LocalDateTime.now();

                            String fileName = invoiceNmber + "_" + dtf.format(now) + ".pdf";

                            String fullPath = selectedPath + "\\" + fileName;

                            Map<String, Object> map = new HashMap<>();
                            map.put("saleMainId",saleMainId);
                            map.put("path",fullPath);
                            map.put("isDownloadable",true);
                            map.put("isReportPrint",false);

                            map.put("button",bnDownload);


                            switch (billType) {

                                case "REGULAR" -> {

                                    map.put("billType","REGULAR");
                                    MyAsyncTask myAsyncTask = new MyAsyncTask(false,map);
                                    myAsyncTask.setDaemon(false);
                                    myAsyncTask.execute();
                                }

                                case "GST" -> {
                                    map.put("billType","GST");
                                    MyAsyncTask myAsyncTask = new MyAsyncTask(false,map);
                                    myAsyncTask.setDaemon(false);
                                    myAsyncTask.execute();
                                    }

                            }

                        }
                    });

                    bnPrint.setOnMouseClicked(mouseEvent -> {
                        int saleMainId = invoiceList.get(getIndex()).getSale_main_id();
                        String billType = invoiceList.get(getIndex()).getBillType();
                        String tempPath = method.getTempFile();
                        Map<String, Object> map = new HashMap<>();
                        map.put("saleMainId",saleMainId);
                        map.put("path",tempPath);
                        map.put("isDownloadable",false);
                        map.put("isReportPrint",true);
                        map.put("button",bnPrint);

                        switch (billType) {

                            case "REGULAR" -> {

                                map.put("billType","REGULAR");
                                MyAsyncTask myAsyncTask = new MyAsyncTask(false,map);
                                myAsyncTask.setDaemon(false);
                                myAsyncTask.execute();
                            }

                            case "GST" -> {
                                map.put("billType","GST");
                                MyAsyncTask myAsyncTask = new MyAsyncTask(false,map);
                                myAsyncTask.setDaemon(false);
                                myAsyncTask.execute();
                            }

                        }
                    });

                    HBox managebtn = new HBox(bnDownload, bnPrint);

                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(bnDownload, new Insets(5, 0, 5, 30));
                    HBox.setMargin(bnPrint, new Insets(0, 3, 0, 20));

                    setGraphic(managebtn);
                    setText(null);
                }
            }

        };

        colAction.setCellFactory(cellFactory);
        customColumn(colCusName);
        customColumn(colDate);
    }

    private void customColumn(TableColumn<InvoiceModel, String> columnName) {

        columnName.setCellFactory(tc -> {
            TableCell<InvoiceModel, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("-fx-font-size: 14");
            cell.setGraphic(text);
            text.setStyle("-fx-text-alignment: CENTER ;");
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(columnName.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    public void searchInvoice(ActionEvent event) {

        if (null == fromDateP.getValue()) {
            method.show_popup("SELECT START DATE", fromDateP);
            return;
        } else if (null == toDateP.getValue()) {
            method.show_popup("SELECT END DATE", toDateP);
            return;
        }
        callThread(true);

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

    public void bnRefresh(MouseEvent mouseEvent) {

        callThread(false);
        changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
        setOptionalCell();

        if (null != fromDateP) {
            fromDateP.setValue(null);
        }

        if (null != toDateP) {
            toDateP.setValue(null);
        }
    }


}
