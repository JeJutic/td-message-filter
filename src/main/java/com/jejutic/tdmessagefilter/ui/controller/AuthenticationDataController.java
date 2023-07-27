package com.jejutic.tdmessagefilter.ui.controller;

import it.tdlight.client.AuthenticationData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.concurrent.CompletableFuture;

public class AuthenticationDataController {

    public TextField phoneNumber;
    public Button submitDataButton;
    private CompletableFuture<AuthenticationData> authData;

    @FXML
    private void clickSubmit(ActionEvent actionEvent) {
        authData.complete(new AuthenticationDataImpl(
                false, false, null, phoneNumber.getText()
        ));

        ControllerUtility.closeSummonedStage(actionEvent);
    }// TODO: consider other login options

    public void setAuthData(CompletableFuture<AuthenticationData> authData) {
        this.authData = authData;
    }
}
