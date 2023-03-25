package edu.byu.cs240.breed34.familymapclient.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.asynchronous.tasks.GetDataTask;
import edu.byu.cs240.breed34.familymapclient.asynchronous.HandlerBase;
import edu.byu.cs240.breed34.familymapclient.asynchronous.tasks.RegisterTask;
import edu.byu.cs240.breed34.familymapclient.asynchronous.tasks.SignInTask;
import edu.byu.cs240.breed34.familymapclient.listeners.LoginListener;
import requests.EventsRequest;
import requests.LoginRequest;
import requests.PersonsRequest;
import requests.RegisterRequest;

/**
 * The fragment for logging a user in.
 */
public class LoginFragment extends Fragment {
    /**
     * The listener to notify the main activity
     * when login is complete.
     */
    private LoginListener listener;

    /**
     * A reference to the server host field.
     */
    private EditText serverHostField;

    /**
     * A reference to the server port field.
     */
    private EditText serverPortField;

    /**
     * A reference to the username field.
     */
    private EditText usernameField;

    /**
     * A reference to the password field.
     */
    private EditText passwordField;

    /**
     * A reference to the first name field.
     */
    private EditText firstNameField;

    /**
     * A reference to the last name field.
     */
    private EditText lastNameField;

    /**
     * A reference to the email field.
     */
    private EditText emailField;

    /**
     * A reference to the gender radio button group.
     */
    private RadioGroup genderRadioGroup;

    /**
     * A reference to the sign in button.
     */
    private Button signInButton;

    /**
     * A reference to the register button.
     */
    private Button registerButton;

    /**
     * A text watcher that can be shared by each field
     * to validate the current form.
     */
    private final TextWatcher textWatcher = new TextWatcher() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            validateForm();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
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

    /**
     * Registers a listener for the login fragment.
     *
     * @param listener the listener to register.
     */
    public void registerListener(LoginListener listener) {
        this.listener = listener;
    }

    /**
     * Validates the current form and decides whether
     * to enable the register and sign in buttons.
     */
    private void validateForm() {
        // Check fields for enabling sign in button.
        signInButton.setEnabled(
                !serverHostField.getText().toString().trim().isEmpty() &&
                !serverPortField.getText().toString().trim().isEmpty() &&
                !usernameField.getText().toString().trim().isEmpty() &&
                !passwordField.getText().toString().trim().isEmpty());

        // Check fields for enabling register button.
        registerButton.setEnabled(
                !serverHostField.getText().toString().trim().isEmpty() &&
                !serverPortField.getText().toString().trim().isEmpty() &&
                !usernameField.getText().toString().trim().isEmpty() &&
                !passwordField.getText().toString().trim().isEmpty() &&
                !firstNameField.getText().toString().trim().isEmpty() &&
                !lastNameField.getText().toString().trim().isEmpty() &&
                !emailField.getText().toString().trim().isEmpty() &&
                genderRadioGroup.getCheckedRadioButtonId() != -1);
    }

    /**
     * Sets the server host and server port in
     * the data cache.
     */
    private void setServerHostAndPort() {
        DataCache.getInstance().setServerHost(getFieldValueById(R.id.serverHostField));
        DataCache.getInstance().setServerPort(getFieldValueById(R.id.serverPortField));
    }

    /**
     * Starts a task for signing a user in.
     */
    private void signIn() {
        Handler signInHandler = new HandlerBase(
            // Callback to execute if success.
            (bundle) -> {
                String personID = bundle.getString(SignInTask.PERSON_ID_KEY, "");
                getData(false);
            },
            // Callback to execute if error.
            (bundle) -> {
                Toast.makeText(getActivity(),
                        R.string.signInUnsuccessfulToast,
                        Toast.LENGTH_SHORT).show();
            });

        LoginRequest loginRequest = new LoginRequest(
                getFieldValueById(R.id.usernameField),
                getFieldValueById(R.id.passwordField));

        // Execute sign in task.
        SignInTask signInTask = new SignInTask(signInHandler, loginRequest);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(signInTask);
    }

    /**
     * Starts a task for registering a user.
     */
    private void register() {
        Handler registerHandler = new HandlerBase(
            // Callback to execute if success.
            (bundle) -> {
                String personID = bundle.getString(RegisterTask.PERSON_ID_KEY, "");
                getData(true);
            },
            // Callback to execute if error.
            (bundle) -> {
                Toast.makeText(getActivity(),
                        R.string.registerUnsuccessfulToast,
                        Toast.LENGTH_SHORT).show();
            });

        RegisterRequest registerRequest = new RegisterRequest(
                getFieldValueById(R.id.usernameField),
                getFieldValueById(R.id.passwordField),
                getFieldValueById(R.id.emailField),
                getFieldValueById(R.id.firstNameField),
                getFieldValueById(R.id.lastNameField),
                getFieldValueById(R.id.genderRadioGroup));

        // Execute register task.
        RegisterTask registerTask = new RegisterTask(registerHandler, registerRequest);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(registerTask);
    }

    /**
     * Starts a task for getting the family history data
     * for the given user.
     *
     * @param isRegister whether the user is being registered.
     */
    private void getData(boolean isRegister) {
        Handler getDataHandler = new HandlerBase(
            // Callback to execute if success.
            (bundle) -> {
                listener.onLogin();
            },
            // Callback to execute if error.
            (bundle) -> {
                if (isRegister) {
                    Toast.makeText(getActivity(),
                            R.string.registerUnsuccessfulToast,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),
                            R.string.signInUnsuccessfulToast,
                            Toast.LENGTH_SHORT).show();
                }
            });

        // Execute get data task.
        GetDataTask getUserDataTask = new GetDataTask(getDataHandler);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(getUserDataTask);
    }

    /**
     * Gets the value of a given field on the form.
     *
     * @param id the id of the given field.
     * @return The string value contained in the given field.
     */
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