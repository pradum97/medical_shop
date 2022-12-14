package com.techwhizer.medicalshop.controller.product.dealer;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDealer implements Initializable {
    public TextField sNameTf;
    public TextField sPhoneTf;
    public TextField sEmailTf;
    public TextField sGstNumTf;
    public TextField sAddressTf;
    public TextField sStateTf,dlTf;

    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();

    }

    public void bnAddDealer(ActionEvent event) {
       addDealer(event.getSource());
    }

    private void addDealer(Object source) {

        String sName = sNameTf.getText();
        String sPhone = sPhoneTf.getText();
        String sEmail = sEmailTf.getText();
        String sGstNum = sGstNumTf.getText();
        String sAddress = sAddressTf.getText();
        String sState = sStateTf.getText();
        String dl = dlTf.getText();

        Pattern pattern = Pattern.compile(new StaticData().emailRegex);

        Matcher matcher = pattern.matcher(sEmail);

        if (sName.isEmpty()) {
            method.show_popup("Enter Dealer Full Name", sNameTf);
            return;
        }else if (sAddress.isEmpty()) {
            method.show_popup("Enter Dealer Address", sAddressTf);
            return;
        }else if (!sEmail.isEmpty()) {
            if (!matcher.matches()) {
                method.show_popup("Enter Valid Email", sEmailTf);
                return;
            }
        }

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = dbConnection.getConnection();
            if (null == connection) {
                return;
            }

            String query = "INSERT INTO tbl_dealer (DEALER_NAME, DEALER_PHONE, DEALER_EMAIL, DEALER_GSTNO," +
                    " ADDRESS, STATE,dealer_dl) VALUES (?,?,?,?,?,?,?)";
            ps = connection.prepareStatement(query);
            ps.setString(1, sName);
            ps.setString(2, sPhone);

            if (sEmail.isEmpty()) {
                ps.setNull(3, Types.NULL);
            } else {
                ps.setString(3, sEmail);
            }

            if (sGstNum.isEmpty()) {
                ps.setNull(4, Types.NULL);
            } else {
                ps.setString(4, sGstNum);
            }

            ps.setString(5, sAddress);
            ps.setString(6, sState);
            ps.setString(7, dl);



            int res = ps.executeUpdate();

            if (res > 0) {

                Stage stage = (Stage) ((Node) source).getScene().getWindow();

                if (stage.isShowing()) {
                    stage.close();
                }

                customDialog.showAlertBox("success", "Successfully Added");


            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }
    }

    public void cancel(ActionEvent event) {

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        if (stage.isShowing()){
            stage.close();
        }
    }

    public void enterPress(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER){
            addDealer(event.getSource());
        }
    }
}
