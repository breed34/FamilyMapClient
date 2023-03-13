package edu.byu.cs240.breed34.familymapclient.asynchronous.tasks;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.ServerProxy;
import models.Authtoken;
import requests.LoginRequest;
import results.LoginResult;

/**
 * The asynchronous task for signing in an existing user.
 */
public class SignInTask extends TaskBase {
    /**
     * The key for the personID of the newly registered
     * user stored in the bundle.
     */
    public static final String PERSON_ID_KEY = "PersonID";

    /**
     * The request object for signing in an existing user.
     */
    private final LoginRequest request;

    public SignInTask(Handler messageHandler, LoginRequest request) {
        super(messageHandler);
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        ServerProxy proxy = new ServerProxy();

        // Sign in the existing user.
        LoginResult result = proxy.login(request);
        if (result.isSuccess()) {
            DataCache.getInstance().setCurrentUserToken(new Authtoken(
                    result.getAuthtoken(), result.getUsername()));
        }

        // Put data in bundle and send message.
        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, result.isSuccess());
        results.put(PERSON_ID_KEY, result.getPersonID());
        sendMessage(results);
    }
}
