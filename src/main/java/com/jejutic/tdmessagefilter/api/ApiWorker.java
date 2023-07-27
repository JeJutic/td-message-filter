package com.jejutic.tdmessagefilter.api;

import com.jejutic.tdmessagefilter.ui.NewMessageHandler;
import com.jejutic.tdmessagefilter.ui.UpdateAuthorizationStateHandler;
import it.tdlight.Init;
import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import it.tdlight.util.UnsupportedNativeLibraryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class ApiWorker {

    private static final Logger log = LogManager.getLogger(ApiWorker.class);

    public void run(CompletableFuture<SimpleTelegramClient> clientCf,
                    AuthenticationSupplier<?> authenticationData,
                    ClientInteraction clientInteraction,
                    NewMessageHandler newMessageHandler,
                    UpdateAuthorizationStateHandler updateAuthorizationStateHandler)
            throws UnsupportedNativeLibraryException {

        log.info("Initialising TD native library...");
        Init.init();
        log.info("TD native library initialised");

        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {

            APIToken apiToken = APIToken.example();

            TDLibSettings settings = TDLibSettings.create(apiToken);

            Path sessionPath = Paths.get("session");
            settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
            settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

            settings.setChatInfoDatabaseEnabled(false);
            settings.setFileDatabaseEnabled(false);
            settings.setMessageDatabaseEnabled(false);

            SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);

            clientBuilder.setClientInteraction(clientInteraction);
            clientBuilder.addUpdateHandler(TdApi.UpdateAuthorizationState.class, updateAuthorizationStateHandler);
            clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class, newMessageHandler);

            log.debug("TDLight library is ready to build the client");

            SimpleTelegramClient client = clientBuilder.build(authenticationData);
            newMessageHandler.setSimpleTelegramClient(client);

            if (clientCf.complete(client)) {
                log.info("TDLight client is ready. Waiting for exit");
                client.waitForExit();
                log.info("TDLight client exited");
            } else {
                log.debug(
                        "The application started closing before client became available: {}",
                        clientCf
                );
            }
        } catch (InterruptedException e) {
            log.error("TD thread was suddenly interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}