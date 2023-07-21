package com.jejutic.tdmessagefilter.ui;

import com.jejutic.tdmessagefilter.Runner;
import it.tdlight.client.AuthenticationSupplier;

public interface UiAdapter {

    AuthenticationSupplier<?> authenticationSupplier();

    void run(Runner.StopIssue stopIssue);
}
