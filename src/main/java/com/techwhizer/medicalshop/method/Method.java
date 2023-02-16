package com.techwhizer.medicalshop.method;

import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.model.*;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Method extends StaticData {

    public ProgressIndicator getProgressBar(double height , double width){
        ProgressIndicator pi = new ProgressIndicator();
        pi.indeterminateProperty();
        pi.setPrefHeight(height);
        pi.setPrefWidth(width);

        return pi;
    }

    public String stripTrailingZeros(Object o) {
        return new BigDecimal(String.valueOf(o)).stripTrailingZeros().toPlainString();
    }

    public ObservableList<CategoryModel> getCategory() {

        ObservableList<CategoryModel> categoryList = FXCollections.observableArrayList();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            ps = connection.prepareStatement("SELECT * FROM tbl_category order by category_id desc");
            rs = ps.executeQuery();

            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                String categoryName = rs.getString("category_name");
                categoryList.add(new CategoryModel(categoryId, categoryName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

        return categoryList;
    }

    public ObservableList<CompanyModel> getCompany() {

        ObservableList<CompanyModel> companyList = FXCollections.observableArrayList();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();

            ps = connection.prepareStatement("SELECT * FROM tbl_company order by company_id desc");
            rs = ps.executeQuery();

            while (rs.next()) {
                int categoryId = rs.getInt("company_id");
                String categoryName = rs.getString("company_name");
                String categoryAddress = rs.getString("company_address");
                String createdDate = rs.getString("created_date");
                companyList.add(new CompanyModel(categoryId, categoryName, categoryAddress, createdDate));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

        return companyList;
    }

    public  Map<String, Object> getDiscount() {

        Map<String, Object> map = new HashMap<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ObservableList<DiscountModel> discountList = FXCollections.observableArrayList();

        try {

            connection = new DBConnection().getConnection();

            if (null == connection) {
                return null;
            }

            ps = connection.prepareStatement("SELECT * FROM TBL_DISCOUNT ORDER BY discount_id ASC");
            rs = ps.executeQuery();

            int res = 0;

            while (rs.next()) {

                // discount
                int discountID = rs.getInt("discount_id");
                int discount = rs.getInt("discount");
                String description = rs.getString("description");
                String discountName = rs.getString("discount_name");


                discountList.addAll(new DiscountModel(discountID, discountName, discount, description));
                res++;

            }

           if (res>0){
               map.put("data",discountList);
               map.put("is_success",true);
               map.put("message","success");
           }else {
               map.put("data",discountList);
               map.put("is_success",true);
               map.put("message","Not Available");
           }

            return map;


        } catch (SQLException e) {
            map.put("data",discountList);
            map.put("is_success", false);
            map.put("message", "Something went wrong...");
            e.printStackTrace();
        } finally {

            DBConnection.closeConnection(connection, ps, rs);
        }

        return map;
    }

    public DiscountModel getSpecificDiscount(int discountId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = new DBConnection().getConnection();

            if (null == connection) {
                return null;
            }

            ps = connection.prepareStatement("SELECT * FROM TBL_DISCOUNT WHERE discount_id = ? ORDER BY discount_id ASC");
            ps.setInt(1, discountId);
            rs = ps.executeQuery();
            if (rs.next()) {

                // discount
                int discountID = rs.getInt("discount_id");
                int discount = rs.getInt("discount");
                String description = rs.getString("description");
                String discountName = rs.getString("discount_name");

                return new DiscountModel(discountID, discountName, discount, description);
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;

        } finally {

            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public boolean isExpires(String startDate, String endDate) {

        try {
            SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");

            Date currentDate = sdformat.parse(startDate);
            Date expiresDate = sdformat.parse(endDate);

            int checkExpireDate = currentDate.compareTo(expiresDate);

            return checkExpireDate > 0;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public long countDays(String startDate, String endDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        long days = ChronoUnit.DAYS.between(
                LocalDate.parse(startDate, dateFormat),
                LocalDate.parse(endDate, dateFormat));

        if (days < 0) {
            days = 0;
        }
        return days;
    }

    public void convertDateFormat(DatePicker... date) {
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

    public ObservableList<Role> getRole() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ObservableList<Role> role = FXCollections.observableArrayList();

        try {
            connection = new DBConnection().getConnection();
            if (null == connection) {
                System.out.println(" Signup ( 65 ) : Connection Failed");
                return null;
            }
            ps = connection.prepareStatement(new PropertiesLoader().getReadProp().getProperty("ROLE"));
            rs = ps.executeQuery();

            while (rs.next()) {
                int role_Id = rs.getInt("ROLE_ID");
                String roleName = rs.getString("ROLE");

                role.add(new Role(role_Id, roleName));

            }
            return role;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {

            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public void hideElement(Node node) {
        node.setVisible(false);
        node.managedProperty().bind(node.visibleProperty());
    }

    public ContextMenu show_popup(String message, Object textField) {

        ContextMenu form_Validator = new ContextMenu();
        form_Validator.setAutoHide(true);
        form_Validator.getItems().add(new MenuItem(message));
        form_Validator.show((Node) textField, Side.RIGHT, 10, 0);
        return form_Validator;
    }

    public String getTempFile() {
        try {
            String folderLocation = System.getenv("temp");
            String fileName = "inv.pdf";
            File temp = new File(folderLocation + File.separator + fileName);
            return temp.getAbsolutePath();

        } catch (Exception e) {
            try {
                String tempPath = Files.createTempFile(null, ".pdf").toString();
                return tempPath;
            } catch (IOException ex) {

                String fileName = "inv.pdf";

                String path = System.getProperty("user.home") + "\\invoice\\";
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                    DatabaseBackup.setHiddenAttrib(file);
                }

                String fullPath = path + fileName;

                return fullPath;
            }

        }
    }

    public void openFileInBrowser(String path) {
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(path);
                Desktop.getDesktop().open(myFile);

            } catch (Exception ex) {
                // no application registered for PDFs
            }
        }
    }

    public String removeZeroAfterDecimal(Object o) {
        return new BigDecimal(String.valueOf(o)).stripTrailingZeros().toPlainString();
    }

    public void closeStage(Node node) {

        Stage stage = (Stage) node.getScene().getWindow();
        if (stage.isShowing()) {
            stage.close();
        }
    }

    public void selectTable(int index, TableView tableView) {

        if (!tableView.getSelectionModel().isEmpty()) {
            tableView.getSelectionModel().clearSelection();
        }

        tableView.getSelectionModel().select(index);
    }

    public String get_mac_address() {

        InetAddress ip;
        StringBuilder sb;
        try {

            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            if (null == mac) {
                return "Not-Found";
            } else {
                sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    public UpiModel getUpiDetails() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select * from tbl_upi";
            ps = connection.prepareStatement(qry);
            rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("u_id");
                String upiId = rs.getString("upi_id");
                String payeeName = rs.getString("payee_name");


                return new UpiModel(id, upiId, payeeName);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public boolean isShopDetailsAvailable() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = new DBConnection().getConnection();

            String query = "select * from tbl_shop_details";

            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public String rec(String str){
        String txt = "";

        if (null == str || str.isEmpty()) {
            txt = "-";
        } else {
            txt = str;
        }
        return txt;
    }

    public boolean isItemAvailableInStock(int itemId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select item_id from tbl_stock where item_id = ? and quantity > 0";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, itemId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }
    public boolean isBatchAvailableInStock(String batch) {

        batch = batch.trim();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = """
                    select tpi.batch from tbl_stock ts
                    left join tbl_purchase_items tpi on tpi.purchase_items_id = ts.purchase_items_id
                    where batch = ?
                    """;
            ps = connection.prepareStatement(qry);
            ps.setString(1, batch);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public boolean isMultipleItemInStock(int itemId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = """
                select  count(stock_id) as itemCount  from tbl_stock ts
                left join tbl_purchase_items tpi on tpi.purchase_items_id = ts.purchase_items_id
                where tpi.item_id =?  and ts.quantity>0
                """;
            ps = connection.prepareStatement(qry);
            ps.setInt(1, itemId);
            rs = ps.executeQuery();

            int count = 0;
            if (rs.next()){
                 count = rs.getInt("itemCount");
            }

            return count > 1;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public String getStockUnit(int itemId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select quantity_unit from tbl_stock where item_id = ?";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, itemId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("quantity_unit");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }


    }

    public String getStockUnitStockWise(int stockId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select quantity_unit from tbl_stock where stock_id = ?";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, stockId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("quantity_unit");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }


    }

    public String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public int getQuantity(int stockId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select quantity from tbl_stock where stock_id = ?";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, stockId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public int getTbPerStrip(int itemId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select strip_tab from tbl_items_master where item_id = ?";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, itemId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("strip_tab");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }
    public String getAvailableQty(int itemId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry =
                    "select  ts.quantity,ts.quantity_unit,strip_tab from tbl_items_master tim\n" +
                    "left join tbl_stock ts on tim.item_id = ts.item_id where ts.item_id = ?";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, itemId);
            rs = ps.executeQuery();
            if (rs.next()) {
                double quantity = rs.getDouble("quantity");
                String unit = rs.getString("quantity_unit");
                int tabPerStrip = rs.getInt("strip_tab");

                double tab = 0;
                if (unit.equalsIgnoreCase("strip")) {
                    tab = quantity * tabPerStrip;
                    unit = "TAB";
                } else {
                    tab = quantity;
                }

                return tabToStrip(tab, tabPerStrip, unit);

            } else {
                return "";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }
    public String tabToStrip(double tablet, int stripPerTab, String unitType) {
        String val = "";
        int strip = (int) (tablet / stripPerTab);
        int tab = (int) (tablet % stripPerTab);
        if (unitType.equalsIgnoreCase("tab")) {

            if (strip > 0) {
                val = strip + "-STR";
            }
            if (tab > 0) {
                if (strip > 0) {
                    val = val.concat(",");
                }
                val = val.concat(tab + "-TAB");
            }
        } else {
            val = removeZeroAfterDecimal(tablet) + "-PCS";
        }

        return val;
    }

    public PriceTypeModel getLastPrice(int itemId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PriceTypeModel ptm = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select purchase_rate,mrp,sale_price from tbl_purchase_items where item_id = ? order by purchase_items_id asc";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, itemId);
            rs = ps.executeQuery();
            while (rs.next()) {
                double purchaseRate = rs.getDouble("purchase_rate");
                double mrp = rs.getDouble("mrp");
                double saleRate = rs.getDouble("sale_price");

                if (rs.isLast()) {
                    ptm = new PriceTypeModel(purchaseRate, mrp, saleRate);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

        if (null == ptm) {
            ptm = new PriceTypeModel(0, 0, 0);
        }

        return ptm;
    }

    public PriceTypeModel getStockPrice(int purchaseItemsId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PriceTypeModel ptm = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select purchase_rate,mrp,sale_price from tbl_purchase_items where purchase_items_id = ?";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, purchaseItemsId);
            rs = ps.executeQuery();
            while (rs.next()) {
                double purchaseRate = rs.getDouble("purchase_rate");
                double mrp = rs.getDouble("mrp");
                double saleRate = rs.getDouble("sale_price");

                if (rs.isLast()) {
                    ptm = new PriceTypeModel(purchaseRate, mrp, saleRate);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

        if (null == ptm) {
            ptm = new PriceTypeModel(0, 0, 0);
        }

        return ptm;
    }

    public boolean isItemAvlInCart(int stockId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            String qry = "select stock_id from tbl_cart where stock_id = ?";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, stockId);
            rs = ps.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

    }

    public String decimalFormatter(Object o) {
        DecimalFormat formatter = new DecimalFormat("#0.0");
        return formatter.format(o);
    }

    public MrModel getSpecificMr(int mrId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();

            String qry = "select *,(TO_CHAR(tml.created_date, 'DD-MM-YYYY')) as created_date from tbl_mr_list tml where mr_id = ? order by mr_id desc ";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, mrId);
            rs = ps.executeQuery();

            if (rs.next()) {

                int mr_id = rs.getInt("mr_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String company = rs.getString("company");
                String address = rs.getString("addressTf");
                String createdDate = rs.getString("created_date");
                String gender = rs.getString("gender");

                return new MrModel(mr_id, name, phone, company, email, address, createdDate, gender);
            } else {
                return null;
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public ManufacturerModal getManufacture(int mfrId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();

            String qry = "SELECT * FROM tbl_manufacturer_list where mfr_id = ? order by mfr_id asc";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, mfrId);
            rs = ps.executeQuery();
            if (rs.next()) {

                int id = rs.getInt("mfr_id");
                String manufacturer_name = rs.getString("manufacturer_name");
                String created_date = rs.getString("created_date");
                return new ManufacturerModal(id, manufacturer_name, created_date);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }

    }

    public CompanyModel getSpecificCompany(int companyId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();

            String qry = "SELECT * FROM tbl_company where company_id = ? order by company_id asc";
            ps = connection.prepareStatement(qry);
            ps.setInt(1, companyId);
            rs = ps.executeQuery();

            if (rs.next()) {

                int id = rs.getInt("company_id");
                String company_name = rs.getString("company_name");
                String company_address = rs.getString("company_address");
                String created_date = rs.getString("created_date");
                return new CompanyModel(id, company_name, company_address, created_date);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public GstModel getSpecificGst(int gstId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = new DBConnection().getConnection();

            if (null == connection) {
                return null;
            }

            ps = connection.prepareStatement("SELECT * FROM TBL_PRODUCT_TAX where tax_id = ? ORDER BY tax_id ASC");
            ps.setInt(1, gstId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int taxID = rs.getInt("tax_id");
                int hsn_sac = rs.getInt("hsn_sac");
                int sgst = rs.getInt("sgst");
                int cgst = rs.getInt("cgst");
                int igst = rs.getInt("igst");
                String tax_description = rs.getString("description");
                String gstName = rs.getString("gstName");

                return new GstModel(taxID, hsn_sac, sgst, cgst, igst, gstName, tax_description);
            }else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {

            DBConnection.closeConnection(connection, ps, rs);
        }

    }
}
