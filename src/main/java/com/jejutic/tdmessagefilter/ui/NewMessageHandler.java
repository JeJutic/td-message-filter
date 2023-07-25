package com.jejutic.tdmessagefilter.ui;

import it.tdlight.jni.TdApi.Chat;
import it.tdlight.jni.TdApi.MessageContent;
import it.tdlight.jni.TdApi;
import java.util.Objects;

public class NewMessageHandler extends AbstractNewMessageHandler {

    @Override
    public void onUpdate(TdApi.UpdateNewMessage update) {
        MessageContent messageContent = update.message.content;

        // Get the message text
        String text;
        if (messageContent instanceof TdApi.MessageText messageText) {
            // Get the text of the text message
            text = messageText.text.text;
        } else {
            // We handle only text messages, the other messages will be printed as their type
            text = String.format("(%s)", messageContent.getClass().getSimpleName());
        }

        Objects.requireNonNull(client);
        // Get the chat title
        client.send(new TdApi.GetChat(update.message.chatId), (chatIdResult) -> {
            // Get the chat response
            Chat chat = chatIdResult.get();
            // Get the chat name
            String chatName = chat.title;

            // Print the message
            System.out.printf("Received new message from chat %s: %s%n", chatName, text);
        });
    }
}
