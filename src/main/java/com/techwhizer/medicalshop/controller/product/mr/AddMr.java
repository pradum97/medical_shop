package com.techwhizer.medicalshop.controller.product.mr;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddMr implements Initializable {
    public TextField nameTf;
    public ComboBox<String> genderCom;
    public TextField phoneTf;
    public TextField emailTf;
    public TextField companyTf;
    public TextArea addressTf;
    public Button submit;

    private CustomDialog customDialog;
    private Method method;
    private DBConnection dbConnection;
    private StaticData staticData;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();
        staticData = new StaticData();

        genderCom.setItems(staticData.getGender());
    }

    public void cancel(ActionEvent event) {
        Stage stage = (Stage) submit.getScene().getWindow();

        if (null != stage && stage.isShowing()){
            stage.close();
        }
    }

    public void submit(ActionEvent event) {

        String name = nameTf.getText();
        String phone = phoneTf.getText();
        String email = emailTf.getText();
        String company = companyTf.getText();
        String address = addressTf.getText();

        if (name.isEmpty()){
            method.show_popup("Please enter full name of mr",nameTf);
            return;
        }else if (genderCom.getSelectionModel().isEmpty()){
            method.show_popup("Please select gender",genderCom);
            return;
        }else if (phone.isEmpty()) {
            method.show_popup("Please enter 10 digit phone number", phoneTf);
            return;
        }else if (phone.length() != 10) {
            method.show_popup("Please enter 10 digit phone number", phoneTf);
            return;
        }else if (address.isEmpty()) {
            method.show_popup("Please enter address", addressTf);
            return;
        }

        String gender = genderCom.getSelectionModel().getSelectedItem();

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();
            String qry = "insert into tbl_mr_list(name, phone, gender, email, company, address) VALUES (?,?,?,?,?,?)";
            ps = connection.prepareStatement(qry);
            ps.setString(1,name);
            ps.setString(2,phone);
            ps.setString(3,gender);
            ps.setString(4,email);
            ps.setString(5,company);
            ps.setString(6,address);

            int res = ps.executeUpdate();

            if (res>0){
                cancel(null);
            }

        } catch (SQLException e) {
            customDialog.showAlertBox("Failed","Something went wrong..");
            throw new RuntimeException(e);
        }finally {
            DBConnection.closeConnection(connection,ps,null);
        }
    }
}
