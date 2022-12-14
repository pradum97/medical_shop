package com.techwhizer.medicalshop.controller.product.discount;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.model.DiscountModel;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Discount implements Initializable {
    int rowsPerPage = 10;

    public TableView<DiscountModel> tableViewDiscount;
    public TableColumn<DiscountModel, Integer> colSlNo;
    public TableColumn<DiscountModel, String> colDiscount;
    public TableColumn<DiscountModel, String> colDiscountDes;
    public TableColumn<DiscountModel, String> colDiscountName;
    public TableColumn<DiscountModel, String> colAction;
    public Pagination pagination;
    private Method method;
    private DBConnection dbConnection;
    private CustomDialog customDialog;
    private ObservableList<DiscountModel> discountList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        method = new Method();
        dbConnection = new DBConnection();
        customDialog = new CustomDialog();

        callThread();

    }

    private void callThread() {
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setDaemon(false);
        myAsyncTask.execute();
    }

    public void refresh(ActionEvent event) {
        callThread();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg;

        @Override
        public void onPreExecute() {
            msg = "";
            if (null != tableViewDiscount) {
                tableViewDiscount.setItems(null);
            }

            assert tableViewDiscount != null;
            tableViewDiscount.setPlaceholder(method.getProgressBar(40, 40));

        }

        @Override
        public Boolean doInBackground(String... params) {

            Map<String, Object> status = setDiscountData();
            boolean isSuccess = (boolean) status.get("is_success");
            msg = (String) status.get("message");

            if (discountList.size() > 0) {
                pagination.setVisible(true);
            }
            changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
            pagination.setCurrentPageIndex(0);
            changeTableView(0, rowsPerPage);
            pagination.currentPageIndexProperty().addListener(
                    (observable1, oldValue1, newValue1) -> changeTableView(newValue1.intValue(), rowsPerPage));

            return isSuccess;
        }

        @Override
        public void onPostExecute(Boolean success) {
            tableViewDiscount.setPlaceholder(new Label(msg));
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private void changeTableView(int index, int limit) {

        int totalPage = (int) (Math.ceil(discountList.size() * 1.0 / rowsPerPage));
        Platform.runLater(()-> pagination.setPageCount(totalPage));

        colSlNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                tableViewDiscount.getItems().indexOf(cellData.getValue()) + 1));
        colDiscountName.setCellValueFactory(new PropertyValueFactory<>("discountName"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colDiscountDes.setCellValueFactory(new PropertyValueFactory<>("description"));

        setOptionalCell();

        customColumn(colDiscountName);

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, discountList.size());

        int minIndex = Math.min(toIndex, discountList.size());
        SortedList<DiscountModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(discountList.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableViewDiscount.comparatorProperty());

        tableViewDiscount.setItems(sortedData);

    }

    private void setOptionalCell() {

        Callback<TableColumn<DiscountModel, String>, TableCell<DiscountModel, String>>
                cellFactory = (TableColumn<DiscountModel, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);

                } else {
                    ImageLoader imageLoader = new ImageLoader();
                    ImageView iv_edit = new ImageView(imageLoader.load("img/icon/edit_ic.png"));
                    iv_edit.setFitHeight(22);
                    iv_edit.setFitHeight(22);
                    iv_edit.setPreserveRatio(true);


                    iv_edit.setStyle(
                            " -fx-cursor: hand ;"
                                    + "-glyph-size:28px;"
                                    + "-fx-fill:#c506fa;"
                    );

                    iv_edit.setOnMouseClicked((MouseEvent event) -> {
                        method.selectTable(getIndex(), tableViewDiscount);

                        DiscountModel edit_selection = tableViewDiscount.
                                getSelectionModel().getSelectedItem();

                        if (null == edit_selection) {
                            method.show_popup("Please Select", tableViewDiscount);
                            return;
                        }

                        Main.primaryStage.setUserData(edit_selection);

                        customDialog.showFxmlDialog("update/product/discountUpdate.fxml", "UPDATE DISCOUNT");
                        callThread();

                    });


                    HBox managebtn = new HBox(iv_edit);

                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(iv_edit, new Insets(2, 2, 0, 3));
                    setGraphic(managebtn);

                    setText(null);

                }
            }

        };

        colAction.setCellFactory(cellFactory);
    }


    private void customColumn(TableColumn<DiscountModel, String> columnName) {

        columnName.setCellFactory(tc -> {
            TableCell<DiscountModel, String> cell = new TableCell<>();
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

    public void addDiscountBN(ActionEvent event) {

        customDialog.showFxmlDialog("product/discount/addDiscount.fxml", "Create new discount");
        callThread();
    }

    private Map<String, Object> setDiscountData() {

        if (null != discountList) {
            discountList.clear();
        }
        Map<String, Object> map = method.getDiscount();
        discountList = (ObservableList<DiscountModel>) map.get("data");
        return map;
    }
}
