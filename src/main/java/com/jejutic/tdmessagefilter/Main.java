package com.jejutic.tdmessagefilter;

import com.jejutic.tdmessagefilter.api.ApiWorker;
import com.jejutic.tdmessagefilter.ui.AuthenticationSupplierImpl;
import com.jejutic.tdmessagefilter.ui.ClientInteractionImpl;
import com.jejutic.tdmessagefilter.ui.NewMessageHandler;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.util.UnsupportedNativeLibraryException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public final class Main extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    private void startApi(Stage primaryStage,
                            AtomicReference<Pair<SimpleTelegramClient, Boolean>> clientMark,
                            Pair<SimpleTelegramClient, Boolean> startingState) {
        final Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws UnsupportedNativeLibraryException {
                new ApiWorker().run(
                        clientMark,
                        startingState,
                        new AuthenticationSupplierImpl(),
                        new ClientInteractionImpl(),
                        new NewMessageHandler()
                );
                return null;
            }
        };

        task.setOnFailed(e -> {
            Throwable problem = task.getException();
            System.out.println("API thread has stopped: " + problem.getMessage());
            primaryStage.close();
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pair<SimpleTelegramClient, Boolean> startingState = new Pair<>(null, false);
        AtomicReference<Pair<SimpleTelegramClient, Boolean>> clientMark = new AtomicReference<>(startingState);
        startApi(primaryStage, clientMark, startingState);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainScene.fxml"));

        primaryStage.setScene(new Scene(loader.load()));
//        primaryStage.setOnHiding((event) -> {
//            System.out.println("hiding");
//            apiThread.interrupt();
//        });
        primaryStage.setOnCloseRequest((event) -> {
            System.out.println("closing");
            if (!clientMark.compareAndSet(new Pair<>(null, false), new Pair<>(null, true))) {
                clientMark.get().getKey().sendClose();
            }
        });


        primaryStage.show();

        Platform.setImplicitExit(true);
    }
}