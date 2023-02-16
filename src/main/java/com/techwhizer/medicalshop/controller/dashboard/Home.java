package com.techwhizer.medicalshop.controller.dashboard;

import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.DailySaleReport;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Home implements Initializable {
    int rowsPerPage = 15;
    public TableColumn<DailySaleReport, String> colTotalItem;
    public TableColumn<DailySaleReport, String> colNetAmount;
    public Label totalNetAmountL;
    public TableColumn<DailySaleReport, String> colItemName;
    public TableColumn<DailySaleReport, String> colQuantity;
    public TableColumn<DailySaleReport, String> colBatch;
    public TableColumn<DailySaleReport, String> colExpiryDate;
    public BorderPane mainContainer;
    public TableView<DailySaleReport> tableViewHome;
    public TableColumn<DailySaleReport, Integer> col_sno;

    public HBox refresh_bn;
    public Pagination pagination;
    private DBConnection dbConnection;
    private DecimalFormat df = new DecimalFormat("0.##");
    private Method method;

    private ObservableList<DailySaleReport> reportList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConnection = new DBConnection();
        method = new Method();
        callThread();
    }

    private void callThread() {

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }

    public void bnRefresh(MouseEvent event) {
        if (null == reportList) {
            return;
        }
        callThread();

        changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
    }

    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(reportList.size() * 1.0 / rowsPerPage));
        pagination.setPageCount(totalPage);
        col_sno.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                tableViewHome.getItems().indexOf(cellData.getValue()) + 1));
        colTotalItem.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colNetAmount.setCellValueFactory(new PropertyValueFactory<>("totalNetAmount"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("fullQuantity"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batch"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, reportList.size());

        int minIndex = Math.min(toIndex, reportList.size());
        SortedList<DailySaleReport> sortedData = new SortedList<>(
                FXCollections.observableArrayList(reportList.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableViewHome.comparatorProperty());

        tableViewHome.setItems(sortedData);

    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        public void onPreExecute() {
            //Background Thread will start
           if (null != tableViewHome){
               tableViewHome.setItems(null);
           }
            assert tableViewHome != null;
            tableViewHome.setPlaceholder(method.getProgressBar(40,40));
        }

        @Override
        public Boolean doInBackground(String... params) {

            Map<String, Object> status = getSaleItem();
            return (boolean) status.get("is_success");
        }

        @Override
        public void onPostExecute(Boolean success) {
           tableViewHome.setPlaceholder(new Label("Item not available"));
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private Map<String, Object> getSaleItem() {

        if (null != reportList) {
            reportList.clear();
        }

        if (null!=tableViewHome){
            tableViewHome.setItems(null);
            tableViewHome.refresh();
        }
        Map<String, Object> map = new HashMap<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = dbConnection.getConnection();

            String query = """
                 select  tsi.item_name ,tsi.stock_id,tsi.batch,
                         count(*)as total_Item  ,tsi.expiry_date,
                         sum(net_amount) as total_Net_Amount,
                         ( sum(tsi.strip*(select strip_tab from tbl_items_master tim where tsi.item_id = tim.item_id )))+ ( sum(tsi.pcs))as totalTab,
                         (select quantity_unit from tbl_stock ts where tsi.stock_id = ts.stock_id) as qtyUnit,
                         tsi.strip_tab as stripTab
                 from tbl_sale_Items tsi
                 where TO_CHAR(sale_date, 'yyyy-MM-dd' ) =
                       TO_CHAR(CURRENT_DATE, 'yyyy-MM-dd')   group by stock_id,tsi.batch, tsi.item_name, tsi.strip_tab, tsi.expiry_date
                    """;

            ps = connection.prepareStatement(query);

            rs = ps.executeQuery();

            double totalNetAmount = 0;

            int res = 0;

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                int stockId = rs.getInt("stock_id");
                int totalItem = rs.getInt("total_Item");
                double totalNet_Amount = rs.getDouble("total_Net_Amount");
                int totalTab = rs.getInt("totalTab");
                String qtyUnit = rs.getString("qtyUnit");
                String batch  = rs.getString("batch");
                String expiryDate = rs.getString("expiry_date");
                int stripTab = rs.getInt("stripTab");
                String qty = method.tabToStrip(totalTab,stripTab,qtyUnit);;

                if (stripTab > 0 && qtyUnit.equalsIgnoreCase("tab")) {
                    itemName = itemName.concat(" ( STRIP-"+stripTab+" )");
                }

                reportList.add(new DailySaleReport(0 ,totalItem, itemName, totalNet_Amount,totalTab,qtyUnit,qty,stripTab,batch,stockId,expiryDate));
                totalNetAmount = totalNetAmount + totalNet_Amount;
                res++;
            }
            double finalTotalNetAmount = totalNetAmount;
            Platform.runLater(()-> totalNetAmountL.setText(String.valueOf(Double.parseDouble(df.format(finalTotalNetAmount)))));

            if (res > 0){
                map.put("message", "Many items found");
                map.put("is_success", true);
            }else {
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

        if (reportList.size() > 0) {
            pagination.setVisible(true);
            pagination.setCurrentPageIndex(0);
            Platform.runLater(()->changeTableView(0, rowsPerPage));
            pagination.currentPageIndexProperty().addListener(
                    (observable1, oldValue1, newValue1) -> changeTableView(newValue1.intValue(), rowsPerPage));
        }

        return map;
    }
}
