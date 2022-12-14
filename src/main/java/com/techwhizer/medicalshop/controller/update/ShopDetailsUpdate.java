package com.techwhizer.medicalshop.controller.update;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.ShopModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ResourceBundle;

public class ShopDetailsUpdate implements Initializable {
    public TextField shopNameTf;
    public TextField phone_1Tf;
    public TextField phone_2TF;
    public TextField emailTF;
    public TextArea addressTF;
    public TextField gstNumberTF;
    public Label submit_button;
    public TextField foodLicenceTf;
    public TextField drugLicenceTf;
    private DBConnection dbConnection;
    private Method method;
    private CustomDialog customDialog;
    private boolean fromDashboard;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();


        if (Main.primaryStage.getUserData() instanceof ShopModel sm) {
            setOldValue(sm);
            fromDashboard = false;
        } else {
            fromDashboard = true;
        }

        config();
    }

    private void config() {

        if (method.isShopDetailsAvailable()) {
            submit_button.setText("UPDATE");
        } else {
            submit_button.setText("SUBMIT");
        }
    }

    private void setOldValue(ShopModel shopModelDetails) {

        shopNameTf.setText(shopModelDetails.getShopName());
        phone_1Tf.setText(shopModelDetails.getShopPhone_1());
        phone_2TF.setText(shopModelDetails.getShopPhone_2());
        emailTF.setText(shopModelDetails.getShopEmail());
        addressTF.setText(shopModelDetails.getShopAddress());
        foodLicenceTf.setText(shopModelDetails.getFoodLicence());
        gstNumberTF.setText(shopModelDetails.getGstNumber());
        drugLicenceTf.setText(shopModelDetails.getDrugLicence());
    }

    public void bnUpdate(MouseEvent event) {

        submit(event.getSource());
    }

    private void submit(Object source) {

        String sName = shopNameTf.getText().toUpperCase();
        String sPhone_1 = phone_1Tf.getText();
        String sPhone_2 = phone_2TF.getText();
        String sEmail = emailTF.getText();
        String sAddress = addressTF.getText();
        String drugLicence = drugLicenceTf.getText();
        String foodLicence = foodLicenceTf.getText();
        String gstNumber = gstNumberTF.getText();

        if (sName.isEmpty()) {

            method.show_popup("ENTER SHOP NAME", shopNameTf);
            return;
        } else if (sPhone_1.isEmpty()) {

            method.show_popup("ENTER SHOP PHONE-1", phone_1Tf);
            return;
        } else if (sEmail.isEmpty()) {

            method.show_popup("ENTER SHOP EMAIL", emailTF);
            return;
        } else if (sAddress.isEmpty()) {

            method.show_popup("ENTER SHOP FULL ADDRESS", addressTF);
            return;
        }

        if (method.isShopDetailsAvailable()) {
            updateDetails(sName, sPhone_1, sPhone_2, sEmail, sAddress, drugLicence, gstNumber, source, foodLicence);
        } else {
            addDetails(sName, sPhone_1, sPhone_2, sEmail, sAddress, drugLicence, gstNumber, source, foodLicence);
        }
    }

    private void addDetails(String sName, String sPhone_1, String sPhone_2, String sEmail,
                            String sAddress, String drugLicence, String gstNumber, Object source, String foodLicence) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();

            String query = "INSERT INTO tbl_shop_details (SHOP_NAME, shop_phone_1, shop_phone_2,\n" +
                    "                              shop_email, shop_address, shop_drug_licence, shop_gst_number,shop_food_licence) VALUES (?,?,?,?,?,?,?,?)";

            ps = connection.prepareStatement(query);
            ps.setString(1, sName);
            ps.setString(2, sPhone_1);

            if (sPhone_2.isEmpty()) {
                ps.setNull(3, Types.NULL);
            } else {
                ps.setString(3, sPhone_2);
            }

            ps.setString(4, sEmail);
            ps.setString(5, sAddress);

            if (drugLicence.isEmpty()) {
                ps.setNull(6, Types.NULL);
            } else {
                ps.setString(6, drugLicence);
            }

            if (gstNumber.isEmpty()) {
                ps.setNull(7, Types.NULL);
            } else {
                ps.setString(7, gstNumber);
            }

            if (foodLicence.isEmpty()) {
                ps.setNull(8, Types.NULL);
            } else {
                ps.setString(8, foodLicence);
            }

            int res = ps.executeUpdate();

            if (res > 0) {

                if (fromDashboard) {
                    customDialog.showFxmlDialog2("shopDetails.fxml", "SHOP DETAILS");
                } else {
                    customDialog.showAlertBox("Success", "Successfully Added");

                    Stage stage = (Stage) ((Node) source).getScene().getWindow();

                    if (stage.isShowing()) {
                        stage.close();
                    }
                }

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }
    }

    private void updateDetails(String sName, String sPhone_1, String sPhone_2, String sEmail,
                               String sAddress, String prop, String gstNumber, Object source, String foodLicence) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();

            String query = "UPDATE tbl_shop_details SET shop_name = ? , shop_phone_1 = ? , shop_phone_2 = ? , shop_email = ? ,\n" +
                    "                            shop_address = ? , shop_drug_licence = ? , shop_gst_number = ?,shop_food_licence=?";

            ps = connection.prepareStatement(query);
            ps.setString(1, sName);
            ps.setString(2, sPhone_1);

            if (null == sPhone_2 || sPhone_2.isEmpty()) {
                ps.setNull(3, Types.NULL);
            } else {
                ps.setString(3, sPhone_2);
            }

            ps.setString(4, sEmail);
            ps.setString(5, sAddress);

            if (null == prop || prop.isEmpty()) {
                ps.setNull(6, Types.NULL);
            } else {
                ps.setString(6, prop);
            }

            if (null == gstNumber || gstNumber.isEmpty()) {
                ps.setNull(7, Types.NULL);
            } else {
                ps.setString(7, gstNumber);
            }
            if (foodLicence.isEmpty()) {
                ps.setNull(8, Types.NULL);
            } else {
                ps.setString(8, foodLicence);
            }

            int res = ps.executeUpdate();
            if (res > 0) {
                if (fromDashboard) {
                    customDialog.showFxmlDialog2("shopDetails.fxml", "SHOP DETAILS");
                } else {
                    customDialog.showAlertBox("Success", "Successfully updated");

                    Stage stage = (Stage) ((Node) source).getScene().getWindow();

                    if (stage.isShowing()) {
                        stage.close();
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }
    }

    public void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isShowing()) {
            stage.close();
        }
    }

    public void enterPress(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER) {
            submit(event.getSource());
        }
    }
}
