package com.jejutic.tdmessagefilter.ui;

import com.jejutic.tdmessagefilter.domain.Message;
import it.tdlight.client.AuthenticationData;
import it.tdlight.client.AuthenticationSupplier;
import it.tdlight.client.ClientInteraction;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public interface UiAdapter extends AuthenticationSupplier<AuthenticationData>, ClientInteraction {

    void run(Queue<Message> queue, AtomicReference<Throwable> stopIssue);
}
