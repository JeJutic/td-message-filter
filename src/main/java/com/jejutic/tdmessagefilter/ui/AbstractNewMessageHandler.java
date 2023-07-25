package com.jejutic.tdmessagefilter.ui;

import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;

public abstract class AbstractNewMessageHandler implements GenericUpdateHandler<TdApi.UpdateNewMessage> {

    SimpleTelegramClient client;

    public void setSimpleTelegramClient(SimpleTelegramClient client) {
        this.client = client;
    }
}
