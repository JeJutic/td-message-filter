module com.jejutic.tdmessagefilter {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires tdlight.java;
    requires tdlight.api;
    requires mapdb;
    requires org.apache.logging.log4j;

    opens com.jejutic.tdmessagefilter;
    opens com.jejutic.tdmessagefilter.ui;
    opens com.jejutic.tdmessagefilter.ui.controller;
}