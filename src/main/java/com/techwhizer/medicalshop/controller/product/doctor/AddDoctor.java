package com.techwhizer.medicalshop.controller.product.doctor;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddDoctor implements Initializable {
    public TextField nameTf;
    public TextField phoneTf;
    public TextField addressTf;
    public TextField regNoTf;
    public TextField specialityTf;
    public TextField qualificationTf;
    public Button submitBn;
    private Method method;
    private CustomDialog customDialog;
    private DBConnection dbConnection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();
    }

    public void cancelBn(ActionEvent event) {
        Stage stage = (Stage) submitBn.getScene().getWindow();
        if (null != stage && stage.isShowing()) {
            stage.close();
        }
    }

    public void submitBn(ActionEvent event) {

        String name = nameTf.getText();
        String phone = phoneTf.getText();
        String address = addressTf.getText();
        String regNo = regNoTf.getText();
        String speciality = specialityTf.getText();
        String qlf = qualificationTf.getText();

        if (name.isEmpty()) {
            method.show_popup("Please Enter doctor name", nameTf);
            return;
        }

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();
            String query = """
                    INSERT INTO TBL_DOCTOR(DR_NAME, DR_PHONE, DR_ADDRESS, DR_REG_NUM, SPECIALITY,
                     QUALIFICATION) VALUES (?,?,?,?,?,?)
                    """;
            ps = connection.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setString(4, regNo);
            ps.setString(5, speciality);
            ps.setString(6, qlf);

            int res = ps.executeUpdate();

            if (res > 0) {
                customDialog.showAlertBox("Success", "Successfully created");
                cancelBn(null);
                nameTf.setText("");
                phoneTf.setText("");
                addressTf.setText("");
                specialityTf.setText("");
                regNoTf.setText("");
                qualificationTf.setText("");
            }


        } catch (SQLException e) {
            customDialog.showAlertBox("Failed", "Something went wrong. Please try again");
            throw new RuntimeException(e);
        }finally {
            DBConnection.closeConnection(connection,ps,null);
        }


    }

    public void keyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            submitBn(null);
        }
    }
}
