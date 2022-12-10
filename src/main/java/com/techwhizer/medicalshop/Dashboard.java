package com.techwhizer.medicalshop;

import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.GetUserProfile;
import com.techwhizer.medicalshop.model.UserDetails;
import com.techwhizer.medicalshop.util.AppConfig;
import com.techwhizer.medicalshop.util.DBConnection;
import com.techwhizer.medicalshop.util.RoleKey;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    @FXML
    HBox staffTopContainer;
    public Button bn_logout;
    public StackPane contentArea;
    public Label fullName;
    public ImageView userImage;
    public Label userRole;
    public ScrollPane menuScrollTop;
    public Hyperlink homeBn;
    public Hyperlink myProductBn;
    public Hyperlink saleProduct;
    public Hyperlink saleReportBn;
    public Hyperlink returnProductBn;
    public Hyperlink invoiceBn;
    public Hyperlink kittyPartBn;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private Main main;
    public static Stage stage;
    private Properties propRead;
    public MenuButton feedbackMenuButton;
    public MenuButton settingMenuButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
/*        if (Objects.equals(Login.currentRoleName, RoleKey.STAFF)){
            saleProductBnClick(null);
        }else {
            homeBnClick(null);
        }*/

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

        setVisible(menuScrollTop, !Objects.equals(Login.currentRoleName, RoleKey.STAFF));
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

        // FEEDBACK
        MenuItem addFeedback = new MenuItem("ADD FEEDBACK");
        MenuItem view_feedback = new MenuItem("VIEW FEEDBACK");
        feedbackMenuButton.getItems().addAll(addFeedback, view_feedback);
        addFeedback.setOnAction(event -> addFeedback());
        view_feedback.setOnAction(event -> showDialog("viewFeedback.fxml", "ALL FEEDBACK", 650, 750, StageStyle.DECORATED));

        // product -- start
        Menu product = new Menu("PRODUCT");
        MenuItem category = new MenuItem("CATEGORY");
        MenuItem discount = new MenuItem("DISCOUNT");
        MenuItem gst = new MenuItem("GST");
        MenuItem company = new MenuItem("COMPANY");


        product.getItems().addAll(category,discount,gst,company);

        // general -- end
        MenuItem dealer = new MenuItem("DEALER");
        MenuItem shopData = new MenuItem("SHOP DETAILS");
        MenuItem profile = new MenuItem("PROFILE");
        MenuItem users = new MenuItem("USERS");
        MenuItem patient = new MenuItem("VIEW PATIENT");
        MenuItem myLicense = new MenuItem("MY LICENSE");
        MenuItem backup = new MenuItem("BACKUP");

        users.setVisible(Objects.equals(Login.currentRoleName, RoleKey.ADMIN));

        settingMenuButton.getItems().addAll(product, profile, users, shopData, patient, myLicense, dealer, backup);

        onClickAction(gst, myLicense, shopData, category,
                profile, users, dealer, patient, backup,company,discount);

    }

    private void showAddProductDialog() {

        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(CustomDialog.class.getResource("product/addProduct.fxml")));
            stage = new Stage();
            stage.getIcons().add(new ImageLoader().load(AppConfig.APPLICATION_ICON));
            stage.setTitle("Add new product");
            stage.setMaximized(false);
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/main.css")).toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Main.primaryStage);
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addFeedback() {
        customDialog.showFxmlDialog2("feedbackDialog.fxml", "FEEDBACK");

    }

    private void onClickAction(MenuItem gst, MenuItem myLicense, MenuItem shopData, MenuItem category,
                               MenuItem profile, MenuItem users, MenuItem dealer, MenuItem patient , MenuItem backup, MenuItem company, MenuItem discount) {

        gst.setOnAction(event -> {
            customDialog.showFxmlDialog2("product/gst/gstConfig.fxml", "GST");
            if (Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase())) {
                refreshPage();
            }
        });
        myLicense.setOnAction(event -> customDialog.showFxmlDialog2("license/licenseMain.fxml", "My Subscription"));
        backup.setOnAction(event -> customDialog.showFxmlDialog2("db_backup.fxml", "BACKUP"));
        patient.setOnAction(event -> customDialog.showFxmlFullDialog("setting/customer.fxml", "ALL CUSTOMER"));
        shopData.setOnAction(event -> customDialog.showFxmlDialog2("shopDetails.fxml", ""));
        category.setOnAction(event -> customDialog.showFxmlDialog2("product/viewCategory.fxml","Categories list"));
        company.setOnAction(event -> customDialog.showFxmlDialog2("product/viewCompany.fxml","Companies list"));
        discount.setOnAction(event -> customDialog.showFxmlDialog2("product/discount/discount.fxml","Companies list"));
        users.setOnAction(event -> {
            customDialog.showFxmlFullDialog("dashboard/users.fxml", "ALL USERS");
            if (Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase())) {
                refreshPage();
            }
        });
        dealer.setOnAction(event -> customDialog.showFxmlFullDialog("product/dealer/allDealer.fxml", "ALL DEALER"));
        profile.setOnAction(event -> {

            Main.primaryStage.setUserData(Login.currentlyLogin_Id);
            customDialog.showFxmlDialog2("dashboard/userprofile.fxml", "MY PROFILE");
            if (Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase())) {
                refreshPage();
            }
        });


    }

    private void refreshPage() {
        setCustomImage();
        setUserData();
    }

    private void showDialog(String fxmlName, String title, double height,
                            double width, StageStyle utility) {

        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(CustomDialog.class.getResource(fxmlName)));
            stage = new Stage();
            stage.getIcons().add(new ImageLoader().load(AppConfig.APPLICATION_ICON));
            stage.setTitle(title);
            stage.setMaximized(false);
            Scene scene = new Scene(parent, width, height);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/main.css")).toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(Main.primaryStage);
            stage.initStyle(utility);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            main.changeScene("login.fxml", "LOGIN HERE");
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
        replaceScene("dashboard/allProducts.fxml");
    }

    public void saleProductBnClick(ActionEvent actionEvent) {
        replaceScene("dashboard/saleProducts.fxml");
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


    public void kittyBnClick(ActionEvent actionEvent) {
        replaceScene("proposal/proposalMain.fxml");
    }

    public void addProductBnClick(ActionEvent actionEvent) {
        showAddProductDialog();
    }

    public void myProfileClick(ActionEvent event) {

        Main.primaryStage.setUserData(Login.currentlyLogin_Id);
        customDialog.showFxmlDialog2("dashboard/userprofile.fxml", "MY PROFILE");
        if (Objects.equals(Login.currentRoleName.toLowerCase(), "admin".toLowerCase())) {
            refreshPage();
        }
    }

    public void backupBnClick(ActionEvent event) {

        customDialog.showFxmlDialog2("db_backup.fxml", "    BACKUP");
    }

    public void addItems(ActionEvent actionEvent) {

        customDialog.showFxmlFullDialog("product/all_items.fxml","ADD ITEMS");
    }
}
