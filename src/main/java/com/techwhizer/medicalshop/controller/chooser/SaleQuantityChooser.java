package com.techwhizer.medicalshop.controller.chooser;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.PriceTypeModel;
import com.techwhizer.medicalshop.model.chooserModel.BatchChooserModel;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class SaleQuantityChooser implements Initializable {
    public Label itemNameL;
    public Label purchasePriceL;
    public Label mrpL, saleRateLabel;
    public TextField saleRateTf;
    public Label avlQuantity;
    public Label tabPerStripL;
    public TextField pcsTf;
    public TextField stripTf;
    public Label msgTf;
    private CustomDialog customDialog;
    private Method method;
    private DBConnection dbConnection;
    private StaticData staticData;
    private  BatchChooserModel bcm ;

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

                if (method.isMultipleItemInStock(icm.getItemId())){

                    Map<String, Object> map = new HashMap<>();
                    map.put("item_id", icm.getItemId());
                    map.put("item_name", icm.getItemName());
                    Main.primaryStage.setUserData(map);
                    customDialog.showFxmlDialog2("chooser/batchChooser.fxml", "SELECT BATCH");

                    if (Main.primaryStage.getUserData() instanceof BatchChooserModel bcm) {
                        this.bcm = bcm;
                    }

                } else {

                    Connection connection = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;

                    try {
                        connection = new DBConnection().getConnection();

                        String qry = """
                                 select  stock_id,tpi.purchase_items_id ,tim.items_name,tim.strip_tab ,tpi.batch , tpi.expiry_date , ts.quantity , ts.quantity_unit  from tbl_stock ts
                                                    left join tbl_purchase_items tpi on tpi.purchase_items_id = ts.purchase_items_id
                                                    left join tbl_items_master tim on tim.item_id = ts.item_id
                                                    where tpi.item_id =?  and ts.quantity>0 order by expiry_date asc
                                """;
                        ps = connection.prepareStatement(qry);
                        ps.setInt(1,icm.getItemId());
                        rs = ps.executeQuery();

                        if (rs.next()){

                            int stockId = rs.getInt("stock_id");
                            String itemName = rs.getString("items_name");
                            String batch = rs.getString("batch");
                            String expiryDate = rs.getString("expiry_date");
                            int quantity = rs.getInt("quantity");
                            int strip_tab = rs.getInt("strip_tab");
                            int purchase_items_id = rs.getInt("purchase_items_id");
                            String quantityUnit = rs.getString("quantity_unit");
                            String qty = method.tabToStrip(quantity, strip_tab, quantityUnit);

                             bcm = new BatchChooserModel(stockId, itemName, batch,
                                    expiryDate, quantity, quantityUnit, qty,strip_tab,purchase_items_id);
                        }



                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }finally {
                        DBConnection.closeConnection(connection,ps,rs);
                    }

                }

                if (null == bcm){
                    return;
                }

                avlQuantity.setText(bcm.getFullQty());
                tabPerStripL.setText(String.valueOf(bcm.getStripTab()));
                itemNameL.setText(bcm.getItemName());
                PriceTypeModel ptm = method.getStockPrice(bcm.getPurchaseItemId());

                purchasePriceL.setText(method.removeZeroAfterDecimal(ptm.getPurchaseRate()));
                mrpL.setText(String.valueOf(method.removeZeroAfterDecimal(ptm.getMrp())));

                if (ptm.getSaleRate() < 1) {
                    saleRateTf.setText(method.removeZeroAfterDecimal(ptm.getMrp()));
                }else {
                    saleRateTf.setText(method.removeZeroAfterDecimal(ptm.getSaleRate()));
                }

                if (method.isItemAvlInCart(bcm.getStockId())) {
                    Connection connection = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;

                    try {
                        connection = new DBConnection().getConnection();
                        String qry = "select * from tbl_cart where stock_id = ?";
                        ps = connection.prepareStatement(qry);
                        ps.setInt(1, bcm.getStockId());
                        rs = ps.executeQuery();
                        if (rs.next()) {
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
                } else {
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

        if (null == bcm) {
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
            String stockUnit = method.getStockUnitStockWise(bcm.getStockId());
            if (!Objects.equals(stockUnit, "TAB")) {
                customDialog.showAlertBox("", "Strip not available in stock. Only available in pcs");
                return;
            }
            isStripAVl = true;
        }
        if (!pcs.isEmpty() && pcsI > 0) {
            isPcsAvl = true;
        }
        int totalTab = 0;
        int stockQty = method.getQuantity(bcm.getStockId());
        if (isStripAVl && isPcsAvl) {
            totalTab = (stripI * bcm.getStripTab()) + pcsI;
            if (totalTab > stockQty) {
                customDialog.showAlertBox("", "Quantity not available");
                return;
            }

        } else if (isStripAVl) {
            totalTab = (stripI *  bcm.getStripTab());
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

        if (totalTab < 1) {
            customDialog.showAlertBox("", "Please enter strip or pcs");
            return;
        }

        String qry = "";
        if (method.isItemAvlInCart(bcm.getStockId())) {
            qry = "UPDATE tbl_cart SET stock_id=?, MRP=?,  STRIP=?, PCS  = ? WHERE stock_id = " + bcm.getStockId();
        } else {
            qry = "INSERT INTO TBL_CART(stock_id, MRP,  STRIP, PCS) VALUES (?,?,?,?)";
        }
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dbConnection.getConnection();
            ps = connection.prepareStatement(qry);
            ps.setInt(1, bcm.getStockId());
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
