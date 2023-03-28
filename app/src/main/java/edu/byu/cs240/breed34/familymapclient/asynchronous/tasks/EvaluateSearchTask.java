package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Handler;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
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
        // Initialize search filtered events and persons.
        Collection<Person> personsCollection = DataCache.getInstance().getPersons().values();
        Collection<Event> eventsCollection = DataCache.getInstance().getFilteredEvents().values();

        List<Person> searchFilteredPersons = new ArrayList<>(personsCollection);
        List<Event> searchFilteredEvents = new ArrayList<>(eventsCollection);

        // Remove persons who do not match search.
        for (Person person : personsCollection) {
            if (doesNotContainSearch(person)) {
                searchFilteredPersons.remove(person);
            }
        }

        // Remove events that do not match search.
        for (Event event : eventsCollection) {
            if (doesNotContainSearch(event)) {
                searchFilteredEvents.remove(event);
            }
        }

        // Convert to JSON and add to bundle.
        String eventResultsJson = new Gson().toJson(searchFilteredEvents);
        String personResultsJson = new Gson().toJson(searchFilteredPersons);

        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, true);
        results.put(EVENTS_RESULTS_KEY, eventResultsJson);
        results.put(PERSONS_RESULTS_KEY, personResultsJson);
        sendMessage(results);
    }

    private boolean doesNotContainSearch(Person person) {
        // Checks if a person matches the search string.
        String firstName = person.getFirstName().toLowerCase();
        String lastName = person.getLastName().toLowerCase();

        return  !firstName.contains(searchString) &&
                !lastName.contains(searchString);
    }

    private boolean doesNotContainSearch(Event event) {
        // Checks if an event matches the search string.
        String country = event.getCountry().toLowerCase();
        String city = event.getCity().toLowerCase();
        String eventType = event.getEventType().toLowerCase();
        String year = Integer.toString(event.getYear());

        return  !country.contains(searchString) &&
                !city.contains(searchString) &&
                !eventType.contains(searchString) &&
                !year.contains(searchString);
    }
}
