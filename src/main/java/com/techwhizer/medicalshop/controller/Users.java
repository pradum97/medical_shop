package com.techwhizer.medicalshop.controller;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.controller.auth.Login;
import com.techwhizer.medicalshop.method.GetUserProfile;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.UserDetails;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

;

public class Users implements Initializable {
    public TableView<UserDetails> user_table_view;
    public TableColumn<UserDetails, String> col_id;
    public TableColumn<UserDetails, String> col_username;
    public TableColumn<UserDetails, String> col_FirstName;
    public TableColumn<UserDetails, String> col_LastName;
    public TableColumn<UserDetails, String> col_gender;
    public TableColumn<UserDetails, String> col_email;
    public TableColumn<UserDetails, String> col_phone;
    public TableColumn<UserDetails, String> col_role;
    public TableColumn<UserDetails, String> col_address;
    public TableColumn<UserDetails, String> col_account_status;
    public TableColumn<UserDetails, String> action;

    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private Method method;

    private ObservableList<UserDetails> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();

        setUserData();
    }

    private void setUserData() {

        if (null != userList) {
            userList.clear();
        }

        GetUserProfile getUserProfile = new GetUserProfile();

        userList = getUserProfile.getAllUser();

        user_table_view.setItems(userList);

        col_id.setCellValueFactory(new PropertyValueFactory<>("userID"));
        col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        col_FirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        col_LastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        col_gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_role.setCellValueFactory(new PropertyValueFactory<>("role"));
        col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        col_address.setCellValueFactory(new PropertyValueFactory<>("fullAddress"));


        Callback<TableColumn<UserDetails, String>, TableCell<UserDetails, String>>
                cellFactory = (TableColumn<UserDetails, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {

                    int userId = user_table_view.getItems().get(getIndex()).getUserID();

                    ImageLoader loader = new ImageLoader();


                    ImageView iv_edit, iv_view, iv_delete;


                    iv_edit = new ImageView(loader.load("img/icon/edit_ic.png"));
                    iv_edit.setFitHeight(22);
                    iv_edit.setFitHeight(22);
                    iv_edit.setPreserveRatio(true);

                    iv_view = new ImageView(loader.load("img/icon/view_ic.png"));
                    iv_view.setFitHeight(22);
                    iv_view.setFitWidth(22);
                    iv_view.setPreserveRatio(true);

                    iv_delete = new ImageView(loader.load("img/icon/delete_ic.png"));
                    iv_delete.setPreserveRatio(true);

                    if (Login.currentlyLogin_Id == userId) {

                        if (null != iv_delete) {
                            iv_delete.setImage(null);
                            iv_delete.setImage(loader.load("img/icon/active_ic.png"));
                            iv_delete.setFitHeight(20);
                            iv_delete.setFitWidth(20);
                        }

                    } else {

                        iv_delete.setVisible(true);
                        iv_delete.setFitHeight(17);
                        iv_delete.setFitWidth(17);
                        iv_delete.setOnMouseClicked((MouseEvent event) -> {


                            UserDetails delete_selection = user_table_view.
                                    getSelectionModel().getSelectedItem();

                            if (null == delete_selection) {
                                method.show_popup("Please Select ", user_table_view);
                                return;
                            }

                            deleteUsers(delete_selection);

                        });
                    }


                    iv_edit.setStyle(
                            " -fx-cursor: hand ;"
                                    + "-glyph-size:28px;"
                                    + "-fx-fill:#c506fa;"
                    );
                    iv_view.setStyle(
                            " -fx-cursor: hand ;"
                                    + "-glyph-size:28px;"
                                    + "-fx-fill:#44ee0c;"
                    );

                    iv_delete.setStyle(
                            " -fx-cursor: hand ;"
                                    + "-glyph-size:28px;"
                                    + "-fx-fill:#ff0000;"
                    );
                    iv_edit.setOnMouseClicked((MouseEvent event) -> {

                        UserDetails edit_selection = user_table_view.
                                getSelectionModel().getSelectedItem();

                        if (null == edit_selection) {
                            method.show_popup("Please Select", user_table_view);
                            return;
                        }

                        Main.primaryStage.setUserData(edit_selection.getUserID());

                        customDialog.showFxmlDialog("update/updateProfile.fxml", "EDIT PROFILE");
                        refreshTableData();


                    });
                    iv_view.setOnMouseClicked((MouseEvent event) -> {

                        UserDetails view_selection = user_table_view.
                                getSelectionModel().getSelectedItem();

                        if (null == view_selection) {
                            method.show_popup("Please Select", user_table_view);
                            return;
                        }

                        Main.primaryStage.setUserData(view_selection.getUserID());
                        customDialog.showFxmlDialog("dashboard/userprofile.fxml", "User Profile");
                    });


                    HBox managebtn = new HBox(iv_edit, iv_delete, iv_view);

                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(iv_edit, new Insets(2, 2, 0, 3));
                    HBox.setMargin(iv_view, new Insets(2, 3, 0, 20));
                    HBox.setMargin(iv_delete, new Insets(2, 3, 0, 20));

                    setGraphic(managebtn);

                    setText(null);

                }
            }

        };

        Callback<TableColumn<UserDetails, String>, TableCell<UserDetails, String>>
                acStatusCellFactory = (TableColumn<UserDetails, String> param) -> {

            final TableCell<UserDetails, String> cell = new TableCell<UserDetails, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);

                    } else {
                        String accountStatus = userList.get(getIndex()).getAccountStatus();

                        Label text = new Label();
                        text.setText(accountStatus);
                        switch (accountStatus) {
                            case "Active" -> text.setStyle("-fx-text-fill: green ; -fx-font-weight: bold");
                            case "Inactive" -> text.setStyle("-fx-text-fill: red");
                        }


                        HBox managebtn = new HBox(text);
                        managebtn.setStyle("-fx-alignment:center");
                        HBox.setMargin(text, new Insets(2, 2, 0, 3));

                        setGraphic(managebtn);

                        setText(null);
                    }
                }

            };

            return cell;
        };

        action.setCellFactory(cellFactory);
        col_account_status.setCellFactory(acStatusCellFactory);

        customColumn(col_role);

        customColumn(col_email);

        customColumn(col_address);
        customColumn(col_username);
        customColumn(col_FirstName);
        customColumn(col_LastName);

    }

    private void deleteUsers(UserDetails user) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning ");
        alert.setHeaderText("Are You Sure You Want to Delete This User ( " + user.getFirstName() + " " + user.getLastName() + " )");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Main.primaryStage);
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {
            Connection con = null;
            PreparedStatement ps = null;

            try {

                con = dbConnection.getConnection();

                if (null == con) {
                    return;
                }

                ps = con.prepareStatement("DELETE FROM TBL_USERS WHERE USER_ID = ?");
                ps.setInt(1, user.getUserID());

                int res = ps.executeUpdate();

                if (res > 0) {
                    refreshTableData();
                    alert.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {

                try {

                    if (null != con) {
                        con.close();
                    }
                    if (null != ps) {
                        ps.close();
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } else {
            alert.close();
        }
    }

    private void customColumn(TableColumn<UserDetails, String> columnName) {

        columnName.setCellFactory(tc -> {
            TableCell<UserDetails, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("-fx-font-size: 14");
            cell.setGraphic(text);
            text.setStyle("-fx-text-alignment: CENTER ;");
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(columnName.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    private void refreshTableData() {

        setUserData();

    }

    public void bn_addUsers(ActionEvent event) {

        Main.primaryStage.setUserData("adduser");
        customDialog.showFxmlFullDialog("signup.fxml", "Add New User");
        refreshTableData();

    }
}
