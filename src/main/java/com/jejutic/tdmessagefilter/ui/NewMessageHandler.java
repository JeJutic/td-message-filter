package com.jejutic.tdmessagefilter.ui;

import com.jejutic.tdmessagefilter.common.Message;
import com.jejutic.tdmessagefilter.ui.controller.MessagesController;
import it.tdlight.jni.TdApi.Chat;
import it.tdlight.jni.TdApi.MessageContent;
import it.tdlight.jni.TdApi;
import javafx.application.Platform;

import java.util.Set;

public class NewMessageHandler extends AbstractNewMessageHandler {

    private final MessagesController messagesController;
    private final Set<String> tagList;

    public NewMessageHandler(MessagesController messagesController, Set<String> tagList) {
        this.messagesController = messagesController;
        this.tagList = tagList;
    }

    private boolean containsTag(String s) {
        for (String tag : tagList) {
            if (s.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUpdate(TdApi.UpdateNewMessage update) {
        MessageContent messageContent = update.message.content;
        long id = update.message.id;

        if (messageContent instanceof TdApi.MessageText messageText) {
            String text = messageText.text.text;
            if (!containsTag(text) && !tagList.isEmpty()) {
                return;
            }

            client.send(new TdApi.GetChat(update.message.chatId), (chatIdResult) -> {
                Chat chat = chatIdResult.get();
                String chatName = chat.title;

                Platform.runLater(() ->
                        messagesController.addMessage(new Message(id, chat.id, chatName, text))
                );
            });
        }
        // We handle only text messages
    }
}
