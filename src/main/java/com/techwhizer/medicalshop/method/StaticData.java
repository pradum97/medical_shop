package com.techwhizer.medicalshop.method;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Year;

public class StaticData {

    public String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

    public ObservableList<String> getGender() {

        return FXCollections.observableArrayList("Male", "Female", "Other");
    }

 public ObservableList<Integer> getPcsPerPacketList() {
        return FXCollections.observableArrayList(6 ,1,2,3,4,5,7,8,9 , 10 , 11 , 12);
    }

    public ObservableList<Integer> getYear() {

        int year = Year.now().getValue();

        ObservableList<Integer> yearList = FXCollections.observableArrayList();

        for (int i = (year-4); i < (year+15); i++) {
          yearList.add(i);
        }

        return yearList;
    }


    public ObservableList<String> getMonth() {

        ObservableList<String> yearList = FXCollections.observableArrayList();

        for (int i = 1; i <= 12; i++) {
            if (i<10){
                yearList.add("0"+i);
            }else {
                yearList.add(String.valueOf(i));
            }

        }

        return yearList;
    }
 public ObservableList<String> stockFilter() {

        return FXCollections.observableArrayList("ALL", "Out Of Stock", "LOW" , "MEDIUM" , "HIGH");
    }

    public ObservableList<String> getBillingType() {

        return FXCollections.observableArrayList("REGULAR", "GST" ,"KITTY PARTY");
    }

    public  ObservableList<String> getPaymentMode(){

        return FXCollections.observableArrayList("CASH","PHONE PE","GOOGLE PE" , "UPI" , "OTHER");
    }

    public ObservableList<String> getDiscountType() {

        return FXCollections.observableArrayList("PERCENTAGE","FLAT");
    }
    public ObservableList<String> getUnit() {
        return FXCollections.observableArrayList("STRIP","PCS","PKT","INJ","TAB","CAP","KG","GRAM","ML","LITRE","MG");
    }
    public ObservableList<String> getQuantityUnit() {
        return FXCollections.observableArrayList("STRIP","PCS","TAB","CAP");
    }
    public ObservableList<String> getAccountStatus() {

        return FXCollections.observableArrayList("Inactive","Active" );
    }

}
