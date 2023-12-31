package com.jejutic.tdmessagefilter.ui;

import com.jejutic.tdmessagefilter.ui.controller.AskCodeController;
import it.tdlight.client.ClientInteraction;
import it.tdlight.client.InputParameter;
import it.tdlight.client.ParameterInfo;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ClientInteractionImpl implements ClientInteraction {

    private static final Logger log = LogManager.getLogger(ClientInteractionImpl.class);

    @Override
    public CompletableFuture<String> onParameterRequest(InputParameter parameter, ParameterInfo parameterInfo) {
        if (parameter == InputParameter.ASK_CODE) {
            log.info("Code for login requested");

            CompletableFuture<String> cf = new CompletableFuture<>();
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/scenes/askCode.fxml"));
                    Parent root = loader.load();
                    AskCodeController controller = loader.getController();
                    controller.setCodeCompletable(cf);

                    Stage stage = new Stage();
                    stage.setResizable(false);
                    stage.setTitle("Please enter your code");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to load new stage: " + e.getMessage());
                }
            });
            return cf;
        } else {
            throw new UnsupportedOperationException(
                    "Option " + parameter.name() + " is not supported"
            );
        }
    }
}
