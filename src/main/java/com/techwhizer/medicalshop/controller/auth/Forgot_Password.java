package com.techwhizer.medicalshop.controller.auth;


import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Forgot_Password implements Initializable {

    public TextField email_f;
    public VBox verification_container;
    public VBox password_container;
    public Button submit_bn;
    public TextField confirm_password;
    public TextField new_password;
    public Label error_label;
    private Connection connection;
    private Method method;
    private PreparedStatement ps;
    private DBConnection dbConnection;
    private CustomDialog customDialog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();
        password_container.setVisible(false);
        password_container.managedProperty().bind(password_container.visibleProperty());

    }

    public void forgetPassword_bn(ActionEvent actionEvent) {

        String email = email_f.getText();
        String newPassword = new_password.getText();
        String con_Password = confirm_password.getText();


        if (newPassword.isEmpty()) {
            method.show_popup("Enter New Password", new_password);
            return;
        } else if (con_Password.isEmpty()) {
            method.show_popup("Enter Confirm Password", confirm_password);
            return;
        } else if (!newPassword.equals(con_Password)) {
            method.show_popup("Confirm Password do not match", confirm_password);
            return;
        }

        try {

            ps = connection.prepareStatement(new PropertiesLoader().getUpdateProp().getProperty("UPDATE_PASSWORD"));
            ps.setString(1, con_Password);
            ps.setString(2, email);
            int update_result = ps.executeUpdate();

            if (update_result > 0) {
                email_f.setText("");
                new_password.setText("");
                confirm_password.setText("");
                error_label.setVisible(true);
                error_label.setTextFill(Color.GREEN);
                customDialog.showAlertBox("Congratulations🎉🎉🎉", "Successfully Updated");

                Stage stage = new CustomDialog().stage;

                email_f.setText("");
                new_password.setText("");
                confirm_password.setText("");

                if (null == stage) {
                    return;
                }

                if (stage.isShowing()) {

                    stage.close();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void submit(ActionEvent actionEvent) {

        String email = email_f.getText();

        Pattern pattern = Pattern.compile(method.emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (email.isEmpty()) {
            method.show_popup("Enter Valid Email", email_f);
            return;
        } else if (!matcher.matches()) {
            method.show_popup("Enter Valid Email", email_f);
            return;
        }
        try {
            String query = "SELECT email , user_id FROM TBL_USERS WHERE email = ?";
            connection = dbConnection.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {

                int userId = resultSet.getInt("user_id");
                    error_label.setVisible(false);
                    verification_container.setVisible(false);
                    verification_container.managedProperty().bind(verification_container.visibleProperty());
                    password_container.setVisible(true);



            } else {
                customDialog.showAlertBox("Failed", "Invalid Email !");
            }

        } catch (Exception e) {
        }

    }
}
