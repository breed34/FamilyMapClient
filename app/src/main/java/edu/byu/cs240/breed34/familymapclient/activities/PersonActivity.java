package edu.byu.cs240.breed34.familymapclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.adapter.PersonAdapter;
import edu.byu.cs240.breed34.familymapclient.asynchronous.HandlerBase;
import edu.byu.cs240.breed34.familymapclient.asynchronous.tasks.GetPersonDetailsTask;
import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.models.FamilyMember;
import models.Event;
import models.Person;

/**
 * Activity for displaying person details.
 */
public class PersonActivity extends AppCompatActivity {
    public static final String PERSON_ID_KEY = "PersonID";

    /**
     * The selected person.
     */
    private Person selectedPerson;

    /**
     * The selected person's life events.
     */
    private List<Event> lifeEvents;

    /**
     * The selected person's family members.
     */
    private List<FamilyMember> familyMembers;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        // Get selected person.
        String selectedPersonID = getIntent().getStringExtra(PERSON_ID_KEY);
        selectedPerson = DataCache.getInstance().getPersons().get(selectedPersonID);

        // Set person information.
        TextView firstNameView = findViewById(R.id.personFirstName);
        firstNameView.setText(selectedPerson.getFirstName());

        TextView lastNameView = findViewById(R.id.personLastName);
        lastNameView.setText(selectedPerson.getLastName());

        TextView genderView = findViewById(R.id.personGender);
        int genderId = selectedPerson.getGender().equals("m") ? R.string.male : R.string.female;
        genderView.setText(getString(genderId));

        // Get and set additional person details.
        getPersonDetails();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Setup up button.
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return true;
    }

    private void getPersonDetails() {
        Handler getPersonDetailsHandler = new HandlerBase(
            // Callback to execute if success.
            (bundle) -> {
                // Get details from bundle.
                String lifeEventsJson = bundle.getString(
                        GetPersonDetailsTask.LIFE_EVENTS_KEY);
                String familyMembersJson = bundle.getString(
                        GetPersonDetailsTask.FAMILY_MEMBERS_KEY);

                // Deserialize data.
                Type eventsType = new TypeToken<ArrayList<Event>>(){}.getType();
                lifeEvents = new Gson().fromJson(lifeEventsJson, eventsType);

                Type membersType = new TypeToken<ArrayList<FamilyMember>>(){}.getType();
                familyMembers = new Gson().fromJson(familyMembersJson, membersType);

                // Set display data.
                ExpandableListView expandableListView = findViewById(R.id.expandableListView);
                expandableListView.setAdapter(new PersonAdapter(
                        selectedPerson,
                        lifeEvents,
                        familyMembers));
            },
            // Callback to execute if error.
            (bundle) -> {
                Toast.makeText(PersonActivity.this,
                        R.string.personDetailsError,
                        Toast.LENGTH_SHORT).show();
            });

        // Setup and execute get person details task.
        GetPersonDetailsTask detailsTask = new GetPersonDetailsTask(
                getPersonDetailsHandler,
                selectedPerson);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(detailsTask);
    }
}