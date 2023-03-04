package edu.byu.cs240.breed34.familymapclient.tasks;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

import java.util.Map;

public abstract class TaskBase implements Runnable {
    public static final String IS_SUCCESS_KEY = "IsSuccess";
    private final Handler messageHandler;

    public TaskBase(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public abstract void run();

    public void sendMessage(Map<String, Object> results) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        for (Map.Entry<String, Object> keyPair : results.entrySet()) {
            addToBundle(messageBundle, keyPair);
        }

        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }

    public void addToBundle(Bundle messageBundle, Map.Entry<String, Object> keyPair) {
        if (keyPair.getValue() instanceof Boolean) {
            messageBundle.putBoolean(keyPair.getKey(), (Boolean)keyPair.getValue());
        }
        else if (keyPair.getValue() instanceof String) {
            messageBundle.putString(keyPair.getKey(), (String)keyPair.getValue());
        }
    }
}
