package com.techwhizer.medicalshop.controller.auth;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.Role;
import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp implements Initializable {

    @FXML
    public TextField first_name_f;
    public TextField last_name_f;
    public TextField username_f;
    public TextField phone_f;
    public TextField email_f;
    public ComboBox<String> gender_comboBox;
    public ComboBox<Role> role_combobox;
    public TextArea full_address_f;
    public PasswordField password_f;
    public Button submit_bn;
    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private Main main;
    private Properties  propRead;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();
        main = new Main();
        PropertiesLoader propLoader = new PropertiesLoader();
        propRead = propLoader.getReadProp();

        setData();
    }

    private void setData() {
        gender_comboBox.setItems(new StaticData().getGender());
        role_combobox.setItems(method.getRole());

    }

    public void submit_bn(ActionEvent event) {

        startSignup(event.getSource());
    }

    private void clearValue() {

        first_name_f.setText("");
        last_name_f.setText("");
        username_f.setText("");
        phone_f.setText("");
        email_f.setText("");
        full_address_f.setText("");
        password_f.setText("");
    }

    public void enterPress(KeyEvent e) {

        if (e.getCode() == KeyCode.ENTER) {
            //do something

            startSignup(e.getSource());
        }
    }

    private boolean hasSpace(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(0) == ' ' || str.charAt(i) == ' ' && str.charAt(i + 1) != ' ') {
                return true;
            }
        }
        return false;
    }

    private void startSignup(Object source) {

        Connection connection = null;
        PreparedStatement ps_insert_data = null;

        String mac_address = method.get_mac_address();
        String first_name = first_name_f.getText();
        String last_name = last_name_f.getText();
        String username = username_f.getText();
        String phone = phone_f.getText();
        String email = email_f.getText();
        String full_address = full_address_f.getText();
        String password = password_f.getText();

        Pattern pattern = Pattern.compile(new StaticData().emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (first_name.isEmpty()) {
            method.show_popup("Enter First Name", first_name_f);
            return;

        } else if (username.isEmpty()) {
            method.show_popup("Enter Username", username_f);
            return;

        } else if (hasSpace(username)) {
            method.show_popup("SPACE NOT ALLOW PLEASE REMOVE SPACE FROM USERNAME", username_f);
            return;
        } else if (isExist("USERNAME", username)) {
            method.show_popup("USERNAME ALREADY EXISTS!", username_f);
            return;
        } else if (phone.isEmpty()) {
            method.show_popup("Enter 10-digit Phone Number", phone_f);
            return;

        } else if (hasSpace(phone)) {
            method.show_popup("SPACE NOT ALLOW PLEASE REMOVE SPACE FROM PHONE", phone_f);
            return;
        }
        long phoneNum = 0;
        try {
            phoneNum = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            method.show_popup("Enter 10-digit Phone Number Without Country Code", phone_f);
            return;
        }
        if (isExistPhone("PHONE", phoneNum)) {
            method.show_popup("PHONE NUM ALREADY EXISTS!", phone_f);
            return;
        }

        Pattern phone_pattern = Pattern.compile("^\\d{10}$");
        Matcher phone_matcher = phone_pattern.matcher(phone);
        int role_id = role_combobox.getSelectionModel().getSelectedItem().getRoleId();

        if (!phone_matcher.matches()) {
            customDialog.showAlertBox("Registration Failed ", "Enter 10-digit Phone Number Without Country Code");
            return;
        } else if (email.isEmpty()) {
            method.show_popup("Enter Valid Email", email_f);
            return;

        } else if (!matcher.matches()) {
            method.show_popup("Enter Valid Email", email_f);
            return;

        } else if (hasSpace(email)) {
            method.show_popup("SPACE NOT ALLOW PLEASE REMOVE SPACE FROM EMAIL", email_f);
            return;
        } else if (isExist("EMAIL", email)) {
            method.show_popup("EMAIL ALREADY EXISTS!", email_f);
            return;
        } else if (null == gender_comboBox.getValue()) {
            method.show_popup("Choose Your Gender", gender_comboBox);
            return;

        } else if (null == role_combobox.getValue()) {
            method.show_popup("Choose role_combobox", role_combobox);
            return;
        } else if (full_address.isEmpty()) {
            method.show_popup("Enter Full Address", full_address_f);
            return;
        } else if (password.isEmpty()) {
            method.show_popup("Enter Password", password_f);
            return;
        }else if (null == mac_address) {
            mac_address = "Not-Found";
        }

        try {
            connection = dbConnection.getConnection();
            ps_insert_data = connection.prepareStatement(propRead.getProperty("SIGNUP"));

            ps_insert_data.setString(1, first_name);
            ps_insert_data.setString(2, last_name);
            ps_insert_data.setString(3, gender_comboBox.getValue());
            ps_insert_data.setInt(4, role_id);
            ps_insert_data.setString(5, email);
            ps_insert_data.setString(6, username);
            ps_insert_data.setString(7, password);
            ps_insert_data.setLong(8, phoneNum);
            ps_insert_data.setString(9, full_address);
            ps_insert_data.setString(10, mac_address);

            int result = ps_insert_data.executeUpdate();

            if (result > 0) {

                clearValue();
                customDialog.showAlertBox("Congratulations 🎉🎉🎉", "Successfully Registered");

            } else {
                customDialog.showAlertBox("", "Registration Failed");
            }
        } catch (SQLException e) {
            customDialog.showAlertBox("Registration Failed", e.getMessage());
            e.printStackTrace();
        } finally {

            if (null != connection) {
                try {
                    connection.close();
                    if (ps_insert_data != null) {
                        ps_insert_data.close();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isExist(String columnName, String value) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = dbConnection.getConnection();
            String query = "select " + columnName + " from TBL_USERS where " + columnName + " = ?";

            ps = connection.prepareStatement(query);
            ps.setString(1, value);

            rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    private boolean isExistPhone(String columnName, long enterPhoneNum) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = dbConnection.getConnection();
            String query = "select " + columnName + " from TBL_USERS where " + columnName + " = ?";

            ps = connection.prepareStatement(query);
            ps.setLong(1, enterPhoneNum);

            rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public void resetBn(ActionEvent actionEvent) {

        clearValue();
    }
}
