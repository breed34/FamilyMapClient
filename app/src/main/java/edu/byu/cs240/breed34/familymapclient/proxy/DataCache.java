package edu.byu.cs240.breed34.familymapclient.proxy;

import java.util.Map;

import models.Event;
import models.Person;

public class DataCache {
    private static final DataCache instance = new DataCache();

    public static DataCache getInstance() {
        return instance;
    }

    private DataCache() {
    }

    private Map<String, Person> persons;
    private Map<String, Event> events;
}
