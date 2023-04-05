package edu.byu.cs240.breed34.familymapclient;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.testhelpers.ClearProxy;
import results.ClearResult;

public class ClearProxyTest {
    @Test
    public void clearProxyPass() {
        DataCache.getInstance().setServerHost("localhost");
        DataCache.getInstance().setServerPort("8080");

        ClearResult result = new ClearProxy().clear();

        assertTrue(result.isSuccess());
        assertEquals("Clear succeeded.", result.getMessage());
    }
}
