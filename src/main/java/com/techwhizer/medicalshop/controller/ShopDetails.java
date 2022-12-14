package com.techwhizer.medicalshop.controller;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.ShopModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class ShopDetails implements Initializable {
    public TextField shopNameTf;
    public TextField phone_1Tf;
    public TextField phone_2TF;
    public TextField emailTF;
    public TextField gstNumberTF;
    public TextField foodLicenceTf;
    public TextField drugLicenceTf;
    public TextArea addressTF;
    public Label submit_button;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private Method method;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();
        method = new Method();

        config();
        setData();
    }

    private void config() {
        method.hideElement(submit_button);
        submit_button.setVisible(Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase()));
    }

    private void setData() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = dbConnection.getConnection();

            String query = "select * from tbl_shop_details";

            ps = connection.prepareStatement(query);

            rs = ps.executeQuery();

            while (rs.next()) {

                String shop_name = rs.getString("shop_name");
                String phone_1 = rs.getString("shop_phone_1");
                String phone_2 = rs.getString("shop_phone_2");
                String email = rs.getString("shop_email");
                String gstNum = rs.getString("shop_gst_number");
                String address = rs.getString("shop_address");
                String drugLicence = rs.getString("shop_drug_licence");
                String foodLicence = rs.getString("shop_food_licence");

                shopNameTf.setText(shop_name);
                phone_1Tf.setText(phone_1);
                phone_2TF.setText(phone_2);
                emailTF.setText(email);
                gstNumberTF.setText(gstNum);
                addressTF.setText(address);
                drugLicenceTf.setText(drugLicence);
                foodLicenceTf.setText(foodLicence);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public void bnUpdate(MouseEvent event) {

        String sName = shopNameTf.getText();
        String phone_1 = phone_1Tf.getText();
        String phone_2 = phone_2TF.getText();
        String email = emailTF.getText();
        String address = addressTF.getText();
        String gstNum = gstNumberTF.getText();
        String drugLicence = drugLicenceTf.getText();
        String foodLicence = foodLicenceTf.getText();

        ShopModel shopModel = new ShopModel(sName , phone_1 , phone_2 , email , address ,gstNum,foodLicence,drugLicence);
        Main.primaryStage.setUserData(shopModel);

        customDialog.showFxmlDialog("update/shopDetailsUpdate.fxml", "UPDATE SHOP DETAILS");
        setData();
        config();
    }

    public void cancel(ActionEvent event) {

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        if (stage.isShowing()){
            stage.close();
        }
    }
}
