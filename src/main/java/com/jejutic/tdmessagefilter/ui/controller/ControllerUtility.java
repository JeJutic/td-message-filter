package com.jejutic.tdmessagefilter.ui.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ControllerUtility {

    static void closeSummonedStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        var onCloseRequest = stage.getOnCloseRequest();
        if (onCloseRequest != null) {
            onCloseRequest.handle(null);
        }
        stage.close();
    }
}
