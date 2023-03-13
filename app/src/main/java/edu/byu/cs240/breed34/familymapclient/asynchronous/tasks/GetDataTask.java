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
    /**
     * The key for the first name of the current user
     * stored in the bundle.
     */
    public static final String FIRST_NAME_KEY = "FirstName";

    /**
     * The key for the last name of the current user
     * stored in the bundle.
     */
    public static final String LAST_NAME_KEY = "LastName";

    /**
     * The personID of the current user.
     */
    private final String personID;

    public GetDataTask(Handler messageHandler, String personID) {
        super(messageHandler);
        this.personID = personID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        ServerProxy proxy = new ServerProxy();

        String firstName = "";
        String lastName = "";

        // Get persons related to the user and store it in the data cache.
        PersonsResult personsResult = proxy.getAllPersons();
        if (personsResult.isSuccess()) {
            DataCache.getInstance().setPersons(personsResult.getData());

            // Get the first and last name of the current user.
            Person userPerson = DataCache.getInstance().getPersons().get(personID);
            if (userPerson != null) {
                firstName = userPerson.getFirstName();
                lastName = userPerson.getLastName();
            }
        }

        // Get events related to the user and store it in the data cache.
        EventsResult eventsResult = proxy.getAllEvents();
        if (eventsResult.isSuccess()) {
            DataCache.getInstance().setEvents(eventsResult.getData());
        }

        // Put data in bundle and send message.
        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, personsResult.isSuccess() && eventsResult.isSuccess());
        results.put(FIRST_NAME_KEY, firstName);
        results.put(LAST_NAME_KEY, lastName);
        sendMessage(results);
    }
}
