package com.jejutic.tdmessagefilter.common;


public record Message(long id, long chatId, String chatName, String text) {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Override
    public String toString() {
        return '[' + chatName() + ']' + LINE_SEPARATOR + text();
    }
}
