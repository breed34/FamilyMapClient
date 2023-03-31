package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Handler;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
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
        // Get sorted life events and family members.
        List<Event> sortedLifeEvents = DataCache.getInstance()
                .getPersonLifeEvents(selectedPerson);
        List<FamilyMember> sortedFamilyMembers = DataCache.getInstance()
                .getPersonFamilyMembers(selectedPerson);

        // Convert to JSON and add to bundle.
        String lifeEventsJson = new Gson().toJson(sortedLifeEvents);
        String familyMembersJson = new Gson().toJson(sortedFamilyMembers);

        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, true);
        results.put(LIFE_EVENTS_KEY, lifeEventsJson);
        results.put(FAMILY_MEMBERS_KEY, familyMembersJson);
        sendMessage(results);
    }
}
