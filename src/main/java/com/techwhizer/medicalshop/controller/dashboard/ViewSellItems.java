package com.techwhizer.medicalshop.controller.dashboard;

import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.SaleItemsModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class ViewSellItems implements Initializable {
    private int rowsPerPage = 8;
    public TableColumn<SaleItemsModel, Integer> col_sno;
    public TableColumn<SaleItemsModel, String> colProductName;
    public TableColumn<SaleItemsModel, String> colMrp;
    public TableColumn<SaleItemsModel, String> colQuantity;
    public TableColumn<SaleItemsModel, String> colHsnSac;
    public TableColumn<SaleItemsModel, String> colTax;
    public TableColumn<SaleItemsModel, String> colTaxAmount;
    public TableColumn<SaleItemsModel, String> colNetAmount;
    public TableColumn<SaleItemsModel, String> colDate;
    public TableView<SaleItemsModel> saleTableView;
    public Pagination pagination;
    private Method method;
    private DBConnection dbConnection;
    private int sale_main_id = 0;
    private Properties propRead;
    private ObservableList<SaleItemsModel> reportList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConnection = new DBConnection();
        PropertiesLoader propLoader = new PropertiesLoader();
        propRead = propLoader.getReadProp();
        sale_main_id = (int) Main.primaryStage.getUserData();
        method = new Method();
        getSaleItem();
    }

    private void getSaleItem() {
        if (null != reportList) {
            reportList.clear();
        }
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = dbConnection.getConnection();
            String qry = """
                    select (TO_CHAR(sale_date, 'DD-MM-YYYY HH12:MI:SS AM')) as sale_date,ts.quantity_unit,tsi.strip_tab
                            ,((coalesce(tsi.strip,0)*coalesce(tsi.strip_tab,0))+tsi.pcs) as totalTab,
                           ts.quantity_unit,tim.item_id,tsi.sale_item_id,tsi.sale_item_id, tsi.item_name, tsi.sale_rate, tsi.discount, tsi.hsn_sac,
                           tsi.igst, tsi.sgst, tsi.cgst, tsi.net_amount, tsi.sale_date, tsi.tax_amount
                    from tbl_sale_items tsi
                             left join tbl_items_master tim on tim.item_id = tsi.item_id
                             left join tbl_stock ts on tsi.stock_id = ts.stock_id
                    where sale_main_id = ? order by sale_item_id asc """;

            ps = connection.prepareStatement(qry);
            ps.setInt(1, sale_main_id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int saleItemId = rs.getInt("sale_item_id");
                int productId = rs.getInt("item_id");

                String productName = rs.getString("item_name");
                double sale_rate = rs.getDouble("sale_rate");
                double taxAmount = rs.getDouble("tax_amount");
                double netAmount = rs.getDouble("net_amount");
                int hsn = rs.getInt("hsn_sac");
                int igst = rs.getInt("igst");
                int sgst = rs.getInt("sgst");
                int cgst = rs.getInt("cgst");
                String saleDate = rs.getString("sale_date");
                String quantity_unit = rs.getString("quantity_unit");
                int tax = igst + cgst + sgst;
                int totalTab = rs.getInt("totalTab");
                int stripTab = rs.getInt("strip_tab");

                String qty = method.tabToStrip(totalTab,stripTab,quantity_unit);;

                reportList.add(new SaleItemsModel(saleItemId, productId, productName, sale_rate,
                        taxAmount, netAmount, qty, hsn, tax, igst, cgst, sgst, saleDate));

            }
            if (null != reportList) {
                if (reportList.size() > 0) {

                    pagination.setVisible(true);
                    pagination.setCurrentPageIndex(0);
                    changeTableView(0, rowsPerPage);
                    pagination.currentPageIndexProperty().addListener((observable1, oldValue1, newValue1) -> changeTableView(newValue1.intValue(), rowsPerPage));
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(reportList.size() * 1.0 / rowsPerPage));
        pagination.setPageCount(totalPage);

        col_sno.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(saleTableView.getItems().indexOf(cellData.getValue()) + 1));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colMrp.setCellValueFactory(new PropertyValueFactory<>("mrp"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colHsnSac.setCellValueFactory(new PropertyValueFactory<>("hsn"));
        colTax.setCellValueFactory(new PropertyValueFactory<>("tax"));
        colTaxAmount.setCellValueFactory(new PropertyValueFactory<>("taxAmount"));
        colNetAmount.setCellValueFactory(new PropertyValueFactory<>("netAmount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("sellingDate"));

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, reportList.size());

        int minIndex = Math.min(toIndex, reportList.size());
        SortedList<SaleItemsModel> sortedData = new SortedList<>(FXCollections.observableArrayList(reportList.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(saleTableView.comparatorProperty());

        saleTableView.setItems(sortedData);
        customColumn(colProductName);
        customColumn(colDate);

    }

    private void customColumn(TableColumn<SaleItemsModel, String> columnName) {

        columnName.setCellFactory(tc -> {
            TableCell<SaleItemsModel, String> cell = new TableCell<>();
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
}
