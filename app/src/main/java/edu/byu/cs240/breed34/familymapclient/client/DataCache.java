package edu.byu.cs240.breed34.familymapclient.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Authtoken;
import models.Event;
import models.Person;

/**
 * A singleton class containing the data necessary
 * for use in the Family Map Client.
 */
public class DataCache {
    /**
     * The shared instance of the data cache.
     */
    private static final DataCache instance = new DataCache();

    public static DataCache getInstance() {
        return instance;
    }

    private DataCache() {
    }

    /**
     * The host of the server to call out to.
     */
    private String serverHost;

    /**
     * The port of the server to call out to.
     */
    private String serverPort;

    /**
     * The authtoken of the current user.
     */
    private Authtoken currentUserToken;

    /**
     * The persons related to the current user.
     *
     * NOTE: The key in the map is the personID
     * of the person to which it is mapped.
     */
    private Map<String, Person> persons;

    /**
     * The events related to the current user.
     *
     * NOTE: The key in the map is the eventID
     * of the event to which it is mapped.
     */
    private Map<String, Event> events;

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public Authtoken getCurrentUserToken() {
        return currentUserToken;
    }

    public void setCurrentUserToken(Authtoken currentUserToken) {
        this.currentUserToken = currentUserToken;
    }

    public Map<String, Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> personsList) {
        // Converts list of persons to map representation.
        this.persons = new HashMap<>();
        for (Person person : personsList) {
            this.persons.put(person.getPersonID(), person);
        }
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> eventsList) {
        // Converts list of events to map representation.
        this.events = new HashMap<>();
        for (Event event : eventsList) {
            this.events.put(event.getEventID(), event);
        }
    }
}
