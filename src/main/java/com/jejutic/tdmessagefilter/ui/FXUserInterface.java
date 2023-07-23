package com.jejutic.tdmessagefilter.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FXUserInterface extends Application implements UserInterface {

    @Override
    public void start() {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/mainScene.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public String askPhone() {
        return null;
    }

    @Override
    public String askCode() {
        return null;
    }

    @Override
    public void receiveNewMessages() {

    }
}
