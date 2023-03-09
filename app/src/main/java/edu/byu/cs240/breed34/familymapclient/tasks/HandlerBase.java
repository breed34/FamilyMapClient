package edu.byu.cs240.breed34.familymapclient.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HandlerBase extends Handler {
    private HandlerCallback callback;
    private HandlerCallback error;

    public HandlerBase(HandlerCallback callback, HandlerCallback error) {
        this.callback = callback;
        this.error = error;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean isSuccess = bundle.getBoolean(TaskBase.IS_SUCCESS_KEY, false);

        if (isSuccess) {
            callback.run(bundle);
        }
        else {
            error.run(bundle);
        }
    }
}
