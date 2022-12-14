package com.techwhizer.medicalshop.method;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.model.*;
import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.util.AppConfig;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Method extends StaticData {

    public ProgressIndicator getProgressBar(double height , double width){
        ProgressIndicator pi = new ProgressIndicator();
        pi.indeterminateProperty();
        pi.setPrefHeight(height);
        pi.setPrefWidth(width);

        return pi;
    }

    public void showAddProductDialog() {

        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(CustomDialog.class.getResource("product/addProduct.fxml")));
            Stage stage = new Stage();
            stage.getIcons().add(new ImageLoader().load(AppConfig.APPLICATION_ICON));
            stage.setTitle("Add new product");
            stage.setMaximized(false);
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Main.primaryStage);
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

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
            map.put("is_success",false);
            map.put("message","Something went wrong...");
            e.printStackTrace();
        } finally {

            DBConnection.closeConnection(connection, ps, rs);
        }

        return map;
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

        if (null == str || str.isEmpty()){
            txt = "-";
        }else {
            txt = str;
        }
        return txt;
    }

}
