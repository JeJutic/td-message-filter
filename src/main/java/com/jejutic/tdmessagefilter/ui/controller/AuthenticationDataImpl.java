package com.jejutic.tdmessagefilter.ui.controller;

import it.tdlight.client.AuthenticationData;

class AuthenticationDataImpl implements AuthenticationData {
    final boolean isQr;
    final boolean isBot;
    final String botToken;
    final String phoneNumber;

    AuthenticationDataImpl(boolean isQr, boolean isBot, String botToken, String phoneNumber) {
        if (((isQr || phoneNumber != null) && isBot) || (isBot && botToken == null)) {
            throw new IllegalArgumentException();
        }
        this.isQr = isQr;
        this.isBot = isBot;
        this.botToken = botToken;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean isQrCode() {
        return isQr;
    }

    @Override
    public boolean isBot() {
        return isBot;
    }

    @Override
    public String getUserPhoneNumber() {
        if (isBot || isQr) {
            throw new UnsupportedOperationException("This is not a user");
        }
        return phoneNumber;
    }

    @Override
    public String getBotToken() {
        if (!isBot || isQr) {
            throw new UnsupportedOperationException("This is not a bot");
        }
        return botToken;
    }
}