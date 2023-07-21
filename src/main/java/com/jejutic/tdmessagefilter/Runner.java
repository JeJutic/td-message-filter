package com.jejutic.tdmessagefilter;

import com.jejutic.tdmessagefilter.api.ApiWorker;
import com.jejutic.tdmessagefilter.ui.UiAdapter;
import com.jejutic.tdmessagefilter.ui.UiAdapterImpl;

/**
 * Example class for TDLight Java
 * <p>
 * <a href="https://tdlight-team.github.io/tdlight-docs">The documentation of the TDLight functions can be found here</a>
 */
public final class Runner implements Runnable {

    UiAdapter ui = new UiAdapterImpl();
    ApiWorker api = new ApiWorker();
    private final StopIssue stopIssue = new StopIssue();

    public static class StopIssue {
        private Throwable cause = null;

        public void setCause(Throwable cause) {
            this.cause = cause;
        }
    }

    @Override
    public void run() {
        try {
            Thread uiThread = new Thread(ui);
            Thread apiThread = new Thread(() ->
                    api.run(ui.authenticationSupplier(), stopIssue));

            while (!uiThread.isAlive() && !apiThread.isAlive() ) {
                synchronized (stopIssue) {
                    stopIssue.wait();
                }
            }
            if (stopIssue.cause != null) { // app was just closed or internal error raised in gui or api lib
                System.out.println(stopIssue.cause.getMessage());
            }
            uiThread.interrupt();
            apiThread.interrupt();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}