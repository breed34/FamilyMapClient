package edu.byu.cs240.breed34.familymapclient.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import edu.byu.cs240.breed34.familymapclient.tasks.GetDataTask;
import edu.byu.cs240.breed34.familymapclient.tasks.RegisterTask;
import edu.byu.cs240.breed34.familymapclient.tasks.SignInTask;
import edu.byu.cs240.breed34.familymapclient.tasks.TaskBase;
import requests.EventsRequest;
import requests.LoginRequest;
import requests.PersonsRequest;
import requests.RegisterRequest;
import results.EventsResult;
import results.LoginResult;
import results.PersonsResult;
import results.RegisterResult;

public class LoginFragment extends Fragment {
    private EditText serverHostField;
    private EditText serverPortField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private RadioGroup genderRadioGroup;
    private Button signInButton;
    private Button registerButton;

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            validateForm();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHostField = view.findViewById(R.id.serverHostField);
        serverHostField.addTextChangedListener(textWatcher);

        serverPortField = view.findViewById(R.id.serverPortField);
        serverPortField.addTextChangedListener(textWatcher);

        usernameField = view.findViewById(R.id.usernameField);
        usernameField.addTextChangedListener(textWatcher);

        passwordField = view.findViewById(R.id.passwordField);
        passwordField.addTextChangedListener(textWatcher);

        firstNameField = view.findViewById(R.id.firstNameField);
        firstNameField.addTextChangedListener(textWatcher);

        lastNameField = view.findViewById(R.id.lastNameField);
        lastNameField.addTextChangedListener(textWatcher);

        emailField = view.findViewById(R.id.emailField);
        emailField.addTextChangedListener(textWatcher);

        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        genderRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> validateForm());

        signInButton = view.findViewById(R.id.signInButton);
        signInButton.setOnClickListener((v) -> {
            setServerHostAndPort();
            signIn();
        });

        registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener((v) -> {
            setServerHostAndPort();
            register();
        });

        return view;
    }

    private void validateForm() {
        signInButton.setEnabled(!serverHostField.getText().toString().trim().isEmpty() &&
                !serverPortField.getText().toString().trim().isEmpty() &&
                !usernameField.getText().toString().trim().isEmpty() &&
                !passwordField.getText().toString().trim().isEmpty());

        registerButton.setEnabled(!serverHostField.getText().toString().trim().isEmpty() &&
                !serverPortField.getText().toString().trim().isEmpty() &&
                !usernameField.getText().toString().trim().isEmpty() &&
                !passwordField.getText().toString().trim().isEmpty() &&
                !firstNameField.getText().toString().trim().isEmpty() &&
                !lastNameField.getText().toString().trim().isEmpty() &&
                !emailField.getText().toString().trim().isEmpty() &&
                genderRadioGroup.getCheckedRadioButtonId() != -1);
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

                boolean isSuccess = bundle.getBoolean(SignInTask.IS_SUCCESS_KEY, false);
                String personID = bundle.getString(RegisterTask.PERSON_ID_KEY, "");

                if (isSuccess) {
                    getData(false, personID);
                }
                else {
                    Toast.makeText(getActivity(), R.string.signInUnsuccessfulToast,
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

                boolean isSuccess = bundle.getBoolean(RegisterTask.IS_SUCCESS_KEY, false);
                String personID = bundle.getString(RegisterTask.PERSON_ID_KEY, "");

                if (isSuccess) {
                    getData(true, personID);
                }
                else {
                    Toast.makeText(getActivity(), R.string.registerUnsuccessfulToast,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        RegisterRequest registerRequest = new RegisterRequest(
                getFieldValueById(R.id.usernameField),
                getFieldValueById(R.id.passwordField),
                getFieldValueById(R.id.emailField),
                getFieldValueById(R.id.firstNameField),
                getFieldValueById(R.id.lastNameField),
                getFieldValueById(R.id.genderRadioGroup));

        RegisterTask registerTask = new RegisterTask(registerHandler, registerRequest);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(registerTask);
    }

    private void getData(boolean isRegister, String personID) {
        @SuppressLint("HandlerLeak")
        Handler getDataHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();

                boolean isSuccess = bundle.getBoolean(GetDataTask.IS_SUCCESS_KEY, false);
                String firstName = bundle.getString(GetDataTask.FIRST_NAME_KEY, "");
                String lastName = bundle.getString(GetDataTask.LAST_NAME_KEY, "");

                if (isSuccess) {
                    Toast.makeText(getActivity(),
                            getString(R.string.userSignedInToast, firstName, lastName),
                            Toast.LENGTH_SHORT).show();
                }
                else if (isRegister) {
                    Toast.makeText(getActivity(), R.string.registerUnsuccessfulToast,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), R.string.signInUnsuccessfulToast,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        PersonsRequest personsRequest = new PersonsRequest(
                DataCache.getInstance().getCurrentUserToken().getUsername());

        EventsRequest eventsRequest = new EventsRequest(
                DataCache.getInstance().getCurrentUserToken().getUsername());

        GetDataTask getUserDataTask = new GetDataTask(getDataHandler,
                personID, personsRequest, eventsRequest);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(getUserDataTask);
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
}