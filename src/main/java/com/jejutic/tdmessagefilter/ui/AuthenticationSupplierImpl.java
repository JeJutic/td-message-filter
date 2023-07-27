package com.jejutic.tdmessagefilter.ui;

import com.jejutic.tdmessagefilter.ui.controller.AuthenticationDataController;
import it.tdlight.client.AuthenticationData;
import it.tdlight.client.AuthenticationSupplier;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AuthenticationSupplierImpl implements AuthenticationSupplier<AuthenticationData> {

    private static final Logger log = LogManager.getLogger(AuthenticationSupplierImpl.class);

    @Override
    public CompletableFuture<AuthenticationData> get() {
        CompletableFuture<AuthenticationData> cf = new CompletableFuture<>();

        log.info("Authentication data requested");
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/authData.fxml"));
                Parent root = loader.load();
                AuthenticationDataController controller = loader.getController();
                controller.setAuthData(cf);

                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setTitle("Please enter your phone number");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException("Unable to load new stage: " + e.getMessage());
            }
        });
        return cf;
    }
}
