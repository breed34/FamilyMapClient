package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.ServerProxy;
import models.Authtoken;
import requests.LoginRequest;
import results.LoginResult;

/**
 * The asynchronous task for recalculating filtered events.
 */
public class EvaluateNewSettingsTask extends TaskBase {

    public EvaluateNewSettingsTask(Handler messageHandler) {
        super(messageHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // Re-filter events.
        DataCache.getInstance().setFilteredEvents();

        // Put data in bundle and send message.
        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, true);
        sendMessage(results);
    }
}
