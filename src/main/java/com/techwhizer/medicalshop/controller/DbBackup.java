package com.techwhizer.medicalshop.controller;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.Main;
import com.techwhizer.medicalshop.method.DatabaseBackup;
import com.techwhizer.medicalshop.method.InternetConnection;
import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.util.DBConnection;
import com.victorlaerte.asynctask.AsyncTask;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DbBackup implements Initializable {
    final String BACKUP_URL = "http://techwhizer.in/licenseApi/backup.php";
    public Label backupDateL;
    public Label lastBackupL;
    public RadioButton localBackupRadio;
    public RadioButton serverBackupRadio;
    public Button backup_button;
    public ProgressIndicator progressBar;
    private DatabaseBackup backup;
    private CustomDialog customDialog;
    private Method method;

    private static final String LOCAL_BACKUP = "local", SERVER_BACKUP = "server";
    private String backupType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backup = new DatabaseBackup();
        customDialog = new CustomDialog();
        method = new Method();
        setLabelData();

        localBackupRadio.setSelected(true);

        localBackupRadio.selectedProperty()
                .addListener((observableValue, aBoolean, t1) -> {

                    if (serverBackupRadio.isSelected()) {
                        serverBackupRadio.setSelected(false);
                    } else if (!localBackupRadio.isSelected()) {
                        localBackupRadio.setSelected(true);
                    }

                    setBackupType();
                });

        serverBackupRadio.selectedProperty()
                .addListener((observableValue, aBoolean, t1) -> {

                    if (localBackupRadio.isSelected()) {
                        localBackupRadio.setSelected(false);
                    } else if (!serverBackupRadio.isSelected()) {
                        serverBackupRadio.setSelected(true);
                    }

                    setBackupType();
                });
        method.hideElement(progressBar);
        setBackupType();
    }

    private void setBackupType() {

        if (localBackupRadio.isSelected()) {
            backupType = LOCAL_BACKUP;
        } else if (serverBackupRadio.isSelected()) {
            backupType = SERVER_BACKUP;
        }
    }

    private void setLabelData() {
        backupDateL.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

        final String FIRST_REGEX = "backup_";
        final String LAST_REGEX = ".backup";
        final String PATTERN = "dd_MM_yyyy";
        String path = System.getProperty("user.home") + "/HOTEL_DB_BACKUP/";
        File dir = new File(path);
        String[] list = dir.list((dir1, name) -> name.toLowerCase().endsWith(LAST_REGEX));

        List<String> strDateList = new ArrayList<>();

        if (null == list) {
            return;
        }
        for (String s : list) {
            strDateList.add(s.replaceAll(FIRST_REGEX, "").replaceAll(LAST_REGEX, ""));
        }
        List<LocalDate> dateList = new ArrayList<>();
        for (String ds : strDateList) {
            dateList.add(LocalDate.parse(ds, DateTimeFormatter.ofPattern(PATTERN)));
        }
        Collections.sort(dateList);

        String lastBackup = null;
        for (LocalDate localDate : dateList) {
            lastBackup = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
        lastBackupL.setText(lastBackup);
    }

    public void backupNow(ActionEvent actionEvent) throws IOException {

        setBackupType();

        String msg = "Are you sure you want to backup ?";

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(msg);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Main.primaryStage);
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.CANCEL);
        if (button == ButtonType.OK) {

            MyAsyncTask myAsyncTask = new MyAsyncTask();
            myAsyncTask.setDaemon(false);
            myAsyncTask.execute();

        } else {
            alert.close();
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String msg = "";

        @Override
        public void onPreExecute() {
            //Background Thread will start
            method.hideElement(backup_button);
            progressBar.setVisible(true);
        }

        @Override
        public Boolean doInBackground(String... params) {
            /* Background Thread is running */
            Map<String, Object> status = new HashMap<>();
            switch (backupType) {

                case LOCAL_BACKUP -> status = startLocalBackup();

                case SERVER_BACKUP -> {

                    boolean checkInterNet =  InternetConnection.checkInternetConnection(false);
                    if (checkInterNet){
                        status = backup.startBackup();
                        String path = (String) status.get("path");

                        if (null != path) {
                            status = startServerBackup(path);
                        } else {
                            status.put("message","An error occurred during backup");
                            status.put("is_success",false);
                        }
                    }else {
                        status.put("message","Internet not available. Please connect to the internet");
                        status.put("is_success",false);
                    }

                }
            }

            boolean isSuccess = (boolean) status.get("is_success");
            msg = (String) status.get("message");
            return isSuccess;
        }

        @Override
        public void onPostExecute(Boolean success) {
            method.hideElement(progressBar);
            backup_button.setVisible(true);
            if (success) {
                customDialog.showAlertBox("Success", msg);
                Stage stage = (Stage) backup_button.getScene().getWindow();

                if (null != stage && stage.isShowing()) {
                    stage.close();
                }
            } else {
                customDialog.showAlertBox("Backup failed", msg);
            }
        }

        @Override
        public void progressCallback(Integer... params) {

        }
    }

    private Map<String, Object> startServerBackup(String path) {

        Map<String, Object> map = new HashMap<>();

        File file = new File(path);
        String app_id = getApplicationId();

        if (null == app_id) {
            map.put("message", "Backup failed. because the license is not active");
            map.put("is_success", false);
        } else {

            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(BACKUP_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            FileBody fileBody = new FileBody(file);

            builder.addPart("file", fileBody);
            builder.addTextBody("app_id", app_id);
            builder.addTextBody("extension", ".backup");
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);

            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {

                    String content = EntityUtils.toString(resEntity);

                    JSONObject jo = new JSONObject(content);

                    int statusCode = jo.getInt("statusCode");
                    String msg = jo.getString("message");

                    if (statusCode == 200) {
                        boolean isNotNull = checkExtraFile();
                        map.put("message", "Successfully Backup");
                        map.put("is_success", true);

                    } else {
                        map.put("message", "An error occurred during backup");
                        map.put("is_success", false);
                    }
                } else {
                    map.put("message", "An error occurred during backup");
                    map.put("is_success", false);
                }
            } catch (IOException e) {
                map.put("message", "Backup not completed. Please Try Again!");
                map.put("is_success", false);
            }
        }
        return map;
    }

    private Map<String, Object> startLocalBackup() {
        Map<String, Object> map;
        map = backup.startBackup();
        if (null != map) {
            boolean is_success = (boolean) map.get("is_success");
            if (is_success) {
                boolean isNotNull = checkExtraFile();

                if (isNotNull) {
                    map.put("message", "Successfully backup");
                    map.put("is_success", true);
                }

            } else {
                map.put("message", "An error occurred during backup");
                map.put("is_success", false);
            }
        } else {
            map.put("message", "An error occurred during backup");
            map.put("is_success", false);
        }
        return map;
    }

    private boolean checkExtraFile() {

        int requiredBackupFile = 5;
        final String FIRST_REGEX = "backup_";
        final String LAST_REGEX = ".backup";
        final String PATTERN = "dd_MM_yyyy";
        String path = System.getProperty("user.home") + "/HOTEL_DB_BACKUP/";
        File dir = new File(path);
        String[] list = dir.list((dir1, name) -> name.toLowerCase().endsWith(LAST_REGEX));

        List<String> strDateList = new ArrayList<>();
        for (String s : list) {
            strDateList.add(s.replaceAll(FIRST_REGEX, "").replaceAll(LAST_REGEX, ""));
        }
        List<LocalDate> dateList = new ArrayList<>();
        for (String ds : strDateList) {
            dateList.add(LocalDate.parse(ds, DateTimeFormatter.ofPattern(PATTERN)));
        }
        Collections.sort(dateList);

        int fileLength = dateList.size();
        if (fileLength >= requiredBackupFile) {
            int extraFileLength = fileLength - requiredBackupFile;
            for (int j = 0; j < extraFileLength; j++) {
                String filaName = FIRST_REGEX.concat(dateList.get(j).format(DateTimeFormatter.ofPattern(PATTERN))).concat(LAST_REGEX);
                String dPath = path.concat(filaName);

                File dFile = new File(dPath);
                if (dFile.exists()) {
                    dFile.delete();
                }
            }
        }


        return true;
    }

    private void checkExtraFile(Node source) {
        checkExtraFile();
        customDialog.showAlertBox("success", "Successfully backed up");
        Stage stage = (Stage) source.getScene().getWindow();
        if (null != stage && stage.isShowing()) {
            stage.close();
        }
    }

    private String getApplicationId() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = new DBConnection().getConnection();
            if (null == connection) {
                return null;
            }

            String query = "select application_id from tbl_license";
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            if (rs.next()) {
                String appId = rs.getString("application_id");
                return appId;
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConnection(connection, ps, rs);
        }
    }
}
