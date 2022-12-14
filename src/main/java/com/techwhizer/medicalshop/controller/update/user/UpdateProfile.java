package com.techwhizer.medicalshop.controller.update.user;


import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.GetUserProfile;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.Role;
import com.techwhizer.medicalshop.model.UserDetails;
import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfile implements Initializable {
    @FXML
    public TextField first_name_f;
    public TextField last_name_f;
    public TextField username_f;
    public TextField phone_f;
    public TextField email_f;
    public ComboBox<String> gender_comboBox;
    public ComboBox<Role> role_combobox;
    public TextArea full_address_f;
    public Button bnCancel;
    public Button bnUpdate;
    public ComboBox<String> combo_accountStatus;
    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private int userId;
    private Properties propUpdate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();
        userId = ((int) Main.primaryStage.getUserData());
        PropertiesLoader propLoader = new PropertiesLoader();
        propUpdate = propLoader.getUpdateProp();
        setUserDetails(userId);
    }

    private void setUserDetails(int userId) {

        GetUserProfile getUserProfile = new GetUserProfile();
        UserDetails userDetails = getUserProfile.getUser(userId);

        if (null == userDetails) {
            customDialog.showAlertBox("Failed", "User Not Find Please Re-Login");
            return;
        }
        first_name_f.setText(userDetails.getFirstName());
        last_name_f.setText(userDetails.getLastName());
        username_f.setText(userDetails.getUsername());
        phone_f.setText(String.valueOf(userDetails.getPhone()));
        email_f.setText(userDetails.getEmail());
        full_address_f.setText(userDetails.getFullAddress());
        gender_comboBox.setItems(method.getGender());
        gender_comboBox.getSelectionModel().select(userDetails.getGender());

        Role role = new Role(userDetails.getRole_id(), userDetails.getRole());

        role_combobox.setItems(method.getRole());
        role_combobox.getSelectionModel().select(role);

        combo_accountStatus.setItems(method.getAccountStatus());
        combo_accountStatus.getSelectionModel().select(userDetails.getAccountStatus());

        combo_accountStatus.setDisable(userDetails.getUserID() == Login.currentlyLogin_Id);
        role_combobox.setDisable(userDetails.getUserID() == Login.currentlyLogin_Id);

    }

    public void update_bn(ActionEvent event) {

        update(event.getSource());
    }

    private void update(Object source) {

        Connection connection = null;
        PreparedStatement ps_insert_data = null;

        String first_name = first_name_f.getText();
        String last_name = last_name_f.getText();
        String username = username_f.getText();
        String phone = phone_f.getText();
        String email = email_f.getText();
        String full_address = full_address_f.getText();

        Pattern pattern = Pattern.compile(new StaticData().emailRegex);

        Matcher matcher = pattern.matcher(email);

        if (first_name.isEmpty()) {
            method.show_popup("Enter First Name", last_name_f);
            return;

        } else if (username.isEmpty()) {
            method.show_popup("Enter Username", username_f);
            return;

        } else if (isExist("LOWER(username )", username)) {
            method.show_popup("USERNAME ALREADY EXISTS", username_f);
            return;

        } else if (phone.isEmpty()) {
            method.show_popup("Enter 10-digit Phone Number", phone_f);
            return;

        }
        long phoneNum;
        try {
            phoneNum = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            customDialog.showAlertBox("Registration Failed ", "Enter 10-digit Phone Number Without Country Code");
            return;
        }
        Pattern phone_pattern = Pattern.compile("^\\d{10}$");
        Matcher phone_matcher = phone_pattern.matcher(phone);

        if (!phone_matcher.matches()) {
            customDialog.showAlertBox("Registration Failed ", "Enter 10-digit Phone Number Without Country Code");
            return;
        } else if (isPhoneExist(phoneNum)) {
            method.show_popup("PHONE NUMBER ALREADY EXISTS", phone_f);
            return;

        } else if (email.isEmpty()) {
            method.show_popup("Enter Valid Email", email_f);
            return;

        } else if (!matcher.matches()) {
            method.show_popup("Enter Valid Email", email_f);
            return;

        } else if (isExist("LOWER(email) ", email)) {
            method.show_popup("EMAIL ALREADY EXISTS", email_f);
            return;

        } else if (null == gender_comboBox.getValue()) {
            method.show_popup("Choose Your Gender", gender_comboBox);
            return;

        } else if (null == role_combobox.getValue()) {
            method.show_popup("Choose role_combobox", role_combobox);
            return;
        } else if (null == full_address_f || null == full_address || full_address.isEmpty()) {
            method.show_popup("Enter Full Address", full_address_f);
            return;
        }

        int role_id = role_combobox.getSelectionModel().getSelectedItem().getRoleId();

        try {

            connection = dbConnection.getConnection();
            ps_insert_data = connection.prepareStatement((propUpdate.getProperty("UPDATE_USER")));
            ps_insert_data.setString(1, first_name);
            ps_insert_data.setString(2, last_name);
            ps_insert_data.setString(3, gender_comboBox.getValue());
            ps_insert_data.setInt(4, role_id);
            ps_insert_data.setString(5, email);
            ps_insert_data.setString(6, username);
            ps_insert_data.setLong(7, phoneNum);
            ps_insert_data.setString(8, full_address);
            ps_insert_data.setInt(9, combo_accountStatus.getSelectionModel().getSelectedIndex());
            ps_insert_data.setInt(10, userId);

            int result = ps_insert_data.executeUpdate();

            if (result > 0) {

                customDialog.showAlertBox("Congratulations 🎉🎉🎉", "Successfully Updated");

                Stage stage = (Stage) ((Node) source).getScene().getWindow();

                if (stage.isShowing()) {

                    stage.close();
                }

            } else {
                customDialog.showAlertBox("", "Updating Failed");
            }
        } catch (SQLException e) {
            customDialog.showAlertBox("Updating Failed", e.getMessage());

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

    public void bnCancel(ActionEvent event) {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (stage.isShowing()) {
            stage.close();
        }
    }

    private boolean isPhoneExist(long phoneNum) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = dbConnection.getConnection();
            String query = "select phone,user_id from tbl_users where phone = ?";

            ps = connection.prepareStatement(query);
            ps.setLong(1, phoneNum);

            rs = ps.executeQuery();

            int uid = 0;

            if (rs.next()) {

                uid = rs.getInt("user_id");
                if (userId == uid) {
                    return false;
                } else {
                    return true;
                }

            } else {
                return false;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    private boolean isExist(String columnName, String value) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = dbConnection.getConnection();
            String query = "select " + columnName + " ,user_id from tbl_users where " + columnName + " = ?";

            ps = connection.prepareStatement(query);
            ps.setString(1, value.toLowerCase());

            rs = ps.executeQuery();

            int uid = 0;

            if (rs.next()) {

                uid = rs.getInt("user_id");
                if (userId == uid) {
                    return false;
                } else {
                    return true;
                }

            } else {
                return false;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public void enterPress(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER) {

            update(event.getSource());
        }
    }
}