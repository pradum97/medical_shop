package com.techwhizer.medicalshop;

import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.GetUserProfile;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.UserDetails;
import com.techwhizer.medicalshop.util.DBConnection;
import com.techwhizer.medicalshop.util.RoleKey;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dashboard implements Initializable {
    @FXML
    public Label dateL;
    @FXML
    public BorderPane main_container;
    public Button bn_logout;
    public StackPane contentArea;
    public Label fullName;
    public ImageView userImage;
    public Label userRole;
    public Hyperlink homeBn;
    public Hyperlink myProductBn;
    public Hyperlink saleProduct;
    public Hyperlink saleReportBn;
    public Hyperlink returnProductBn;
    public Hyperlink invoiceBn;
    public Hyperlink stockH;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private Main main;
    public static Stage stage;
    private Properties propRead;
    public MenuButton settingMenuButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Objects.equals(Login.currentRoleName, RoleKey.STAFF)){
            saleProductBnClick(null);
        }else {
            homeBnClick(null);
        }

        main_container.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/setting.css")).toExternalForm());
        dbConnection = new DBConnection();
        PropertiesLoader propLoader = new PropertiesLoader();
        propRead = propLoader.getReadProp();
        customDialog = new CustomDialog();
        main = new Main();
        addButtonMenu();
        setCustomImage();
        setUserData();

        setting();
    }

    private void setting() {

        setVisible(homeBn, !Objects.equals(Login.currentRoleName, RoleKey.STAFF));
        setVisible(myProductBn, !Objects.equals(Login.currentRoleName, RoleKey.STAFF));
        setVisible(saleReportBn, !Objects.equals(Login.currentRoleName, RoleKey.STAFF));
        setVisible(returnProductBn, !Objects.equals(Login.currentRoleName, RoleKey.STAFF));

    }

    private void setVisible(Node node, boolean isVisible) {
        node.setVisible(isVisible);
        node.managedProperty().bind(node.visibleProperty());
    }

    private void addButtonMenu() {

        // product -- start
        Menu product = new Menu("ITEM MASTER");
        MenuItem discount = new MenuItem("DISCOUNT");
        MenuItem gst = new MenuItem("GST");
        MenuItem company = new MenuItem("COMPANY");
        MenuItem manufacture = new MenuItem("MANUFACTURE");
        MenuItem mr = new MenuItem("MEDICAL REPRESENTATIVE");


        product.getItems().addAll(discount,gst,company,manufacture,mr);

        // general -- end
        MenuItem dealer = new MenuItem("DEALER");
        MenuItem shopData = new MenuItem("SHOP DETAILS");
        MenuItem profile = new MenuItem("PROFILE");
        MenuItem users = new MenuItem("USERS");
        MenuItem patient = new MenuItem("PATIENT");
        MenuItem doctor = new MenuItem("DOCTOR");
        MenuItem myLicense = new MenuItem("MY LICENSE");
        MenuItem backup = new MenuItem("BACKUP");

        users.setVisible(Objects.equals(Login.currentRoleName, RoleKey.ADMIN));

        settingMenuButton.getItems().addAll(product, profile, users, shopData, patient,doctor, myLicense, dealer, backup);

        onClickAction(gst, myLicense, shopData,
                profile, users, dealer, patient, backup,company,discount,manufacture , mr,doctor);

    }

    private void onClickAction(MenuItem gst, MenuItem myLicense, MenuItem shopData,
                               MenuItem profile, MenuItem users, MenuItem dealer, MenuItem patient ,
                               MenuItem backup, MenuItem company, MenuItem discount, MenuItem manufacture,
                               MenuItem mr, MenuItem doctor) {

        gst.setOnAction(event -> {
            customDialog.showFxmlDialog2("product/gst/gstConfig.fxml", "GST");
            if (Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase())) {
                refreshPage();
            }
        });
        myLicense.setOnAction(event -> customDialog.showFxmlDialog2("license/licenseMain.fxml", "My Subscription"));
        backup.setOnAction(event -> customDialog.showFxmlDialog2("db_backup.fxml", "BACKUP"));
        patient.setOnAction(event -> customDialog.showFxmlFullDialog("product/patient/patientMain.fxml", "ALL PATIENTS"));
        doctor.setOnAction(event -> customDialog.showFxmlFullDialog("product/doctor/view_doctor.fxml", "DOCTORS"));
        shopData.setOnAction(event -> {
            if (new Method().isShopDetailsAvailable()){
                customDialog.showFxmlDialog2("shopDetails.fxml", "SHOP DETAILS");
            }else {
                customDialog.showFxmlDialog2("update/shopDetailsUpdate.fxml", "Shop details not available.Please submit shop details");
            }

        });
        company.setOnAction(event -> customDialog.showFxmlDialog2("product/viewCompany.fxml","Companies lists"));
        discount.setOnAction(event -> customDialog.showFxmlDialog2("product/discount/discount.fxml","Discounts"));
        users.setOnAction(event -> {
            customDialog.showFxmlFullDialog("user/users.fxml", "ALL USERS");
            if (Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase())) {
                refreshPage();
            }
        });
        dealer.setOnAction(event -> customDialog.showFxmlFullDialog("product/dealer/allDealer.fxml", "DEALERS"));
        profile.setOnAction(event -> {

            Main.primaryStage.setUserData(Login.currentlyLogin_Id);
            customDialog.showFxmlDialog2("user/userprofile.fxml", "MY PROFILE");
            if (Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase())) {
                refreshPage();
            }
        });

        manufacture.setOnAction(event -> customDialog.showFxmlDialog2("product/manufactureMain.fxml","Manufactures"));
        mr.setOnAction(event -> customDialog.showFxmlDialog2("product/mr/mrMain.fxml","Medical Representatives"));



    }

    private void refreshPage() {
        setCustomImage();
        setUserData();
    }
    private void setUserData() {

        UserDetails userDetails = new GetUserProfile().getUser(Login.currentlyLogin_Id);

        if (null != userDetails) {
            fullName.setText((userDetails.getFirstName() + " " + userDetails.getLastName()).toUpperCase());

            userRole.setText(userDetails.getRole().toUpperCase());
            String imgPath = "img/Avatar/" + userDetails.getUserImage();
            userImage.setImage(new ImageLoader().load(imgPath));
        }
    }

    private void setCustomImage() {

        ImageView logout_img = new ImageView();
        logout_img.setImage(new ImageLoader().load("img/menu_icon/logout_ic.png"));
        logout_img.setFitHeight(20);
        logout_img.setFitWidth(20);

        bn_logout.setGraphic(logout_img);
    }

    private void replaceScene(String fxml_file_name) {

        try {
            Parent parent = FXMLLoader.load(getClass().getResource(fxml_file_name));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(parent);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void bnLogout(ActionEvent event) {

        ImageView image = new ImageView(new ImageLoader().load("img/icon/warning_ic.png"));
        image.setFitWidth(45);
        image.setFitHeight(45);
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning ");
        alert.setGraphic(image);
        alert.setHeaderText("ARE YOU SURE YOU WANT TO LOGOUT ?");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Main.primaryStage);
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
            main.changeScene("auth/login.fxml", "LOGIN HERE");
            Login.currentlyLogin_Id = 0;
            Login.currentRoleName = "";
            Login.currentRole_Id = 0;
        } else {
            alert.close();
        }

    }

    public void homeBnClick(ActionEvent actionEvent) {
        replaceScene("dashboard/home.fxml");
    }

    public void myProductBnClick(ActionEvent actionEvent) {
        replaceScene("dashboard/all_products.fxml");
    }
    public void stockReport(ActionEvent event) {

        replaceScene("dashboard/stockReport.fxml");
    }


    public void saleProductBnClick(ActionEvent actionEvent) {

        customDialog.showFxmlDialog2("dashboard/saleEntry.fxml","SALE ENTRY");
    }

    public void saleReportBnClick(ActionEvent actionEvent) {
        replaceScene("dashboard/saleReport.fxml");
    }

    public void returnProductBnClick(ActionEvent actionEvent) {
        replaceScene("returnItems/returnProduct.fxml");

    }

    public void invoiceBnClick(ActionEvent actionEvent) {
        replaceScene("dashboard/invoiceReport.fxml");
    }

    public void addProductBnClick(MouseEvent actionEvent) {
        customDialog.showFxmlDialog2("product/addProduct.fxml","Add new product");
    }

    public void addPurchase(MouseEvent actionEvent) {
       customDialog.showFxmlFullDialog("product/purchase/purchaseMain.fxml","PURCHASE ENTRY");
    }

}
