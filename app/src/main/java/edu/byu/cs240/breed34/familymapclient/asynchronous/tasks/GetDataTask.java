package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.ServerProxy;
import models.Person;
import requests.EventsRequest;
import requests.PersonsRequest;
import results.EventsResult;
import results.PersonsResult;

/**
 * The asynchronous task for getting family history
 * data for the assigned user.
 */
public class GetDataTask extends TaskBase {
    public GetDataTask(Handler messageHandler) {
        super(messageHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        ServerProxy proxy = new ServerProxy();

        // Get persons and events related to the user and store it in the data cache.
        PersonsResult personsResult = proxy.getAllPersons();
        EventsResult eventsResult = proxy.getAllEvents();
        if (personsResult.isSuccess() && eventsResult.isSuccess()) {
            DataCache.getInstance().setPersons(personsResult.getData());
            DataCache.getInstance().setEvents(eventsResult.getData());
            DataCache.getInstance().setFilteredEvents();
        }

        // Put data in bundle and send message.
        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, personsResult.isSuccess() && eventsResult.isSuccess());
        sendMessage(results);
    }
}
