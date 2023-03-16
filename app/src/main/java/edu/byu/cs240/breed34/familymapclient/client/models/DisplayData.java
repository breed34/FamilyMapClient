package edu.byu.cs240.breed34.familymapclient.client.models;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Event;
import models.Person;

/**
 * Contains all data and logic related to displaying data
 * on a Google map or in search results based on user
 * settings.
 */
public class DisplayData {
    /**
     * The events to display after filters are applied.
     */
    private Map<String, Event> filteredEvents;

    /**
     * The line to show connected the filtered events.
     */
    private List<PolylineOptions> eventLines;

    /**
     * The event markers for the filtered events.
     */
    private List<MarkerOptions> eventMarkers;

    /**
     * The colors to be used for each event type.
     */
    private Map<String, Float> eventColors;

    public DisplayData(String currentUserPersonID,
            Settings settings,
            Map<String, Event> allEvents,
            Map<String, Person> allPersons) {

        setDisplayData(currentUserPersonID, settings, allEvents, allPersons);
    }

    private void setDisplayData(String currentUserPersonID,
            Settings settings,
            Map<String, Event> allEvents,
            Map<String, Person> allPersons) {

        filteredEvents = new HashMap<>(allEvents);

        if (!settings.showFatherSide()) {
            String fatherID = allPersons.get(currentUserPersonID).getFatherID();
            filterParentSideIDs(fatherID, allPersons);
        }

        if (!settings.showMotherSide()) {
            String motherID = allPersons.get(currentUserPersonID).getMotherID();
            filterParentSideIDs(motherID, allPersons);
        }

        if (!settings.showMaleEvents()) {
            filterGender("m", allPersons);
        }

        if (!settings.showFemaleEvents()) {
            filterGender("f", allPersons);
        }

        // Create markers

        // Create lines
    }

    private void filterParentSideIDs(String parentID, Map<String, Person> allPersons) {
        Set<String> parentSidePersonIDs = new HashSet<>();
        calculateParentSidePersonIDs(parentID, allPersons, parentSidePersonIDs);
        for (String id : parentSidePersonIDs) {
            filteredEvents.remove(id);
        }
    }

    private void filterGender(String gender, Map<String, Person> allPersons) {
        for (Map.Entry<String, Event> event : filteredEvents.entrySet()) {
            // Remove if person associated with event has given gender.
            String personID = event.getValue().getPersonID();
            if (allPersons.get(personID).getGender().equals(gender)) {
                filteredEvents.remove(event.getKey());
            }
        }
    }


    private void calculateParentSidePersonIDs(String personID,
            Map<String, Person> allPersons,
            Set<String> parentSidePersonIDs) {

        // Get father and mother ids and add to set
        String fatherID = allPersons.get(personID).getFatherID();
        String motherID = allPersons.get(personID).getMotherID();
        parentSidePersonIDs.add(personID);

        // Check if reached end of tree
        if (fatherID == null && motherID == null) {
            return;
        }

        // Recursively loop through parents
        calculateParentSidePersonIDs(fatherID, allPersons, parentSidePersonIDs);
        calculateParentSidePersonIDs(motherID, allPersons, parentSidePersonIDs);
    }
}
