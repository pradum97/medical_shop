package com.techwhizer.medicalshop.controller.update.product;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.DiscountModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DiscountUpdate implements Initializable {

    @FXML
    public TextField discountTF;
    public TextArea descriptionTF;
    public Button submitBn;
    public TextField discountNameC;
    private CustomDialog customDialog;
    private Method method;
    private DBConnection dbConnection;
    private DiscountModel discount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        customDialog = new CustomDialog();
        method = new Method();
        dbConnection = new DBConnection();

        discount = (DiscountModel) Main.primaryStage.getUserData();

        setPreviousData(discount);

    }

    private void setPreviousData(DiscountModel discount) {

        discountTF.setText(String.valueOf(discount.getDiscount()));
        descriptionTF.setText(discount.getDescription());
        discountNameC.setText(discount.getDiscountName());
    }

    public void updateBn(ActionEvent event) {

        String discountTf = discountTF.getText();
        String descriptionTf = descriptionTF.getText();
        String discountName = discountNameC.getText();

        if (discountName.isEmpty()) {
            method.show_popup("Enter Discount Name ", discountNameC);
            return;
        } else if (discountTf.isEmpty()) {
            method.show_popup("Enter Discount ", discountTF);
            return;
        }
        int discountD = 0;
        try {
            discountD = Integer.parseInt(discountTf.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (discountD > 100) {
            method.show_popup("Enter Discount Less Than 100 ", discountTF);
            return;
        }


        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();
            if (null == connection) {
                return;
            }

            ps = connection.prepareStatement("UPDATE TBL_DISCOUNT SET DISCOUNT = ?  , DESCRIPTION = ? , DISCOUNT_NAME = ? WHERE DISCOUNT_ID = ?");
            ps.setInt(1, discountD);
            ps.setString(2, descriptionTf);
            ps.setString(3, discountName);
            ps.setInt(4, discount.getDiscount_id());

            int res = ps.executeUpdate();

            if (res > 0) {

                Stage stage = CustomDialog.stage;

                if (stage.isShowing()) {
                    stage.close();
                }
            }


        } catch (SQLException e) {
            customDialog.showAlertBox("Failed","Duplicate Entry Not Allow");
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
            updateBn(null);
        }
    }
}
