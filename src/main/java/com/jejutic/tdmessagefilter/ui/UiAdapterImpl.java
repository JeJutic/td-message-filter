package com.jejutic.tdmessagefilter.ui;

import com.jejutic.tdmessagefilter.domain.Message;
import it.tdlight.client.*;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class UiAdapterImpl implements UiAdapter {

    private AtomicReference<CompletableFuture<AuthenticationData>> authState = new AtomicReference<>();
    private CompletableFuture<String> request;
    private InputParameter parameter;
    private ParameterInfo parameterInfo;
    private boolean closed = false;

    @Override
    public CompletableFuture<AuthenticationData> get() {
        CompletableFuture<AuthenticationData> cf = new CompletableFuture<>();
        if (authState.compareAndSet(null, cf)) {
            synchronized (this) {
                notify();
            }
            return cf;
        } else {
            return authState.get();
        }
    }

    @Override
    public synchronized CompletableFuture<String> onParameterRequest(InputParameter parameter, ParameterInfo parameterInfo) {
        try {
            while (request != null) {
                wait();
            }

            this.parameter = parameter;
            this.parameterInfo = parameterInfo;
            request = new CompletableFuture<>();
            notify();
            return request;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }
    }

    @Override
    public synchronized void run(Queue<Message> queue, AtomicReference<Throwable> stopIssue) {
        try {
            while (true) {
                if (authState.get() != null) {
                    authState.get().complete(new AuthenticationData() {
                        @Override
                        public boolean isQrCode() {
                            return false;
                        }

                        @Override
                        public boolean isBot() {
                            return false;
                        }

                        @Override
                        public String getUserPhoneNumber() {
                            return "79530080157";
                        }

                        @Override
                        public String getBotToken() {
                            return null;
                        }
                    });
                    authState = new AtomicReference<>();
                    System.out.println("AuthenticationData sent");
                } else if (request != null) {
                    String response = "empty";
                    switch (parameter) {
                        case ASK_FIRST_NAME -> {
                            response = "Mary";
                            System.out.println("asked first name");
                        }
                        case ASK_LAST_NAME -> {
                            response = "Sue";
                            System.out.println("asked last name");
                        }
                        case ASK_CODE -> System.out.println("asked code");
                        case ASK_PASSWORD -> System.out.println("asked password");
                        case NOTIFY_LINK -> System.out.println("Something with qr code");
                        case TERMS_OF_SERVICE -> System.out.println("Terms of service");
                    }
                    request.complete(response);
                    request = null;
                    System.out.println("Request completed");
                } else if (!queue.isEmpty()) {
                    while (!queue.isEmpty()) {
                        System.out.println(queue.poll().text());
                    }
                } else if (closed) {
                    Thread.currentThread().interrupt();
                    stopIssue.notify();
                    break;
                } else {
                    System.out.println("started waiting");
                    wait();
                    System.out.println("stopped waiting");
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
            stopIssue.compareAndSet(null, e);
            stopIssue.notify();
        }
    }
}
