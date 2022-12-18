module com.techwhizer.medicalshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jfx.asynctask;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires org.apache.httpcomponents.httpcore;
    requires org.json;
    requires org.apache.httpcomponents.httpclient;
    requires java.base;
    requires jasperreports;
    opens com.techwhizer.medicalshop to javafx.fxml;
    exports com.techwhizer.medicalshop;

    opens com.techwhizer.medicalshop.controller to javafx.fxml;
    exports com.techwhizer.medicalshop.controller;
    opens com.techwhizer.medicalshop.controller.auth to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.auth;

    opens com.techwhizer.medicalshop.method to javafx.fxml;
    exports com.techwhizer.medicalshop.method;
    opens com.techwhizer.medicalshop.model to javafx.fxml;
    exports com.techwhizer.medicalshop.model;
    opens com.techwhizer.medicalshop.model.chooserModel to javafx.fxml;
    exports com.techwhizer.medicalshop.model.chooserModel;

    opens com.techwhizer.medicalshop.controller.product.gst to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.product.gst;

    opens com.techwhizer.medicalshop.controller.user to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.user;

    opens com.techwhizer.medicalshop.controller.update.user to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.update.user;

    opens com.techwhizer.medicalshop.controller.product to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.product;

    opens com.techwhizer.medicalshop.controller.update.product.gst to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.update.product.gst;

    opens com.techwhizer.medicalshop.controller.product.dealer to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.product.dealer;
    exports com.techwhizer.medicalshop.controller.product.discount;
    opens com.techwhizer.medicalshop.controller.product.discount to javafx.fxml;

    exports com.techwhizer.medicalshop.controller.update.product;
    opens com.techwhizer.medicalshop.controller.update.product to javafx.fxml;

    exports com.techwhizer.medicalshop.controller.product.purchase;
    opens com.techwhizer.medicalshop.controller.product.purchase to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.chooser;
    opens com.techwhizer.medicalshop.controller.chooser to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.product.mr;
    opens com.techwhizer.medicalshop.controller.product.mr to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.update;
    opens com.techwhizer.medicalshop.controller.update to javafx.fxml;

    exports com.techwhizer.medicalshop.controller.product.patient;
    opens com.techwhizer.medicalshop.controller.product.patient to javafx.fxml;
    exports com.techwhizer.medicalshop.controller.dashboard;
    opens com.techwhizer.medicalshop.controller.dashboard to javafx.fxml;

    exports com.techwhizer.medicalshop.controller.product.doctor;
    opens com.techwhizer.medicalshop.controller.product.doctor to javafx.fxml;


}