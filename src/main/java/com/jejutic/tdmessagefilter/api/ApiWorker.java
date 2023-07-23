package com.jejutic.tdmessagefilter.api;

import com.jejutic.tdmessagefilter.domain.Message;
import it.tdlight.Init;
import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.AuthorizationState;
import it.tdlight.jni.TdApi.MessageContent;
import it.tdlight.util.UnsupportedNativeLibraryException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Example class for TDLight Java
 * <p>
 * <a href="https://tdlight-team.github.io/tdlight-docs">The documentation of the TDLight functions can be found here</a>
 */
public final class ApiWorker {

    private static SimpleTelegramClient client;

    public void run(AuthenticationSupplier<?> authenticationData, ClientInteraction clientInteraction,
                    final Queue<Message> queue, AtomicReference<Throwable> stopIssue) {
        try {
            // Initialize TDLight native libraries
            Init.init();

            // Create the client factory
            try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {

                // Obtain the API token
                //
                // var apiToken = new APIToken(your-api-id-here, "your-api-hash-here");
                //
                APIToken apiToken = APIToken.example();

                // Configure the client
                TDLibSettings settings = TDLibSettings.create(apiToken);

                // Configure the session directory
                Path sessionPath = Paths.get("example-tdlight-session");
                settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
                settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

                // Prepare a new client builder
                SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);

                // Configure the authentication info
                // Replace with AuthenticationSupplier.consoleLogin(), or .user(xxx), or .bot(xxx);
                //            SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.testUser(7381);
//                AuthenticationSupplier<?> authenticationData = AuthenticationSupplier.consoleLogin();

                // This is an example, remove this line to use the real telegram datacenters!
//                settings.setUseTestDatacenter(true);

                clientBuilder.setClientInteraction(clientInteraction);

                // Add an example update handler that prints when the bot is started
                clientBuilder.addUpdateHandler(TdApi.UpdateAuthorizationState.class, ApiWorker::onUpdateAuthorizationState);

                // Add an example update handler that prints every received message
                clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class,
                        (update) -> onUpdateNewMessage(update, queue));

                System.out.println("I wanna build");

                // Create and start the client
                client = clientBuilder.build(authenticationData);

                System.out.println("Waiting for exit");

                // Wait for exit
                client.waitForExit();
            }
        } catch (UnsupportedNativeLibraryException | InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
            stopIssue.compareAndSet(null, e);
            stopIssue.notify();
        }
    }

    /**
     * Print new messages received via updateNewMessage
     */
    private static void onUpdateNewMessage(TdApi.UpdateNewMessage update, Queue<Message> queue) {
        // Get the message content
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

//        // Get the chat title
//        client.send(new TdApi.GetChat(update.message.chatId), chatIdResult -> {
//            // Get the chat response
//            Chat chat = chatIdResult.get();
//            // Get the chat name
//            String chatName = chat.title;
//
//            // Print the message
//            System.out.printf("Received new message from chat %s: %s%n", chatName, text);
//        });
        queue.add(new Message(text));
    }

    /**
     * Print the bot status
     */
    private static void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        AuthorizationState authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            System.out.println("Logged in");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            System.out.println("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out...");
        }
    }
}