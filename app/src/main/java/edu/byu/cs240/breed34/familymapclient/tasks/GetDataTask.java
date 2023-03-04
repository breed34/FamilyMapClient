package edu.byu.cs240.breed34.familymapclient.tasks;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs240.breed34.familymapclient.proxy.DataCache;
import edu.byu.cs240.breed34.familymapclient.proxy.ServerProxy;
import models.Person;
import requests.EventsRequest;
import requests.PersonsRequest;
import results.EventsResult;
import results.PersonsResult;

public class GetDataTask extends TaskBase {
    public static final String FIRST_NAME_KEY = "FirstName";
    public static final String LAST_NAME_KEY = "LastName";

    private final String personID;
    private final PersonsRequest personsRequest;
    private final EventsRequest eventsRequest;

    public GetDataTask(Handler messageHandler, String personID, PersonsRequest personsRequest,
                       EventsRequest eventsRequest) {
        super(messageHandler);
        this.personID = personID;
        this.personsRequest = personsRequest;
        this.eventsRequest = eventsRequest;
    }

    @Override
    public void run() {
        ServerProxy proxy = new ServerProxy();

        String firstName = "";
        String lastName = "";
        PersonsResult personsResult = proxy.getAllPersons(personsRequest);
        if (personsResult.isSuccess()) {
            DataCache.getInstance().setPersons(personsResult.getData());
            Person userPerson = DataCache.getInstance().getPersons().get(personID);

            assert userPerson != null;
            firstName = userPerson.getFirstName();
            lastName = userPerson.getLastName();
        }

        EventsResult eventsResult = proxy.getAllEvents(eventsRequest);
        if (eventsResult.isSuccess()) {
            DataCache.getInstance().setEvents(eventsResult.getData());
        }

        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, personsResult.isSuccess()
                && eventsResult.isSuccess());
        results.put(FIRST_NAME_KEY, firstName);
        results.put(LAST_NAME_KEY, lastName);

        sendMessage(results);
    }
}
