package com.jejutic.tdmessagefilter.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.concurrent.CompletableFuture;

public class AskCodeController {

    public TextField code;
    public Button submitCodeButton;
    private CompletableFuture<String> codeCompletable;

    @FXML
    private void clickSubmit(ActionEvent actionEvent) {
        codeCompletable.complete(code.getText());

        ControllerUtility.closeSummonedStage(actionEvent);
    }

    public void setCodeCompletable(CompletableFuture<String> codeCompletable) {
        this.codeCompletable = codeCompletable;
    }
}
