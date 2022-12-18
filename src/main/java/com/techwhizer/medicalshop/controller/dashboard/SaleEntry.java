package com.techwhizer.medicalshop.controller.dashboard;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.GenerateBillNumber;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.model.PatientModel;
import com.techwhizer.medicalshop.model.SaleEntryModel;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class SaleEntry implements Initializable {
    public Label patientNameL;
    public Label addItem;
    public TableView<SaleEntryModel> tableView;
    public TableColumn<SaleEntryModel, Integer> colSrNo;
    public TableColumn<SaleEntryModel, String> colProductName;
    public TableColumn<SaleEntryModel, String> colMrp;
    public TableColumn<SaleEntryModel, String> colPack;
    public TableColumn<SaleEntryModel, String> colStrip;
    public TableColumn<SaleEntryModel, String> colPcs;
    public TableColumn<SaleEntryModel, String> colExpiryDate;
    public TableColumn<SaleEntryModel, String> colDiscount;
    public TableColumn<SaleEntryModel, String> colGst;
    public TableColumn<SaleEntryModel, String> colAmount;

    public TableColumn<SaleEntryModel, String> colAction;
    public Button saleButton;
    public Label totAmountL;
    public Label totDisAmountL;
    public Label totGstAmountL;
    public Label invoiceValueTf;
    public VBox paymentModeContainer;
    public ComboBox<String> paymentModeC;
    public ComboBox<String> billingTypeC;
    public VBox gstContainer;
    public Label taxTitleL;
    public TextField addDiscTF;
    public VBox amountContainer;
    public Button checkOutButton;
    public HBox progressBar;
    private CustomDialog customDialog;
    private Method method;
    private DBConnection dbConnection;
    private StaticData staticData;
    private ObservableList<SaleEntryModel> itemList = FXCollections.observableArrayList();
    private double totGstAmount = 0, totalAmount = 0, totalDiscount = 0;
    private PatientModel patientModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();
        method = new Method();
        staticData = new StaticData();
        dbConnection = new DBConnection();
        method.hideElement(progressBar);
        setData();
        refresh();

    }

    private void setData() {
        paymentModeC.setItems(staticData.getPaymentMode());
        billingTypeC.setItems(staticData.getBillingType());
        paymentModeC.getSelectionModel().select("CASH");
        billingTypeC.getSelectionModel().select("REGULAR");
    }

    private void setTableDate() {
        tableView.refresh();
        tableView.setItems(itemList);

        colSrNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(cellData.getValue()) + 1));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPack.setCellValueFactory(new PropertyValueFactory<>("pack"));
        colStrip.setCellValueFactory(new PropertyValueFactory<>("strip"));
        colPcs.setCellValueFactory(new PropertyValueFactory<>("pcs"));
        colMrp.setCellValueFactory(new PropertyValueFactory<>("saleRate"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colGst.setCellValueFactory(new PropertyValueFactory<>("totalGst"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        setOptionalCell();

        double finalTotalAmount = totalAmount;
        double finalTotGstAmount = totGstAmount;
        double finalTotalDiscount = totalDiscount;
        Platform.runLater(() -> {
            totAmountL.setText(method.decimalFormatter(finalTotalAmount));
            totGstAmountL.setText(method.decimalFormatter(finalTotGstAmount));
            totDisAmountL.setText(method.decimalFormatter(finalTotalDiscount));
            invoiceValueTf.setText(method.removeZeroAfterDecimal(Math.round((finalTotalAmount - finalTotalDiscount))));
        });
    }

    private void setOptionalCell() {


        Callback<TableColumn<SaleEntryModel, String>, TableCell<SaleEntryModel, String>>
                cellFactory = (TableColumn<SaleEntryModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    Button selectBn = new Button();

                    ImageView iv = new ImageView(new ImageLoader().load("img/icon/delete_ic_white.png"));
                    iv.setFitHeight(17);
                    iv.setFitWidth(17);

                    selectBn.setGraphic(iv);
                    selectBn.setStyle("-fx-cursor: hand ; -fx-background-color: #c1061c ; -fx-background-radius: 3 ");

                    selectBn.setOnAction((event) -> {
                        method.selectTable(getIndex(), tableView);
                        SaleEntryModel sem = tableView.getSelectionModel().getSelectedItem();
                        removeItem(sem.getItemId());

                    });

                    HBox managebtn = new HBox(selectBn);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(selectBn, new Insets(0, 0, 0, 0));

                    setGraphic(managebtn);

                    setText(null);

                }
            }

        };

        colAction.setCellFactory(cellFactory);
    }

    private void getCartData() {

        if (null != itemList) {
            itemList.clear();
        }
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        totGstAmount = 0;
        totalAmount = 0;
        totalDiscount = 0;

        try {
            connection = dbConnection.getConnection();
            String qry = """
                    select tim.items_name,tc.item_id ,tpt.hsn_sac as hsn,tpt.igst,tpt.sgst,tpt.cgst, tim.packing , tc.mrp as sale_rate , tc.strip,tc.pcs,
                           (select expiry_date from tbl_purchase_items tpi where ts.purchase_items_id = tpi.purchase_items_id),t.purchase_rate,t.mrp,
                           coalesce(td.discount,0) as discount,td.discount_id,tpt.tax_id,
                           coalesce((coalesce(tpt.sgst,0)+coalesce(tpt.cgst,0)+coalesce(tpt.igst,0)),0)as total_gst,
                           ((coalesce(tc.strip,0)*coalesce(tim.strip_tab,0))+coalesce(tc.pcs,0))*tc.mrp as amount,
                           ( ((coalesce(tc.strip,0)*coalesce(tim.strip_tab,0))+coalesce(tc.pcs,0))*tc.mrp)*coalesce(td.discount,0)/100 as discountAmount,
                           ( coalesce( ( ( ((coalesce(tc.strip,0)*coalesce(tim.strip_tab,0))+coalesce(tc.pcs,0))*tc.mrp)-
                                         ( ((coalesce(tc.strip,0)*coalesce(tim.strip_tab,0))+coalesce(tc.pcs,0))*tc.mrp)*coalesce(td.discount,0)/100),0)*100)/
                           (100+(coalesce((coalesce(tpt.sgst,0)+coalesce(tpt.cgst,0)+coalesce(tpt.igst,0)),0))) as taxableAmount,
                           ( coalesce( ( ( ((coalesce(tc.strip,0)*coalesce(tim.strip_tab,0))+coalesce(tc.pcs,0))*tc.mrp)-
                                         ( ((coalesce(tc.strip,0)*coalesce(tim.strip_tab,0))+coalesce(tc.pcs,0))*tc.mrp)*coalesce(td.discount,0)/100),0)*100)/
                           (100+(coalesce((coalesce(tpt.sgst,0)+coalesce(tpt.cgst,0)+coalesce(tpt.igst,0)),0)))*coalesce((coalesce(tpt.sgst,0)+coalesce(tpt.cgst,0)+coalesce(tpt.igst,0)),0)/100 as gstAmount
                    from tbl_cart tc
                             left join tbl_items_master tim on tc.item_id = tim.item_id
                             left join tbl_stock ts on tim.item_id = ts.item_id
                             left join tbl_discount td on tim.discount_id = td.discount_id
                             left join tbl_product_tax tpt on tpt.tax_id = tim.gst_id
                             left outer join tbl_purchase_items t on ts.purchase_items_id = t.purchase_items_id""";
            ps = connection.prepareStatement(qry);
            rs = ps.executeQuery();

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String productName = rs.getString("items_name");
                double saleRate = rs.getDouble("sale_rate");
                String pack = rs.getString("packing");
                int strip = rs.getInt("strip");
                int pcs = rs.getInt("pcs");
                String expiryDate = rs.getString("expiry_date");
                int discountId = rs.getInt("discount_id");
                double discount = rs.getDouble("discount");
                int gstId = rs.getInt("tax_id");
                double totalGst = rs.getInt("total_gst");
                double amount = rs.getInt("amount");
                double taxableAmt = rs.getDouble("taxableAmount");
                double discountAmt = rs.getDouble("discountAmount");
                double gstAmount = rs.getDouble("gstAmount");
                long hsn = rs.getLong("hsn");
                int iGst = rs.getInt("igst");
                int cGst = rs.getInt("cgst");
                int sGst = rs.getInt("sgst");
                double purchaseRate = rs.getDouble("purchase_rate");
                double mrp = rs.getDouble("mrp");

                itemList.add(new SaleEntryModel(itemId, productName, saleRate, pack, strip, pcs, expiryDate, discountId,
                        discount, gstId, totalGst, amount, hsn, iGst, cGst, sGst, gstAmount, purchaseRate, mrp));
                totGstAmount += gstAmount;
                totalDiscount += discountAmt;
                totalAmount += amount;
            }

            setTableDate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }

    public void addItem(MouseEvent mouseEvent) {
        customDialog.showFxmlDialog2("chooser/saleQuantityChooser.fxml", "SELECT ITEM");
        refresh();
    }

    public void selectPatient(MouseEvent mouseEvent) {
        customDialog.showFxmlDialog2("chooser/patientChooser.fxml", "SELECT PATIENT");
        if (Main.primaryStage.getUserData() instanceof PatientModel pm) {
            this.patientModel = pm;
            patientNameL.setText(pm.getName());
        }
    }

    public void closeBn(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isShowing()) {
            stage.close();
        }
    }

    public void clearAllBn(ActionEvent event) {

        Platform.runLater(() -> {
            ImageView image = new ImageView(new ImageLoader().load("img/icon/warning_ic.png"));
            image.setFitWidth(45);
            image.setFitHeight(45);
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning ");
            alert.setGraphic(image);
            alert.setHeaderText("Are you sure you want to clearAllBn all items");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(Main.primaryStage);
            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);
            if (button == ButtonType.OK) {
                clearAll();
            } else {
                alert.close();
            }
        });
    }

    private void clearAll() {
        Platform.runLater(() -> {

            Connection con = null;
            PreparedStatement ps = null;
            String query = "TRUNCATE TABLE tbl_cart RESTART IDENTITY";

            try {
                con = dbConnection.getConnection();
                ps = con.prepareStatement(query);

                int res = ps.executeUpdate();

                if (res >= 0) {
                    if (null != itemList) {
                        itemList.clear();
                    }
                    refresh();
                }

            } catch (SQLException e) {
                customDialog.showAlertBox("ERROR", "Failed to Clear Cart !");
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(con, ps, null);
                refresh();
            }
        });
    }

    public void removeItem(int itemId) {
        Connection con = null;
        PreparedStatement ps = null;
        String query = "DELETE FROM tbl_cart WHERE item_id = ?";

        try {
            con = dbConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, itemId);
            int res = ps.executeUpdate();
            if (res >= 0) {
                getCartData();
            }

        } catch (SQLException e) {
            customDialog.showAlertBox("ERROR", "Failed to Clear Cart !");
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(con, ps, null);
            refresh();
        }
    }

    private void refresh() {
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        public void onPreExecute() {
            if (null != tableView) {
                tableView.setItems(null);
            }
            if (null != itemList) {
                itemList.clear();
            }
            assert tableView != null;
            tableView.setPlaceholder(method.getProgressBar(40, 40));
        }

        @Override
        public Boolean doInBackground(String... params) {
            getCartData();
            return true;
        }

        @Override
        public void onPostExecute(Boolean success) {
            tableView.setPlaceholder(new Label("Not Available."));
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    public void checkOutBn(ActionEvent event) {
        if (itemList.size() < 1) {
            customDialog.showAlertBox("Not available", "Item not available.please add at least one item");
            return;
        } else if (null == patientModel) {
            method.show_popup("Please select patient", patientNameL);
            return;
        } else if (!method.isShopDetailsAvailable()) {
            customDialog.showAlertBox("Shop Details Not Available",
                    "Please update shop details");
            return;
        }

        String billType = billingTypeC.getSelectionModel().getSelectedItem();

        ImageView image = new ImageView(new ImageLoader().load("img/icon/warning_ic.png"));
        image.setFitWidth(45);
        image.setFitHeight(45);
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning");
        alert.setGraphic(image);
        alert.setHeaderText("Are you sure you want to sell ?");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Main.primaryStage);
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
            // Bill Type -> "REGULAR", "GST", "KITTY PARTY"
            SaleTask saleTask = new SaleTask(billType, patientModel.getPatientId());
            saleTask.setDaemon(false);
            saleTask.execute();

        } else {
            alert.close();
        }

    }

    private class SaleTask extends AsyncTask<String, Integer, Boolean> {

        private String billType;
        private int patientId;

        public SaleTask(String billType, int patientId) {
            this.billType = billType;
            this.patientId = patientId;
        }


        @Override
        public void onPreExecute() {
            method.hideElement(checkOutButton);
            progressBar.setVisible(true);
        }

        @Override
        public Boolean doInBackground(String... params) {
            addSaleItem(patientId, billType);
            return true;
        }

        @Override
        public void onPostExecute(Boolean success) {
            method.hideElement(progressBar);
            checkOutButton.setVisible(true);
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private void addSaleItem(int patient, String billingType) {
        String paytmModeS = paymentModeC.getSelectionModel().getSelectedItem();

        double totalTaxAmtD = totGstAmount;
        double invoiceValue = 0;

        invoiceValue = Double.parseDouble(invoiceValueTf.getText());
        Connection connection = null;
        PreparedStatement ps = null, psUpdateQty = null;
        ResultSet rs = null;
        ObservableList<SaleEntryModel> items = tableView.getItems();
        try {

            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);
            double addiDisc = 0;
            try {
                addiDisc = Double.parseDouble(addDiscTF.getText());
            } catch (NumberFormatException ignored) {
                customDialog.showAlertBox("", "Please enter valid additional discount");
                return;
            }

            String invoiceNumber = new GenerateBillNumber().getSaleBillNum();

            String saleMainQuery = "INSERT INTO TBL_SALE_MAIN(PATIENT_ID, SELLER_ID, ADDITIONAL_DISCOUNT," +
                    " PAYMENT_MODE, TOT_TAX_AMOUNT, NET_AMOUNT, INVOICE_NUMBER, BILL_TYPE) VALUES (?,?,?,?,?,?,?,?)\n";

            ps = connection.prepareStatement(saleMainQuery, new String[]{"sale_main_id"});
            ps.setInt(1, patient);
            ps.setInt(2, Login.currentlyLogin_Id);
            ps.setDouble(3, addiDisc);
            ps.setString(4, paytmModeS);
            ps.setDouble(5, totalTaxAmtD);
            ps.setDouble(6, invoiceValue);
            ps.setString(7, invoiceNumber);
            ps.setString(8, billingType);
            int resMain = ps.executeUpdate();

            if (resMain > 0) {
                rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    int sale_main_id = rs.getInt(1);
                    ps = null;
                    rs = null;
                    int resItem = 0;
                    String query = "INSERT INTO TBL_SALE_ITEMS(SALE_MAIN_ID, ITEM_ID, ITEM_NAME, " +
                            "sale_rate, STRIP, PCS, DISCOUNT, HSN_SAC, igst, sgst, cgst, NET_AMOUNT, TAX_AMOUNT," +
                            "strip_tab,purchase_rate,mrp)" +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    ps = connection.prepareStatement(query);

                    for (SaleEntryModel model : items) {
                        ps.setInt(1, sale_main_id);
                        ps.setInt(2, model.getItemId());
                        ps.setString(3, model.getProductName());
                        ps.setDouble(4, model.getSaleRate());
                        ps.setInt(5, model.getStrip());
                        ps.setInt(6, model.getPcs());
                        ps.setDouble(7, model.getDiscount());
                        ps.setLong(8, model.getHsn());
                        ps.setInt(9, model.getiGst());
                        ps.setInt(10, model.getsGst());
                        ps.setInt(11, model.getcGst());
                        ps.setDouble(12, model.getAmount());
                        ps.setDouble(13, model.getGstAmount());
                        ps.setInt(14, method.getTbPerStrip(model.getItemId()));
                        ps.setDouble(15, model.getPurchaseRate());
                        ps.setDouble(16, model.getMrp());

                        resItem = ps.executeUpdate();

                        if (resItem > 0) {
                            int pcs = model.getPcs();
                            if (model.getStrip() > 0) {
                                int tab = model.getStrip() * method.getTbPerStrip(model.getItemId());
                                pcs += tab;
                            }

                            String qry = "UPDATE tbl_stock SET quantity = quantity-? WHERE  item_id= ?";
                            psUpdateQty = connection.prepareStatement(qry);
                            psUpdateQty.setInt(1, pcs);
                            psUpdateQty.setInt(2, model.getItemId());
                            psUpdateQty.executeUpdate();

                        }


                    }
                    if (resItem > 0) {
                        psUpdateQty.close();
                        connection.commit();
                        addDiscTF.setText(String.valueOf(0));
                        patientModel = null;
                        Platform.runLater(() -> patientNameL.setText("Click to select patient"));

                        clearAll();
                        /*switch (billingType) {
                            case "REGULAR" -> {
                                map = new GenerateInvoice().regularInvoice(sale_main_id, false, null, true, billingType);
                            }
                            case "GST" -> {
                                map = new GenerateInvoice().gstInvoice(sale_main_id, false, null, billingType);
                            }
                        }*/

                    }

                }
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        } finally {

            DBConnection.closeConnection(connection, ps, rs);
        }

    }
}
