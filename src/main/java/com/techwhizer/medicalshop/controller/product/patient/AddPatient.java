package com.techwhizer.medicalshop.controller.product.patient;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.method.StaticData;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AddPatient implements Initializable {
    public TextField nameTf;
    public TextField phoneTf;
    public TextField addressTf;
    public TextField idNumberTf;
    public ComboBox<String> genderCom;
    public TextField ageTf;
    public TextField coName;
    public TextField weightTf;
    public TextField bpTf;
    public TextField pulseTf;
    public TextField sugarTf;

    public TextField spo2Tf;
    public TextField tempTf;
    public TextField cvsTf;
    public TextField cnsTf;
    public TextField chestTf;

    private Method method;
    private CustomDialog customDialog;
    private DBConnection dbConnection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        customDialog = new CustomDialog();
        dbConnection = new DBConnection();
        genderCom.setItems(new StaticData().getGender());
    }

    public void enterPress(KeyEvent ev) {
        if (ev.getCode() == KeyCode.ENTER) {
            addNewPatient();
        }
    }

    public void cancel_Bn(ActionEvent event) {
        closeStage();
    }

    public void submit_bn(ActionEvent event) {
        addNewPatient();
    }

    private void closeStage() {
        Stage stage = (Stage) nameTf.getScene().getWindow();
        if (null != stage && stage.isShowing()) {
            stage.close();
        }
    }

    private void addNewPatient() {
        Pattern pattern = Pattern.compile("^\\d{10}$");

        String fullName = nameTf.getText();
        String phone = phoneTf.getText();
        String idNumber = idNumberTf.getText();
        String address = addressTf.getText();
        String gender = genderCom.getSelectionModel().getSelectedItem();
        String age = ageTf.getText();
        String co = coName.getText();
        String weight = weightTf.getText();
        String bp = bpTf.getText();
        String pulse = pulseTf.getText();
        String sugar = sugarTf.getText();

        String spo2 = spo2Tf.getText();
        String temp  = tempTf.getText();
        String cvs = cvsTf.getText();
        String cns = cnsTf.getText();
        String chest = chestTf.getText();

        if (fullName.isEmpty()) {
            method.show_popup("Enter customer full name", nameTf);
            return;
        }else if (address.isEmpty()) {
            method.show_popup("Please enter patient address", addressTf);
            return;
        }else if (genderCom.getSelectionModel().isEmpty()) {
            method.show_popup("Please select gender", genderCom);
            return;
        }
        if (!phone.isEmpty()) {
            if (!pattern.matcher(phone).find()) {
                method.show_popup("Enter 10-digit phone number", phoneTf);
                return;
            }
        }

        if (!age.isEmpty()) {
            try {
                Integer.parseInt(age);
            } catch (NumberFormatException e) {
                method.show_popup("Enter age in number", ageTf);
                return;
            }
        }

        ImageView image = new ImageView(new ImageLoader().load("img/icon/warning_ic.png"));
        image.setFitWidth(45);
        image.setFitHeight(45);
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning ");
        alert.setGraphic(image);
        alert.setHeaderText("Are you sure you want to submit.");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Main.primaryStage);
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
            Connection connection = null;
            PreparedStatement ps = null;

            try {
                connection = new DBConnection().getConnection();

                String qry = "INSERT INTO TBL_PATIENT(NAME, PHONE, ADDRESS, ID_NUMBER, GENDER, AGE,CHEST, CARE_OF,\n" +
                        "                        WEIGHT, BP, PULSE, SUGAR, SPO2, TEMP, CVS, CNS) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                ps = connection.prepareStatement(qry);
                ps.setString(1, fullName);
                ps.setString(2, phone);
                ps.setString(3, address);
                ps.setString(4, idNumber);
                ps.setString(5, gender);
                ps.setString(6, age);
                ps.setString(7, chest);
                ps.setString(8, co);
                ps.setString(9, weight);
                ps.setString(10, bp);
                ps.setString(11, pulse);
                ps.setString(12, sugar);
                ps.setString(13, spo2);
                ps.setString(14, temp);
                ps.setString(15, cvs);
                ps.setString(16, cns);

                int res = ps.executeUpdate();

                if (res > 0) {
                    closeStage();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(connection, ps, null);
            }
        } else {
            alert.close();
        }
    }

    public void cancelBn(ActionEvent event) {
    }

    public void submitBn(ActionEvent event) {
    }
}
