package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Handler;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import models.Event;
import models.Person;

/**
 * The asynchronous task for applying a search
 * in the search activity.
 */
public class EvaluateSearchTask extends TaskBase {
    public static String EVENTS_RESULTS_KEY = "EventsResults";
    public static String PERSONS_RESULTS_KEY = "PersonsResults";

    /**
     * The search string.
     */
    private String searchString;

    public EvaluateSearchTask(Handler messageHandler, String searchString) {
        super(messageHandler);
        this.searchString = searchString;
    }

    @Override
    public void run() {
        // Search filtered events and persons.
        List<Person> searchFilteredPersons = DataCache.getInstance().searchPersons(searchString);
        List<Event> searchFilteredEvents = DataCache.getInstance().searchEvents(searchString);

        // Convert to JSON.
        String eventResultsJson = new Gson().toJson(searchFilteredEvents);
        String personResultsJson = new Gson().toJson(searchFilteredPersons);

        // Put data in bundle and send message.
        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, true);
        results.put(EVENTS_RESULTS_KEY, eventResultsJson);
        results.put(PERSONS_RESULTS_KEY, personResultsJson);
        sendMessage(results);
    }
}
