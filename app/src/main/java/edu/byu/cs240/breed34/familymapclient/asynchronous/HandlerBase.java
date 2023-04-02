package edu.byu.cs240.breed34.familymapclient.asynchronous;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import edu.byu.cs240.breed34.familymapclient.asynchronous.tasks.TaskBase;

/**
 * A base class for handling messages
 * received from tasks.
 */
public class HandlerBase extends Handler {
    /**
     * The callback to be executed if task is successful.
     */
    private HandlerCallback callback;

    /**
     * The callback to be executed if task is unsuccessful.
     */
    private HandlerCallback error;

    public HandlerBase(HandlerCallback callback, HandlerCallback error) {
        this.callback = callback;
        this.error = error;
    }

    /**
     * Handles the message received from the task.
     *
     * @param message the message received from the task.
     */
    @Override
    public void handleMessage(Message message) {
        // Check success.
        Bundle bundle = message.getData();
        boolean isSuccess = bundle.getBoolean(TaskBase.IS_SUCCESS_KEY, false);

        // Call appropriate callback.
        if (isSuccess) {
            callback.run(bundle);
        }
        else {
            error.run(bundle);
        }
    }
}
