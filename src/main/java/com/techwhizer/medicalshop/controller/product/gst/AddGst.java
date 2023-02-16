package com.techwhizer.medicalshop.controller.product.gst;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;

public class AddGst implements Initializable {
    @FXML
    public TextField sgstTF;
    public TextField cgstTF;
    public TextField igstTF;
    public TextField gstNameTF;
    public TextField descriptionTF;
    public TextField hsn_sacTf;

    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();
    }

    public void addTax(ActionEvent event) {

        addGst(event.getSource());
    }

    private boolean isExist(long enterHsnCode) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            connection = dbConnection.getConnection();
            String query = "select HSN_SAC from TBL_PRODUCT_TAX where HSN_SAC = ?";

            ps = connection.prepareStatement(query);
            ps.setLong(1, enterHsnCode);

            rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
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

            addGst(event.getSource());
        }
    }

    private void addGst(Object source) {

        String sgst = sgstTF.getText();
        String cgst = cgstTF.getText();
        String igst = igstTF.getText();
        String gstName = gstNameTF.getText();
        String description = descriptionTF.getText();
        String hsn_sacS = hsn_sacTf.getText();

        if (hsn_sacS.isEmpty()) {
            method.show_popup("Enter HSN / SAC", hsn_sacTf);
            return;
        } else if (sgst.isEmpty()) {
            method.show_popup("Enter sgst", sgstTF);
            return;
        } else if (cgst.isEmpty()) {
            method.show_popup("Enter cgst", cgstTF);
            return;
        } else if (igst.isEmpty()) {
            method.show_popup("Enter igst", igstTF);
            return;
        } else if (gstName.isEmpty()) {
            method.show_popup("Enter Gst Name", gstNameTF);
            return;
        }

        int sGst = 0, cGst = 0, iGst = 0, hsn_sac = 0;

        final String REGEX = "[^0-9.]";

        try {
            hsn_sac = Integer.parseInt(hsn_sacS.replaceAll(REGEX, ""));
        } catch (NumberFormatException e) {
            hsn_sacTf.setText("");
            return;
        }

        if (isExist(hsn_sac)) {
            method.show_popup("THIS HSN CODE IS ALREADY EXIST!", hsn_sacTf);
            return;
        }

        try {
            sGst = Integer.parseInt(sgst.replaceAll(REGEX, ""));
        } catch (NumberFormatException e) {
            customDialog.showAlertBox("Validation Failed", "Please Enter Valid SGST");
            return;
        }

        try {
            iGst = Integer.parseInt(igst.replaceAll(REGEX, ""));
        } catch (NumberFormatException e) {
            customDialog.showAlertBox("Validation Failed", "Please Enter Valid IGST");
            return;
        }

        try {
            cGst = Integer.parseInt(cgst.replaceAll(REGEX, ""));
        } catch (NumberFormatException e) {
            customDialog.showAlertBox("Validation Failed", "Please Enter Valid CGST");
            e.printStackTrace();
            return;
        }

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();

            if (null == connection) {
                return;
            }

            ps = connection.prepareStatement("INSERT INTO tbl_product_tax (HSN_SAC ,SGST, CGST, IGST, gstName ,DESCRIPTION) VALUES(? ,?,?,?,?,?)");
            ps.setInt(1, hsn_sac);
            ps.setDouble(2, sGst);
            ps.setInt(3, cGst);
            ps.setDouble(4, iGst);

            if (gstName.isEmpty()) {
                ps.setNull(5, Types.NULL);
            } else {
                ps.setString(5, gstName);
            }

            if (description.isEmpty()) {
                ps.setNull(6, Types.NULL);
            } else {
                ps.setString(6, description);
            }

            int res = ps.executeUpdate();

            if (res > 0) {

                hsn_sacTf.setText("");
                sgstTF.setText("");
                cgstTF.setText("");
                igstTF.setText("");
                gstNameTF.setText("");
                descriptionTF.setText("");

                Stage stage = (Stage) ((Node) source).getScene().getWindow();
                if (stage.isShowing()) {
                    stage.close();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                if (null != connection) {
                    connection.close();
                }
                if (null != ps) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
