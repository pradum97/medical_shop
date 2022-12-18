package com.techwhizer.medicalshop.controller.chooser;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.PriceTypeModel;
import com.techwhizer.medicalshop.model.chooserModel.ItemChooserModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class SaleQuantityChooser implements Initializable {
    public Label itemNameL;
    public Label purchasePriceL;
    public Label mrpL;
    public TextField saleRateTf;
    public Label avlQuantity;
    public Label tabPerStripL;
    public TextField pcsTf;
    public TextField stripTf;
    public Label msgTf;
    private CustomDialog customDialog;
    private Method method;
    private DBConnection dbConnection;
    private ItemChooserModel icm;
    private StaticData staticData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        method = new Method();
        dbConnection = new DBConnection();
        staticData = new StaticData();
    }


    public void chooseItem(MouseEvent mouseEvent) {
        customDialog.showFxmlDialog2("chooser/itemChooser.fxml", "SELECT ITEM");
        if (Main.primaryStage.getUserData() instanceof ItemChooserModel icm) {
            if (method.isItemAvailableInStock(icm.getItemId())) {
                avlQuantity.setText(method.getAvailableQty(icm.getItemId()));
                tabPerStripL.setText(String.valueOf(icm.getTabPerStrip()));
                this.icm = icm;
                itemNameL.setText(icm.getItemName());

                PriceTypeModel ptm = method.getLastPrice(icm.getItemId());
                purchasePriceL.setText(method.removeZeroAfterDecimal(ptm.getPurchaseRate()));
                mrpL.setText(String.valueOf(method.removeZeroAfterDecimal(ptm.getMrp())));

                if (ptm.getSaleRate() < 1) {
                    saleRateTf.setText(method.removeZeroAfterDecimal(ptm.getMrp()));
                } else {
                    saleRateTf.setText(method.removeZeroAfterDecimal(ptm.getSaleRate()));
                }

                if (method.isItemAvlInCart(icm.getItemId())){

                    Connection connection = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;

                    try {
                        connection = new DBConnection().getConnection();
                        String qry = "select * from tbl_cart where item_id = ?";
                        ps = connection.prepareStatement(qry);
                        ps.setInt(1, icm.getItemId());
                        rs = ps.executeQuery();
                       if (rs.next()){
                           double mrp = rs.getDouble("mrp");
                           int strip = rs.getInt("strip");
                           int pcs = rs.getInt("pcs");

                           mrpL.setText(method.removeZeroAfterDecimal(mrp));
                           stripTf.setText(method.removeZeroAfterDecimal(strip));
                           pcsTf.setText(method.removeZeroAfterDecimal(pcs));
                           msgTf.setText("Item Already Added");
                       }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } finally {
                        DBConnection.closeConnection(connection, ps, rs);
                    }
                }else {
                    msgTf.setText("");
                }

            } else {
                customDialog.showAlertBox("", "Item stock not available. Please add purchase item");
            }
        }
    }

    public void enterKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            submit(null);
        }
    }

    public void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isShowing()) {
            stage.close();
        }
    }

    public void submit(ActionEvent event) {
        String saleRate = saleRateTf.getText();
        String strip = stripTf.getText();
        String pcs = pcsTf.getText();
        boolean isStripAVl = false, isPcsAvl = false;
        int stripI = 0, pcsI = 0;
        double saleRateD = 0;

        if (null == icm) {
            method.show_popup("Please select item", itemNameL);
            return;
        } else if (strip.isEmpty() && pcs.isEmpty()) {
            method.show_popup("Please enter strip or pcs", pcsTf);
            return;
        } else if (!strip.isEmpty()) {
            try {
                stripI = Integer.parseInt(strip);
            } catch (NumberFormatException e) {
                method.show_popup("Special characters are not allowed here", stripTf);
                return;
            }
            if (pcs.isEmpty()) {
                if (stripI < 1) {
                    method.show_popup("Please enter valid strip", stripTf);
                    return;
                }
            }

        }
        if (!pcs.isEmpty()) {
            try {
                pcsI = Integer.parseInt(pcs);
            } catch (NumberFormatException e) {
                method.show_popup("Special characters are not allowed here", pcsTf);
                return;
            }
            if (strip.isEmpty()) {
                if (pcsI < 1) {
                    method.show_popup("Please enter valid pcs or tab", pcsTf);
                    return;
                }
            }
        }

        if (stripI < 1 && pcsI < 1) {
            method.show_popup("Please enter strip or pcs", pcsTf);
            return;
        } else if (saleRate.isEmpty()) {
            method.show_popup("Please sale rate", saleRateTf);
            return;
        } else if (!saleRate.isEmpty()) {
            try {
                saleRateD = Double.parseDouble(saleRate);
            } catch (NumberFormatException e) {
                method.show_popup("Please enter valid sale rate", saleRateTf);
                return;
            }

            if (saleRateD < 1) {
                method.show_popup("Please enter valid sale rate", saleRateTf);
                return;
            }
        }


        if (!strip.isEmpty() && stripI > 0) {
            String stockUnit = method.getStockUnit(icm.getItemId());
            if (!Objects.equals(stockUnit, "TAB")) {
                customDialog.showAlertBox("", "Strip not available in stock");
                System.out.println(stockUnit);
                return;
            }
            isStripAVl = true;
        }
        if (!pcs.isEmpty() && pcsI > 0) {
            isPcsAvl = true;
        }
        int totalTab = 0;
        int stockQty = method.getQuantity(icm.getItemId());
        if (isStripAVl && isPcsAvl) {
            totalTab = (stripI * icm.getTabPerStrip()) + pcsI;
            if (totalTab > stockQty) {
                customDialog.showAlertBox("", "Quantity not available");
                return;
            }

        } else if (isStripAVl) {
            totalTab = (stripI * icm.getTabPerStrip());
            if (totalTab > stockQty) {
                customDialog.showAlertBox("", "Strip not available");
                return;
            }

        } else {
            totalTab = pcsI;
            if (totalTab > stockQty) {
                customDialog.showAlertBox("", "PCS not available");
                return;
            }
        }

        if (totalTab<1){
            customDialog.showAlertBox("","Please enter strip or pcs");
            return;
        }

        String qry = "";
        if (method.isItemAvlInCart(icm.getItemId())){
            qry = "UPDATE tbl_cart SET ITEM_ID=?, MRP=?,  STRIP=?, PCS  = ? WHERE item_id = "+icm.getItemId();
        }else {
            qry = "INSERT INTO TBL_CART(ITEM_ID, MRP,  STRIP, PCS) VALUES (?,?,?,?)";
        }
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();
            ps = connection.prepareStatement(qry);
            ps.setInt(1, icm.getItemId());
            ps.setDouble(2, saleRateD);
            ps.setInt(3, stripI);
            ps.setInt(4, pcsI);

            int res = ps.executeUpdate();

            if (res > 0) {
                Stage stage = (Stage) stripTf.getScene().getWindow();
                if (null != stage && stage.isShowing()) {
                    stage.close();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, null);
        }


    }

}
