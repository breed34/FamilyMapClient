package edu.byu.cs240.breed34.familymapclient;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.ServerProxy;
import edu.byu.cs240.breed34.familymapclient.testhelpers.ClearProxy;
import models.Authtoken;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.EventsResult;
import results.LoginResult;
import results.PersonsResult;
import results.RegisterResult;

public class ServerProxyTest {
    private ServerProxy proxy;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @Before
    public void setUp() {
        DataCache.getInstance().setServerHost("localhost");
        DataCache.getInstance().setServerPort("8080");

        proxy = new ServerProxy();
        registerRequest = new RegisterRequest("jrando",
                "123",
                "jrando@gmail.com",
                "James",
                "Rando",
                "m");
        loginRequest = new LoginRequest("jrando",
                "123");
    }

    @After
    public void tearDown() {
        ClearProxy clearProxy = new ClearProxy();
        clearProxy.clear();
    }

    @Test
    public void registerPass() {
        RegisterResult result = proxy.register(registerRequest);

        assertTrue(result.isSuccess());
        assertEquals(registerRequest.getUsername(), result.getUsername());
    }

    @Test
    public void registerFail() {
        proxy.register(registerRequest);
        RegisterResult result = proxy.register(registerRequest);

        assertFalse(result.isSuccess());
        assertEquals("Error: A user with that username already exists.",
                result.getMessage());
    }

    @Test
    public void loginPass() {
        proxy.register(registerRequest);
        LoginResult result = proxy.login(loginRequest);

        assertTrue(result.isSuccess());
        assertEquals(loginRequest.getUsername(), result.getUsername());
    }

    @Test
    public void loginFail() {
        LoginResult result = proxy.login(loginRequest);

        assertFalse(result.isSuccess());
        assertEquals("Error: The username or password for the user was incorrect.",
                result.getMessage());
    }

    @Test
    public void getAllPersonsPass() {
        RegisterResult registerResult = proxy.register(registerRequest);
        DataCache.getInstance().setCurrentUserToken(new Authtoken(
                registerResult.getAuthtoken(), registerResult.getUsername()));

        PersonsResult result = proxy.getAllPersons();

        assertTrue(result.isSuccess());
        assertTrue(result.getData().size() > 0);
    }

    @Test
    public void getAllPersonsFail() {
        DataCache.getInstance().setCurrentUserToken(null);
        PersonsResult result = proxy.getAllPersons();

        assertFalse(result.isSuccess());
        assertEquals("Error: Request requires authentication.",
                result.getMessage());
    }

    @Test
    public void getAllEventsPass() {
        RegisterResult registerResult = proxy.register(registerRequest);
        DataCache.getInstance().setCurrentUserToken(new Authtoken(
                registerResult.getAuthtoken(), registerResult.getUsername()));

        EventsResult result = proxy.getAllEvents();

        assertTrue(result.isSuccess());
        assertTrue(result.getData().size() > 0);
    }

    @Test
    public void getAllEventsFail() {
        DataCache.getInstance().setCurrentUserToken(null);
        EventsResult result = proxy.getAllEvents();

        assertFalse(result.isSuccess());
        assertEquals("Error: Request requires authentication.",
                result.getMessage());
    }
}