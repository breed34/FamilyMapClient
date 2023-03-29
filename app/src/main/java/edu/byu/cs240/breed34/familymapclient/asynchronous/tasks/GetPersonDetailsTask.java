package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Handler;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.models.FamilyMember;
import edu.byu.cs240.breed34.familymapclient.client.models.Relationship;
import models.Event;
import models.Person;

/**
 * The asynchronous task for getting a given person's details.
 */
public class GetPersonDetailsTask extends TaskBase {
    public static String LIFE_EVENTS_KEY = "LifeEvents";
    public static String FAMILY_MEMBERS_KEY = "FamilyMembers";

    /**
     * The given person.
     */
    private Person selectedPerson;

    public GetPersonDetailsTask(Handler messageHandler, Person selectedPerson) {
        super(messageHandler);
        this.selectedPerson = selectedPerson;
    }

    @Override
    public void run() {
        // Setup sorted sets for life events and family members.
        SortedSet<Event> sortedLifeEvents = new TreeSet<>((event1, event2) -> {
            return event1.getYear() - event2.getYear();
        });

        SortedSet<FamilyMember> sortedFamilyMembers = new TreeSet<>((member1, member2) -> {
            return member1.getRelationship().ordinal() - member2.getRelationship().ordinal();
        });

        // Add applicable events and persons to sets.
        for (Event event : DataCache.getInstance().getEvents().values()) {
            if (event.getPersonID().equals(selectedPerson.getPersonID())) {
                sortedLifeEvents.add(event);
            }
        }

        for (Person person : DataCache.getInstance().getPersons().values()) {
            tryAddFamilyMember(person, sortedFamilyMembers);
        }

        // Convert to JSON and add to bundle.
        String lifeEventsJson = new Gson().toJson(sortedLifeEvents);
        String familyMembersJson = new Gson().toJson(sortedFamilyMembers);

        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, true);
        results.put(LIFE_EVENTS_KEY, lifeEventsJson);
        results.put(FAMILY_MEMBERS_KEY, familyMembersJson);
        sendMessage(results);
    }

    private void tryAddFamilyMember(Person person, SortedSet<FamilyMember> familyMembers) {
        // Tries to add family members based on their relationships to the selected person.
        if (selectedPerson.getFatherID() != null &&
            person.getPersonID().equals(selectedPerson.getFatherID())) {

            familyMembers.add(new FamilyMember(person, Relationship.FATHER));
        }
        else if (selectedPerson.getMotherID() != null &&
                 person.getPersonID().equals(selectedPerson.getMotherID())) {

            familyMembers.add(new FamilyMember(person, Relationship.MOTHER));
        }
        else if (selectedPerson.getSpouseID() != null &&
                 person.getPersonID().equals(selectedPerson.getSpouseID())) {

            familyMembers.add(new FamilyMember(person, Relationship.SPOUSE));
        }
        else if (person.getFatherID() != null &&
                 person.getFatherID().equals(selectedPerson.getPersonID())) {

            familyMembers.add(new FamilyMember(person, Relationship.CHILD));
        }
        else if (person.getMotherID() != null &&
                person.getMotherID().equals(selectedPerson.getPersonID())) {

            familyMembers.add(new FamilyMember(person, Relationship.CHILD));
        }
    }
}
