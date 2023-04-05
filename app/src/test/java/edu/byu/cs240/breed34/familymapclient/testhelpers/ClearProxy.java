package edu.byu.cs240.breed34.familymapclient.testhelpers;

import java.io.IOException;
import java.net.URL;

import edu.byu.cs240.breed34.familymapclient.client.ServerProxy;
import results.ClearResult;

/**
 * A server proxy for clearing the database.
 *
 * NOTE: This should be used exclusively by unit tests.
 */
public class ClearProxy extends ServerProxy {
    /**
     * Clears the database.
     *
     * @return the result of the attempt to clear the database.
     */
    public ClearResult clear() {
        try {
            URL url = getUrlFromEndpoint("/clear");
            return handleHttpRequest("POST",
                    url,
                    false,
                    null,
                    ClearResult.class);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ClearResult("Error: An error occurred while trying" +
                    "to clear the database.", false);
        }
    }
}
