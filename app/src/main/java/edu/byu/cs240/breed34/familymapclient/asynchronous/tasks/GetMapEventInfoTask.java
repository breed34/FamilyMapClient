package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Handler;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.models.EventConnection;

/**
 * The asynchronous task for getting event
 * lines for a given event.
 */
public class GetMapEventInfoTask extends TaskBase {
    public static final String CONNECTIONS_KEY = "ConnectionsKey";

    /**
     * The given eventID.
     */
    private String eventID;

    public GetMapEventInfoTask(Handler messageHandler, String eventID) {
        super(messageHandler);
        this.eventID = eventID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        List<EventConnection> eventConnections = new ArrayList<>();

        // Add spouse connection.
        EventConnection spouseConnection =
                DataCache.getInstance().getSpouseConnection(eventID);
        if (spouseConnection != null) {
            eventConnections.add(spouseConnection);
        }

        String connectionsJson = new Gson().toJson(eventConnections);

        // Put data in bundle and send message.
        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, true);
        results.put(CONNECTIONS_KEY, connectionsJson);
        sendMessage(results);
    }
}
