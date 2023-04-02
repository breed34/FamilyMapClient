package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

import java.util.Map;

/**
 * A base class for asynchronous tasks.
 */
public abstract class TaskBase implements Runnable {
    /**
     * The key for the result of the task stored in the bundle.
     */
    public static final String IS_SUCCESS_KEY = "IsSuccess";

    /**
     * The handler to use in the execution of the task.
     */
    private final Handler messageHandler;

    public TaskBase(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * The method to be called when the task
     * is executed.
     */
    public abstract void run();

    /**
     * Creates and sends the message containing
     * data from the task.
     *
     * @param results the data from the task.
     */
    protected void sendMessage(Map<String, Object> results) {
        Message message = Message.obtain();

        // Setup bundle.
        Bundle messageBundle = new Bundle();
        addToBundle(messageBundle, results);

        // Add bundle to message and send message.
        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }

    /**
     * Adds data to the message bundle.
     *
     * @param messageBundle the message bundle.
     * @param results the data to add to the bundle.
     */
    private void addToBundle(Bundle messageBundle, Map<String, Object> results) {
        for (Map.Entry<String, Object> keyPair : results.entrySet()) {
            // Add boolean to bundle.
            if (keyPair.getValue() instanceof Boolean) {
                messageBundle.putBoolean(keyPair.getKey(), (Boolean)keyPair.getValue());
            }
            // Add string to bundle.
            else if (keyPair.getValue() instanceof String) {
                messageBundle.putString(keyPair.getKey(), (String)keyPair.getValue());
            }
        }
    }
}
