package com.jejutic.tdmessagefilter;

import com.jejutic.tdmessagefilter.api.ApiWorker;
import com.jejutic.tdmessagefilter.domain.Message;
import com.jejutic.tdmessagefilter.ui.UiAdapter;
import com.jejutic.tdmessagefilter.ui.UiAdapterImpl;
import it.tdlight.client.AuthenticationSupplier;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Example class for TDLight Java
 * <p>
 * <a href="https://tdlight-team.github.io/tdlight-docs">The documentation of the TDLight functions can be found here</a>
 */
public final class Runner implements Runnable {

    UiAdapter ui = new UiAdapterImpl();
    ApiWorker api = new ApiWorker();
    private final AtomicReference<Throwable> stopIssue = new AtomicReference<>();
    private final ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        Thread uiThread = new Thread(
                () -> ui.run(queue, stopIssue)
        );
        uiThread.start();
        Thread apiThread = new Thread(
                () -> api.run(ui, ui, queue, stopIssue)
//                    () -> api.run(AuthenticationSupplier.consoleLogin(), null, queue, stopIssue)
        );
        apiThread.start();

        try {
            while (!uiThread.isInterrupted() && !apiThread.isInterrupted()) {
                synchronized (stopIssue) {
                    if (!uiThread.isInterrupted() && !apiThread.isInterrupted()) {
                        stopIssue.wait();
                    }
                }
            }
            Throwable issue = stopIssue.get();
            if (issue != null) {
                System.out.println("Error with libraries or synchronization: " + issue.getMessage());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            uiThread.interrupt();
            apiThread.interrupt();
        }
    }
}