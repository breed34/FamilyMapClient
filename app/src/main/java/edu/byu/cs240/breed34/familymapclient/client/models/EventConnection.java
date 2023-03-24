package edu.byu.cs240.breed34.familymapclient.client.models;

import models.Event;

/**
 * Defines a connection between two events.
 */
public class EventConnection {
    /**
     * The first event.
     */
    private Event firstEvent;

    /**
     * The second event.
     */
    private Event secondEvent;

    /**
     * The generation for the person associated with
     * the first event.
     *
     * NOTE: This should only be used to define connections
     * between events linking a family member to their ancestors.
     */
    private int generation;

    public EventConnection(Event firstEvent, Event secondEvent) {
        this.firstEvent = firstEvent;
        this.secondEvent = secondEvent;
        this.generation = -1;
    }

    public EventConnection(Event firstEvent, Event secondEvent, int generation) {
        this.firstEvent = firstEvent;
        this.secondEvent = secondEvent;
        this.generation = generation;
    }

    public Event getFirstEvent() {
        return firstEvent;
    }

    public Event getSecondEvent() {
        return secondEvent;
    }

    public int getGeneration() {
        return generation;
    }
}
