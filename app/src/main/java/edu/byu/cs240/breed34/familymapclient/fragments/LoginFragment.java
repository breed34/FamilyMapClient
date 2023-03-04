package edu.byu.cs240.breed34.familymapclient.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.proxy.DataCache;
import edu.byu.cs240.breed34.familymapclient.proxy.ServerProxy;
import edu.byu.cs240.breed34.familymapclient.tasks.HttpTask;
import requests.EventsRequest;
import requests.LoginRequest;
import requests.PersonsRequest;
import requests.RegisterRequest;
import results.EventsResult;
import results.LoginResult;
import results.PersonsResult;
import results.RegisterResult;

public class LoginFragment extends Fragment {
    private static final String IS_SUCCESS_KEY = "IsSuccess";
    private static final String FIRST_NAME_KEY = "FirstName";
    private static final String LAST_NAME_KEY = "LastName";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button signInButton = view.findViewById(R.id.signInButton);
        signInButton.setOnClickListener((v) -> {
            setServerHostAndPort();
            signIn();
        });

        Button registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener((v) -> {
            setServerHostAndPort();
            register();
        });

        return view;
    }

    private void setServerHostAndPort() {
        DataCache.getInstance().setServerHost(getFieldValueById(R.id.serverHostField));
        DataCache.getInstance().setServerPort(getFieldValueById(R.id.serverPortField));
    }

    private void signIn() {
        @SuppressLint("HandlerLeak")
        Handler signInHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                boolean isSuccess = bundle.getBoolean(IS_SUCCESS_KEY, false);
                if (isSuccess) {
                    Toast.makeText(getActivity(), "Sign In successful",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Error: Sign In unsuccessful",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        LoginRequest loginRequest = new LoginRequest(
                getFieldValueById(R.id.usernameField),
                getFieldValueById(R.id.passwordField));

        SignInTask signInTask = new SignInTask(signInHandler, loginRequest);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(signInTask);
    }

    private void register() {
        @SuppressLint("HandlerLeak")
        Handler registerHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                boolean isSuccess = bundle.getBoolean(IS_SUCCESS_KEY, false);
                if (isSuccess) {
                    Toast.makeText(getActivity(), "Register successful",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Error: Register unsuccessful",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        RegisterRequest registerRequest = new RegisterRequest(
                getFieldValueById(R.id.usernameField),
                getFieldValueById(R.id.passwordField),
                getFieldValueById(R.id.firstNameField),
                getFieldValueById(R.id.lastNameField),
                getFieldValueById(R.id.emailField),
                getFieldValueById(R.id.genderRadioGroup));

        RegisterTask registerTask = new RegisterTask(registerHandler, registerRequest);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(registerTask);
    }

    private String getFieldValueById(int id) {
        if (id == R.id.genderRadioGroup) {
            switch (((RadioGroup)getView().findViewById(id)).getCheckedRadioButtonId()) {
                case R.id.maleRadioButton:
                    return "m";
                case R.id.femaleRadioButton:
                    return "f";
                default:
                    return "";
            }
        }

        return ((EditText)getView().findViewById(id)).getText().toString();
    }

    private static class SignInTask extends HttpTask {
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
                DataCache.getInstance().setCurrentUserToken(result.getAuthtoken());
            }

            sendBooleanMessage(IS_SUCCESS_KEY, result.isSuccess());
        }
    }

    private static class RegisterTask extends HttpTask {
        private final RegisterRequest request;

        public RegisterTask(Handler messageHandler, RegisterRequest request) {
            super(messageHandler);
            this.request = request;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy();

            RegisterResult result = proxy.register(request);
            if (result.isSuccess()) {
                DataCache.getInstance().setCurrentUserToken(result.getAuthtoken());
            }

            sendBooleanMessage(IS_SUCCESS_KEY, result.isSuccess());
        }
    }

    private static class GetDataTask extends HttpTask {
        private final PersonsRequest personsRequest;
        private final EventsRequest eventsRequest;

        public GetDataTask(Handler messageHandler, PersonsRequest personsRequest,
                           EventsRequest eventsRequest) {
            super(messageHandler);
            this.personsRequest = personsRequest;
            this.eventsRequest = eventsRequest;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy();

            PersonsResult personsResult = proxy.getAllPersons(personsRequest);
            if (personsResult.isSuccess()) {
                DataCache.getInstance().setPersons(personsResult.getData());
            }

            EventsResult eventsResult = proxy.getAllEvents(eventsRequest);
            if (eventsResult.isSuccess()) {
                DataCache.getInstance().setEvents(eventsResult.getData());
            }

            sendBooleanMessage(IS_SUCCESS_KEY, personsResult.isSuccess()
                    && eventsResult.isSuccess());
        }
    }
}