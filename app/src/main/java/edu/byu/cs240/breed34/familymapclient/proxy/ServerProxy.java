package edu.byu.cs240.breed34.familymapclient.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import requests.EventsRequest;
import requests.LoginRequest;
import requests.PersonsRequest;
import requests.RegisterRequest;
import results.EventsResult;
import results.LoginResult;
import results.PersonsResult;
import results.RegisterResult;

public class ServerProxy {
    /**
     * Registers a user.
     *
     * @param request the register request object.
     * @return the result of the attempt to register the user.
     */
    public RegisterResult register(RegisterRequest request) {
        try {
            URL url = getUrlFromEndpoint("/user/register");
            return handleHttpRequest("POST",
                    url,
                    false,
                    request,
                    RegisterResult.class);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new RegisterResult("Error: An error occurred while trying" +
                    "to register the user.");
        }
    }

    /**
     * Logs in a user.
     *
     * @param request the log in request object.
     * @return the result of the attempt to log in the user.
     */
    public LoginResult login(LoginRequest request) {
        try {
            URL url = getUrlFromEndpoint("/user/login");
            return handleHttpRequest("POST",
                    url,
                    false,
                    request,
                    LoginResult.class);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new LoginResult("Error: An error occurred while trying" +
                    "to login the user.");
        }
    }

    /**
     * Gets all persons for the current user.
     *
     * @param request the persons request object.
     * @return the result of the attempt to get all persons for the current user.
     */
    public PersonsResult getAllPersons(PersonsRequest request) {
        try {
            URL url = getUrlFromEndpoint("/person");
            return handleHttpRequest("GET",
                    url,
                    true,
                    request,
                    PersonsResult.class);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new PersonsResult("Error: An error occurred while trying" +
                    "to get all persons for the user.");
        }
    }

    /**
     * Gets all events for the current user.
     *
     * @param request the events request object.
     * @return the result of the attempt to get all events for the current user.
     */
    public EventsResult getAllEvents(EventsRequest request) {
        try {
            URL url = getUrlFromEndpoint("/event");
            return handleHttpRequest("GET",
                    url,
                    true,
                    request,
                    EventsResult.class);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new EventsResult("Error: An error occurred while trying" +
                    "to get all events for the user.");
        }
    }

    /**
     * Creates a full URL from the server endpoint.
     *
     * @param endpoint the server endpoint.
     * @return The full URL.
     * @throws MalformedURLException if an error occurs while creating the URL.
     */
    private URL getUrlFromEndpoint(String endpoint) throws MalformedURLException {
        return new URL(String.format("http://%s:%s%s",
                DataCache.getInstance().getServerHost(),
                DataCache.getInstance().getServerPort(),
                endpoint));
    }

    /**
     * Handles the execution of all HTTP requests.
     *
     * @param method the HTTP method: "POST", "GET", etc.
     * @param url the URL of the endpoint to hit.
     * @param requiresAuth whether the request requires authentication.
     * @param request the request object.
     * @param resultClass the class of the result object.
     * @return the result of the request.
     * @param <TResult> the result type.
     * @param <TRequest> the request type.
     * @throws IOException if an error occurs while trying to execute the request.
     */
    private <TResult, TRequest> TResult handleHttpRequest(String method,
            URL url,
            boolean requiresAuth,
            TRequest request,
            Class<TResult> resultClass) throws IOException {

        // Basic configuration of http connection
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod(method);
        http.setDoOutput(method.toLowerCase().equals("post"));
        http.addRequestProperty("Accept", "application/json");

        // Add authtoken if necessary
        if (requiresAuth) {
            http.addRequestProperty("Authorization",
                    DataCache.getInstance().getCurrentUserToken().getAuthtoken());
        }

        http.connect();

        // Add request body if necessary
        if (method.toLowerCase().equals("post")) {
            String requestJson = new Gson().toJson(request);
            OutputStream requestBody = http.getOutputStream();
            writeString(requestJson, requestBody);
            requestBody.close();
        }

        // Get response body
        InputStream responseBody;
        if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("Successfully registered user");
            responseBody = http.getInputStream();
        }
        else {
            System.out.println("Error: " + http.getResponseMessage());
            responseBody = http.getErrorStream();
        }

        // Return deserialized response
        String responseJson = readString(responseBody);
        return new Gson().fromJson(responseJson, resultClass);
    }

    /**
     * Reads an input stream to a string.
     *
     * @param is the input stream.
     * @return the string.
     * @throws IOException if an error occurs while reading the stream.
     */
    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /**
     * Writes a string to an output stream.
     *
     * @param str the string.
     * @param os the output stream.
     * @throws IOException if an error occurs while writing to the stream.
     */
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
