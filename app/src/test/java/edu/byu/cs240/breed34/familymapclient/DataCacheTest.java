package edu.byu.cs240.breed34.familymapclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.models.ConnectionType;
import edu.byu.cs240.breed34.familymapclient.client.models.EventConnection;
import edu.byu.cs240.breed34.familymapclient.client.models.FamilyMember;
import edu.byu.cs240.breed34.familymapclient.client.models.Relationship;
import edu.byu.cs240.breed34.familymapclient.client.models.Settings;
import models.Event;
import models.Person;

public class DataCacheTest {
    // The personID for James Rando.
    private static final String JAMES_RANDO_ID =
            "04823178-c677-43cc-bbbf-5d47e10e4009";

    // The personID for Ardelia Mcroberts.
    private static final String ARDELIA_MCROBERTS_ID =
            "d0267391-6f49-449e-993d-97362e85dec5";

    // Birth eventID for James Rando.
    private static final String JAMES_RANDO_BIRTH_ID =
            "77c4ed93-dc3c-4e9a-9dae-af26bbd92fcb";

    // Birth eventID for Santiago Freitag.
    private static final String SANTIAGO_FREITAG_BIRTH_ID =
            "faf08a02-4d4f-44ae-96ca-52cb75d4d8bb";

    // Path to test root folder.
    private static final String PATH_TO_TEST =
            "src/test/java/edu/byu/cs240/breed34/familymapclient/";

    // The data cache.
    private DataCache cache;

    @Before
    public void setUp() throws FileNotFoundException {
        cache = DataCache.getInstance();
        cache.setSettings(new Settings());
        cache.setCurrentUserPersonID(JAMES_RANDO_ID);

        // Get test persons from file.
        String personsJson = getJson(PATH_TO_TEST + "testhelpers/data/TestPersons.json");
        Type personsType = new TypeToken<ArrayList<Person>>(){}.getType();
        cache.setPersons(new Gson().fromJson(personsJson, personsType));

        // Get test events from file.
        String eventsJson = getJson(PATH_TO_TEST + "testhelpers/data/TestEvents.json");
        Type eventsType = new TypeToken<ArrayList<Event>>(){}.getType();
        cache.setEvents(new Gson().fromJson(eventsJson, eventsType));

        // Set default filtered events.
        cache.setFilteredEvents();
    }

    @Test
    public void setFilteredEventsSomeEvents() {
        cache.getSettings().setShowFatherSide(false);
        cache.getSettings().setShowMotherSide(false);
        cache.setFilteredEvents();

        Map<String, Event> expected = getExpectedFilteredEvents();
        Map<String, Event> actual = cache.getFilteredEvents();

        assertEquals(expected.size(), actual.size());
        for (Map.Entry<String, Event> entry : expected.entrySet()) {
            assertEquals(actual.get(entry.getKey()), expected.get(entry.getKey()));
        }
    }

    @Test
    public void setFilteredEventsNoEvents() {
        cache.getSettings().setShowMaleEvents(false);
        cache.getSettings().setShowFemaleEvents(false);
        cache.setFilteredEvents();

        Map<String, Event> actual = cache.getFilteredEvents();
        assertEquals(0, actual.size());
    }

    @Test
    public void getConnectionsSomeConnections() {
        List<EventConnection> expected = getExpectedConnections();
        List<EventConnection> actual = cache.getConnections(SANTIAGO_FREITAG_BIRTH_ID);

        assertEquals(expected.size(), actual.size());
        for (EventConnection conn : expected) {
            assertTrue(actual.contains(conn));
        }
    }

    @Test
    public void getConnectionsNoConnections() {
        cache.getSettings().setShowFatherSide(false);
        cache.getSettings().setShowMotherSide(false);
        cache.setFilteredEvents();

        List<EventConnection> actual = cache.getConnections(JAMES_RANDO_BIRTH_ID);
        assertEquals(0, actual.size());
    }

    @Test
    public void searchPersonsSomePersons() {
        String search = "rO";
        List<Person> expected = getExpectedSearchPersons();
        List<Person> actual = cache.searchPersons(search);

        assertEquals(expected.size(), actual.size());
        for (Person person : expected) {
            assertTrue(actual.contains(person));
        }
    }

    @Test
    public void searchPersonsNoPersons() {
        String search = "aBc";
        List<Person> actual = cache.searchPersons(search);

        assertEquals(0, actual.size());
    }

    @Test
    public void searchEventsSomeEvents() {
        String search = "aRi";
        List<Event> expected = getExpectedSearchEvents();
        List<Event> actual = cache.searchEvents(search);

        assertEquals(expected.size(), actual.size());
        for (Event event : expected) {
            assertTrue(actual.contains(event));
        }
    }

    @Test
    public void searchEventsNoEvents() {
        String search = "abC";
        List<Event> actual = cache.searchEvents(search);

        assertEquals(0, actual.size());
    }

    @Test
    public void getPersonLifeEventsSomeEvents() {
        Person person = cache.getPersons().get(ARDELIA_MCROBERTS_ID);
        List<Event> expected = getExpectedPersonLifeEvents();
        List<Event> actual = cache.getPersonLifeEvents(person);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void getPersonLifeEventsNoEvents() {
        cache.getSettings().setShowFemaleEvents(false);
        cache.setFilteredEvents();

        Person person = cache.getPersons().get(ARDELIA_MCROBERTS_ID);
        List<Event> actual = cache.getPersonLifeEvents(person);

        assertEquals(0, actual.size());
    }

    @Test
    public void getPersonFamilyMembersChildAndSpouse() {
        Person person = cache.getPersons().get(ARDELIA_MCROBERTS_ID);
        List<FamilyMember> expected = getExpectedPersonFamilyMembers(ARDELIA_MCROBERTS_ID);
        List<FamilyMember> actual = cache.getPersonFamilyMembers(person);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void getPersonFamilyMembersNoChildOrSpouse() {
        Person person = cache.getPersons().get(JAMES_RANDO_ID);
        List<FamilyMember> expected = getExpectedPersonFamilyMembers(JAMES_RANDO_ID);
        List<FamilyMember> actual = cache.getPersonFamilyMembers(person);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    private String getJson(String path) throws FileNotFoundException {
        // Get JSON string from filepath.
        File file = new File(path);
        StringBuilder builder = new StringBuilder();
        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }
        }

        return builder.toString();
    }

    private Map<String, Event> getExpectedFilteredEvents() {
        Event personBirth = cache.getEvents().get(JAMES_RANDO_BIRTH_ID);

        // Create expected filtered events map.
        Map<String, Event> expected = new HashMap<>();
        expected.put(personBirth.getEventID(), personBirth);

        return expected;
    }

    private List<EventConnection> getExpectedConnections() {
        // Get life story events.
        Event personBirth = cache.getEvents().get(SANTIAGO_FREITAG_BIRTH_ID);
        Event personMarriage = cache.getEvents().get("0586763f-3148-4bfb-a7a4-6c0635c9337d");
        Event personDeath = cache.getEvents().get("38e01507-9999-40e8-9f9b-8c7841f372c9");

        // Get family member first events.
        Event spouseFirst = cache.getEvents().get("ac0ea2bf-24ee-4f8d-acf9-e88c3a302a44");
        Event fatherFirst = cache.getEvents().get("fa067f1a-52ee-43de-83e6-39ecadeef22d");
        Event motherFirst = cache.getEvents().get("1f7f4291-40b9-491a-8bd5-ebe535c599d6");

        // Create life story connections.
        EventConnection birthMarriageConn = new EventConnection(personBirth,
                personMarriage,
                ConnectionType.LIFE_STORY);
        EventConnection marriageDeathConn = new EventConnection(personMarriage,
                personDeath,
                ConnectionType.LIFE_STORY);

        // Create spouse connection.
        EventConnection spouseConn = new EventConnection(personBirth,
                spouseFirst,
                ConnectionType.SPOUSE);

        // Create parent connections.
        EventConnection fatherConn = new EventConnection(personBirth,
                fatherFirst,
                ConnectionType.FAMILY_TREE,
                1);
        EventConnection motherConn = new EventConnection(personBirth,
                motherFirst,
                ConnectionType.FAMILY_TREE,
                1);

        // Create expected connections list.
        List<EventConnection> expected = new ArrayList<>();
        expected.add(birthMarriageConn);
        expected.add(marriageDeathConn);
        expected.add(spouseConn);
        expected.add(fatherConn);
        expected.add(motherConn);

        return expected;
    }

    private List<Person> getExpectedSearchPersons() {
        List<Person> expected = new ArrayList<>();

        // Last name: Cosgrove.
        expected.add(cache.getPersons().get("abf5a81f-8550-45d0-a98c-20be6f1c7378"));

        // First name: Lazaro.
        expected.add(cache.getPersons().get("a9b3e42a-f1c8-43bb-baa5-b7c2d94f84f6"));

        // Last name: Mcroberts.
        expected.add(cache.getPersons().get("d0267391-6f49-449e-993d-97362e85dec5"));

        // Last name: Nigro.
        expected.add(cache.getPersons().get("da6e1da5-56dc-4cf7-98a0-71a6a2569e9c"));

        return expected;
    }

    private List<Event> getExpectedSearchEvents() {
        List<Event> expected = new ArrayList<>();

        // City: Rosario.
        expected.add(cache.getEvents().get("21757fa6-193b-4558-bc2c-985433675133"));

        // City: Rosario.
        expected.add(cache.getEvents().get("4f7c4a76-6c50-4a87-8ad3-55e8864da576"));

        // Country: Bulgaria.
        expected.add(cache.getEvents().get("e98e24ff-4a20-4f28-9e45-ce1601d0dd0f"));

        return expected;
    }

    private List<Event> getExpectedPersonLifeEvents() {
        List<Event> expected = new ArrayList<>();

        // Add life events for Ardelia Mcroberts.
        expected.add(cache.getEvents().get("db162180-b621-4765-81b5-759c41edda0c"));
        expected.add(cache.getEvents().get("c6092010-f02c-42ee-af1d-39589038c930"));
        expected.add(cache.getEvents().get("0bbd9360-93ea-4049-a611-ab4694a391ee"));

        return expected;
    }

    private List<FamilyMember> getExpectedPersonFamilyMembers(String personID) {
        List<FamilyMember> expected = new ArrayList<>();

        switch (personID) {
            case ARDELIA_MCROBERTS_ID:
                // Add family members for Ardelia Mcroberts.
                expected.add(new FamilyMember(
                        cache.getPersons().get("a9b3e42a-f1c8-43bb-baa5-b7c2d94f84f6"),
                        Relationship.FATHER));
                expected.add(new FamilyMember(
                        cache.getPersons().get("d3b3b12a-b30a-4f3f-9206-99d03d85e486"),
                        Relationship.MOTHER));
                expected.add(new FamilyMember(
                        cache.getPersons().get("9ae3edf1-4b9b-4cea-8515-a4231ae8249e"),
                        Relationship.SPOUSE));
                expected.add(new FamilyMember(
                        cache.getPersons().get("a03a9716-612c-47ac-aa3f-d4e5b759ad3d"),
                        Relationship.CHILD));
                break;
            case JAMES_RANDO_ID:
                // Add family members for James Rando.
                expected.add(new FamilyMember(
                        cache.getPersons().get("d41fcbd6-87be-4137-9b3e-7114daaf2509"),
                        Relationship.FATHER));
                expected.add(new FamilyMember(
                        cache.getPersons().get("da6e1da5-56dc-4cf7-98a0-71a6a2569e9c"),
                        Relationship.MOTHER));
                break;
            default:
                throw new RuntimeException();
        }

        return expected;
    }
}
