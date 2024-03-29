package edu.byu.cs240.breed34.familymapclient.client.models;

import androidx.annotation.Nullable;

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

    /**
     * The connection type.
     */
    private ConnectionType connectionType;

    public EventConnection(Event firstEvent,
            Event secondEvent,
            ConnectionType connectionType) {

        this.firstEvent = firstEvent;
        this.secondEvent = secondEvent;
        this.connectionType = connectionType;
        this.generation = -1;
    }

    public EventConnection(Event firstEvent,
            Event secondEvent,
            ConnectionType connectionType,
            int generation) {

        this.firstEvent = firstEvent;
        this.secondEvent = secondEvent;
        this.connectionType = connectionType;
        this.generation = generation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(this.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        EventConnection conn = (EventConnection) obj;
        return  firstEvent.equals(conn.firstEvent) &&
                secondEvent.equals(conn.secondEvent) &&
                connectionType.equals(conn.connectionType) &&
                generation == conn.generation;
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

    public ConnectionType getConnectionType() {
        return connectionType;
    }
}
