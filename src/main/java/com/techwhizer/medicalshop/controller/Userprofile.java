package com.techwhizer.medicalshop.controller;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.GetUserProfile;
import com.techwhizer.medicalshop.model.UserDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Userprofile implements Initializable {

    @FXML
    public ImageView userImage;
    public Label fullName;
    public Label userName;
    public Label userGender;
    public Label userRole;
    public Label userEmail;
    public Label userPhone;
    public Label userAddress;
    public Button bnChangePassword;
    public Button bnEdit;
    private CustomDialog customDialog;
    private int userId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customDialog = new CustomDialog();


        userId = ((int) Main.primaryStage.getUserData());

        setUserData(userId);

        if (Objects.equals(Login.currentRoleName.toLowerCase(), "seller".toLowerCase())){
            bnChangePassword.managedProperty().bind(bnChangePassword.visibleProperty());
            bnChangePassword.setVisible(false);
            bnEdit.managedProperty().bind(bnEdit.visibleProperty());
            bnEdit.setVisible(false);
        }else {
            bnChangePassword.setVisible(true);
            bnEdit.setVisible(true);
        }

    }

    private void setUserData(int userId) {

        if (userId != Login.currentlyLogin_Id) {
            bnChangePassword.setVisible(false);
            bnChangePassword.managedProperty().bind(bnChangePassword.visibleProperty());
        }

        UserDetails userDetails = new GetUserProfile().getUser(userId);

        if (null == userDetails) {
            customDialog.showAlertBox("Failed", "User Not Find Please Re-Login");
        } else {

            fullName.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
            userRole.setText(userDetails.getRole());
            userName.setText(userDetails.getUsername());
            userGender.setText(userDetails.getGender());
            userEmail.setText(userDetails.getEmail());
            userPhone.setText(String.valueOf(userDetails.getPhone()));
            userAddress.setText(userDetails.getFullAddress());

            String path = "img/Avatar/" + userDetails.getUserImage();

            userImage.setImage(new ImageLoader().load(path));
        }
    }

    public void editProfile(ActionEvent event) {
       Main.primaryStage.setUserData(userId);

        customDialog.showFxmlDialog2("update/updateProfile.fxml", "EDIT PROFILE");

        setUserData(userId);
    }

    public void changePassword(ActionEvent event) {

        customDialog.showFxmlDialog("dashboard/forgotPassword.fxml", "CHANGE PASSWORD");
    }

    public void cancel(ActionEvent event) {

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        if (stage.isShowing()){
            stage.close();
        }
    }
}
