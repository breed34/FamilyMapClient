package edu.byu.cs240.breed34.familymapclient.tasks;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs240.breed34.familymapclient.proxy.DataCache;
import edu.byu.cs240.breed34.familymapclient.proxy.ServerProxy;
import models.Authtoken;
import requests.LoginRequest;
import results.LoginResult;

public class SignInTask extends TaskBase {
    public static final String PERSON_ID_KEY = "PersonID";

    private final LoginRequest request;

    public SignInTask(Handler messageHandler, LoginRequest request) {
        super(messageHandler);
        this.request = request;
    }

    @Override
    public void run() {
        ServerProxy proxy = new ServerProxy();

        LoginResult result = proxy.login(request);
        if (result.isSuccess()) {
            DataCache.getInstance().setCurrentUserToken(new Authtoken(
                    result.getAuthtoken(), result.getUsername()));
        }

        Map<String, Object> results = new HashMap<>();
        results.put(IS_SUCCESS_KEY, result.isSuccess());
        results.put(PERSON_ID_KEY, result.getPersonID());

        sendMessage(results);
    }
}
