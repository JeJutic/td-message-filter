package com.jejutic.tdmessagefilter.ui.controller;

import com.jejutic.tdmessagefilter.common.Message;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MessagesController {
    private Stage tagListStage;
    public ListView<Message> messages;
    public Button tagListButton;

    public void initialize(CompletableFuture<SimpleTelegramClient> clientCf,
                           HostServices hostServices, Set<String> tagList) {

        messages.getSelectionModel().selectedItemProperty().addListener((observableValue, ignored1, ignored2) -> {
            Message message = observableValue.getValue();
            clientCf.thenAccept((client) ->
                    client.send(
                            new TdApi.GetMessageLink(
                                    message.chatId(),
                                    message.id(),
                                    0,
                                    false,
                                    false),
                            messageLinkResult -> {
                                String messageLink = messageLinkResult.get().link;
                                hostServices.showDocument(messageLink);
                            })
            );
        });

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/tagList.fxml"));
            Parent root = loader.load();
            TagListController controller = loader.getController();
            controller.setTagListSource(tagList);

            tagListStage = new Stage();
            tagListStage.setResizable(false);
            tagListStage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load new stage: " + e.getMessage());
        }
    }

    @FXML
    private void openTagListClick(ActionEvent actionEvent) {
        tagListStage.show();
    }

    public void addMessage(Message message) {
        messages.getItems().add(message);
    }
}
