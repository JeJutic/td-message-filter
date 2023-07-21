package com.jejutic.tdmessagefilter.ui;

import com.jejutic.tdmessagefilter.Runner;
import it.tdlight.client.AuthenticationSupplier;

public class UiAdapterImpl implements UiAdapter {
    @Override
    public synchronized AuthenticationSupplier<?> authenticationSupplier() {

        notify();
        return null;
    }

    @Override
    public synchronized void run(Runner.StopIssue stopIssue) {

    }
}
