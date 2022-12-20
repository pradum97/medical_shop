package com.techwhizer.medicalshop.method;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Year;

public class StaticData {

    public String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

    public ObservableList<String> getGender() {

        return FXCollections.observableArrayList("Male", "Female", "Other");
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

        return FXCollections.observableArrayList("REGULAR", "GST");
    }

    public  ObservableList<String> getPaymentMode(){

        return FXCollections.observableArrayList("CASH","PHONE PE","GOOGLE PE" , "UPI" , "OTHER");
    }

    public ObservableList<String> getDiscountType() {

        return FXCollections.observableArrayList("PERCENTAGE","FLAT");
    }
    public ObservableList<String> getUnit() {
        return FXCollections.observableArrayList("STRIP","PCS");
    }
    public ObservableList<String> getQuantityUnit() {
        return FXCollections.observableArrayList("PCS");
    }
    public ObservableList<String> getAccountStatus() {

        return FXCollections.observableArrayList("Inactive","Active" );
    }

   public ObservableList<String> tabUnit = FXCollections.observableArrayList("TAB","STRIP");
   public ObservableList<String> strip = FXCollections.observableArrayList("STRIP");
  public   ObservableList<String> pcsUnit = FXCollections.observableArrayList("PCS");
  public   ObservableList<String> stockFilter = FXCollections.observableArrayList("EXPIRED","CLOSE","NARCOTIC ITEMS","PROHIBIT","LOW QTY","OUT OF STOCK");

}
