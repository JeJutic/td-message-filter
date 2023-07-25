package com.jejutic.tdmessagefilter.api;

import com.jejutic.tdmessagefilter.ui.NewMessageHandler;
import it.tdlight.Init;
import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.AuthorizationState;
import it.tdlight.util.UnsupportedNativeLibraryException;
import javafx.util.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

public final class ApiWorker {

    public void run(AtomicReference<Pair<SimpleTelegramClient, Boolean>> sharedClientMark,
                    Pair<SimpleTelegramClient, Boolean> startingState,
                    AuthenticationSupplier<?> authenticationData,
                    ClientInteraction clientInteraction,
                    NewMessageHandler newMessageHandler)
            throws UnsupportedNativeLibraryException {
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
            clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class, newMessageHandler);

            System.out.println("I wanna build");

            SimpleTelegramClient client = clientBuilder.build(authenticationData);
            newMessageHandler.setSimpleTelegramClient(client);

            if (sharedClientMark.compareAndSet(startingState, new Pair<>(client, false))) {
                System.out.println("Waiting for exit");
                client.waitForExit();
                System.out.println("client exited");
            } else {
                System.out.println(sharedClientMark.get().getKey() + " " + sharedClientMark.get().getValue());
            }
        } catch (InterruptedException e) {
            System.out.println("Msg: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
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