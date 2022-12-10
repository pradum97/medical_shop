package com.techwhizer.medicalshop.method;

import com.techwhizer.medicalshop.CustomDialog;
import javafx.application.Platform;

import java.io.IOException;

public class InternetConnection {
    public static boolean checkInternetConnection(boolean showAlertDialog) {

        int x = 0;
        try {
            Process process = Runtime.getRuntime().exec("ping www.google.com");
            x = process.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        if (x!=0){
            if (showAlertDialog){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new CustomDialog().showAlertBox("Internet not available","Please connect to the internet");
                    }
                });
            }
        }

        return x == 0;
    }
}