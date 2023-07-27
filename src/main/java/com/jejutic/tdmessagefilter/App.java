package com.jejutic.tdmessagefilter;

import com.jejutic.tdmessagefilter.api.ApiWorker;
import com.jejutic.tdmessagefilter.ui.AuthenticationSupplierImpl;
import com.jejutic.tdmessagefilter.ui.ClientInteractionImpl;
import com.jejutic.tdmessagefilter.ui.NewMessageHandler;
import com.jejutic.tdmessagefilter.ui.UpdateAuthorizationStateHandler;
import com.jejutic.tdmessagefilter.ui.controller.MessagesController;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.util.UnsupportedNativeLibraryException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapdb.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class App extends Application {

    private static final Logger log;

    static {
        try {
            // you need to do something like below instaed of Logger.getLogger(....);
            log = LogManager.getLogger(App.class);
        } catch (Throwable th) {
            throw new RuntimeException("unable to load log");
        }
    }

    private static Set<String> tagList;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String path = Paths.get("tags.db").toString();

        try(DB db = DBMaker.fileDB(path).make()) {
            tagList = (Set<String>) db.hashSet("tagList").createOrOpen();
            Application.launch();
        }
    }

    private void startApi(Stage primaryStage,
                            CompletableFuture<SimpleTelegramClient> clientMark,
                            MessagesController messagesController) {
        final Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws UnsupportedNativeLibraryException {
                new ApiWorker().run(
                        clientMark,
                        new AuthenticationSupplierImpl(),
                        new ClientInteractionImpl(),
                        new NewMessageHandler(messagesController, tagList),
                        new UpdateAuthorizationStateHandler(messagesController)
                );
                return null;
            }
        };

        task.setOnFailed(e -> {
            Throwable problem = task.getException();
            log.error("TD thread suddenly failed", problem);
            primaryStage.close();
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        CompletableFuture<SimpleTelegramClient> clientCf = new CompletableFuture<>();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/main.fxml"));
        Parent root = loader.load();

        MessagesController messagesController = loader.getController();
        messagesController.initialize(clientCf, getHostServices(), tagList);
        startApi(primaryStage, clientCf, messagesController);

        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest((event) -> {
            log.info("Primary stage is being closed...");

            if (!clientCf.complete(null)) {
                log.info("Explicitly closing TD thread");
                try {
                    clientCf.get().sendClose();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        primaryStage.show();

        Platform.setImplicitExit(true);
    }
}