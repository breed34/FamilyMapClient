package edu.byu.cs240.breed34.familymapclient.tasks;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

public abstract class HttpTask implements Runnable {
    private final Handler messageHandler;

    public HttpTask(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public abstract void run();

    public void sendBooleanMessage(String key, boolean result) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean(key, result);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }

    public void sendStringMessage(String key, String result) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putString(key, result);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
