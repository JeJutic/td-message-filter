package com.jejutic.tdmessagefilter.ui;

import com.jejutic.tdmessagefilter.common.Message;
import com.jejutic.tdmessagefilter.ui.controller.MessagesController;
import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.jni.TdApi;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateAuthorizationStateHandler implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private static final Logger log = LogManager.getLogger(UpdateAuthorizationStateHandler.class);
    private final MessagesController messagesController;

    public UpdateAuthorizationStateHandler(MessagesController messagesController) {
        this.messagesController = messagesController;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        String text;
        TdApi.AuthorizationState authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            text = "Logged in. New messages will appear here";
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            text = "Closing...";
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            text = "Closed";
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            text = "Logging out...";
        } else {
            text = null;
        }
        log.info("New authorization state: {}", text);

        if (text != null) {
            Platform.runLater(() ->
                    messagesController.addMessage(new Message(0, 0, "[System]", text))
            );
        }
    }
}
