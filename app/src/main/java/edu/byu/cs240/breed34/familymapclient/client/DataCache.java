package edu.byu.cs240.breed34.familymapclient.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.byu.cs240.breed34.familymapclient.client.models.ConnectionType;
import edu.byu.cs240.breed34.familymapclient.client.models.EventConnection;
import edu.byu.cs240.breed34.familymapclient.client.models.Settings;
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
        settings = new Settings();
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
     * The personID of the current user.
     */
    private String currentUserPersonID;

    /**
     * The settings for what should data should
     * be displayed.
     */
    private Settings settings;

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

    /**
     * The events after settings have been applied.
     *
     * NOTE: The key in the map is the eventID
     * of the event to which it is mapped.
     */
    private Map<String, Event> filteredEvents;

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

    public void setCurrentUserPersonID(String currentUserPersonID) {
        this.currentUserPersonID = currentUserPersonID;
    }

    public Settings getSettings() {
        return settings;
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

    public Map<String, Event> getFilteredEvents() {
        return filteredEvents;
    }

    /**
     * Gets list of events after filters in settings have been applied.
     */
    public void setFilteredEvents() {
        this.filteredEvents = new HashMap<>(events);
        Person userPerson = persons.get(currentUserPersonID);

        // Handle filter father side events.
        if (!settings.showFatherSide() && userPerson.getFatherID() != null) {
            Set<String> fatherPersonIDs = new HashSet<>();
            calculateParentPersonIDs(userPerson.getFatherID(), fatherPersonIDs);
            filterByPersonIDs(fatherPersonIDs);
        }

        // Handle filter mother side events.
        if (!settings.showMotherSide() && userPerson.getMotherID() != null) {
            Set<String> motherPersonIDs = new HashSet<>();
            calculateParentPersonIDs(userPerson.getMotherID(), motherPersonIDs);
            filterByPersonIDs(motherPersonIDs);
        }

        // Handle filter male events.
        if (!settings.showMaleEvents()) {
            Set<String> malePersonIDs = new HashSet<>();
            calculateGenderPersonIDs("m", malePersonIDs);
            filterByPersonIDs(malePersonIDs);
        }

        // Handle filter female events.
        if (!settings.showFemaleEvents()) {
            Set<String> femalePersonIDs = new HashSet<>();
            calculateGenderPersonIDs("f", femalePersonIDs);
            filterByPersonIDs(femalePersonIDs);
        }
    }

    private void calculateParentPersonIDs(String personID, Set<String> parentPersonIDs) {
        // Get father and mother ids and add to set.
        String fatherID = persons.get(personID).getFatherID();
        String motherID = persons.get(personID).getMotherID();
        parentPersonIDs.add(personID);

        // Check if reached end of tree.
        if (fatherID == null && motherID == null) {
            return;
        }

        // Recursively loop through parents.
        calculateParentPersonIDs(fatherID, parentPersonIDs);
        calculateParentPersonIDs(motherID, parentPersonIDs);
    }

    private void calculateGenderPersonIDs(String gender, Set<String> genderPersonIDs) {
        // Find all person ideas for a given gender.
        for (Person person : persons.values()) {
            if (person.getGender().equals(gender)) {
                genderPersonIDs.add(person.getPersonID());
            }
        }
    }

    private void filterByPersonIDs(Set<String> personIDs) {
        // Remove all events associated with each personID in the set.
        Collection<Event> filteredEventsValues = new ArrayList<>(filteredEvents.values());
        for (Event event : filteredEventsValues) {
            if (personIDs.contains(event.getPersonID())) {
                filteredEvents.remove(event.getEventID());
            }
        }
    }

    /**
     * Gets connections to a given event to be used in creating lines on the map.
     *
     * @param eventID the eventID of the given event.
     * @return the list of connections.
     */
    public List<EventConnection> getConnections(String eventID) {
        // Get event and person, and set up connections list.
        Event filteredEvent = filteredEvents.get(eventID);
        Person eventPerson = persons.get(filteredEvent.getPersonID());
        List<EventConnection> connections = new ArrayList<>();

        // Maps personID to sorted events.
        Map<String, SortedSet<Event>> sortedPersonEvents = new HashMap<>();
        for (Event event : filteredEvents.values()) {
            if (!sortedPersonEvents.containsKey(event.getPersonID())) {
                sortedPersonEvents.put(event.getPersonID(), new TreeSet<>((event1, event2) -> {
                    return event1.getYear() - event2.getYear();
                }));
            }

            sortedPersonEvents.get(event.getPersonID()).add(event);
        }

        // Try to add each list of connections.
        tryAddSpouseConnection(sortedPersonEvents, connections, eventPerson, filteredEvent);
        tryAddLifeStoryConnections(sortedPersonEvents, connections, eventPerson);
        tryAddFamilyTreeConnections(sortedPersonEvents, connections, eventPerson, filteredEvent);

        return connections;
    }

    private void tryAddSpouseConnection(Map<String, SortedSet<Event>> sortedPersonEvents,
            List<EventConnection> connections,
            Person eventPerson,
            Event event) {

        // Add spouse event connection if settings indicate to do so.
        if (settings.showSpouseLines() && eventPerson.getSpouseID() != null) {
            connections.add(new EventConnection(event,
                    sortedPersonEvents.get(eventPerson.getSpouseID()).first(),
                    ConnectionType.SPOUSE));
        }
    }

    private void tryAddLifeStoryConnections(Map<String, SortedSet<Event>> sortedPersonEvents,
            List<EventConnection> connections,
            Person eventPerson) {

        // Add life story event connections if settings indicate to do so.
        if (settings.showLifeStoryLines()) {
            Iterator<Event> it = sortedPersonEvents.get(eventPerson.getPersonID()).iterator();

            // Keep track of each previous and next event.
            Event firstEvent = null;
            while (it.hasNext()) {
                if (firstEvent == null) {
                    firstEvent = it.next();
                }

                if (it.hasNext()) {
                    Event secondEvent = it.next();
                    connections.add(new EventConnection(firstEvent,
                            secondEvent,
                            ConnectionType.LIFE_STORY));

                    firstEvent = secondEvent;
                }
            }
        }
    }

    private void tryAddFamilyTreeConnections(Map<String, SortedSet<Event>> sortedPersonEvents,
            List<EventConnection> connections,
            Person eventPerson,
            Event event) {

        // Recursively add family tree event connections if settings indicate to do so.
        if (settings.showFamilyTreeLines()) {
            familyTreeConnHelper(sortedPersonEvents,
                    connections,
                    eventPerson,
                    event,
                    1);
        }
    }

    private void familyTreeConnHelper(Map<String, SortedSet<Event>> sortedPersonEvents,
            List<EventConnection> connections,
            Person eventPerson,
            Event event,
            int generation) {

        // Get father and mother ids.
        String fatherID = eventPerson.getFatherID();
        String motherID = eventPerson.getMotherID();

        // Check if reached end of tree.
        if (fatherID == null && motherID == null) {
            return;
        }

        // Get first events and add connections.
        Event fatherFirstEvent = sortedPersonEvents.get(fatherID).first();
        Event motherFirstEvent = sortedPersonEvents.get(motherID).first();

        if (fatherFirstEvent != null) {
            connections.add(new EventConnection(event,
                    fatherFirstEvent,
                    ConnectionType.FAMILY_TREE,
                    generation));
        }

        if (motherFirstEvent != null) {
            connections.add(new EventConnection(event,
                    motherFirstEvent,
                    ConnectionType.FAMILY_TREE,
                    generation));
        }

        // Recursively loop through parents.
        familyTreeConnHelper(sortedPersonEvents,
                connections,
                persons.get(fatherID),
                fatherFirstEvent,
                generation + 1);

        familyTreeConnHelper(sortedPersonEvents,
                connections,
                persons.get(motherID),
                motherFirstEvent,
                generation + 1);
    }
}
