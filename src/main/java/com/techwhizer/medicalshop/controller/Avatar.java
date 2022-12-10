package com.techwhizer.medicalshop.controller;

import com.techwhizer.medicalshop.CustomDialog;
import com.techwhizer.medicalshop.ImageLoader;
import com.techwhizer.medicalshop.Main;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Avatar implements Initializable {
    public GridPane gridPane;
    
   private String path = "img/Avatar/avtar_";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        int cols=2, colCnt = 0, rowCnt = 0;

        for (int i = 2; i <= 16; i++) {
            InputStream is = null;

            ImageView iv = new ImageView();
            iv.setPreserveRatio(true);
            iv.setFitWidth(60);
            iv.setFitHeight(60);
            final  int i1 = i;
            iv.setStyle("-fx-cursor: hand");
            iv.setOnMouseClicked(event -> {

                String imgAvatarPath = "avtar_"+i1+".png";


                Main.primaryStage.setUserData(imgAvatarPath);

                Stage stage =  new  CustomDialog().stage2;

                if (stage.isShowing()){
                    stage.hide();
                }
            });

            iv.setImage(new ImageLoader().load(path+i+".png"));

            gridPane.add(iv, colCnt, rowCnt);
            colCnt++;

            if (colCnt>cols) {
                rowCnt++;
                colCnt=0;
            }

        }
    }
}
