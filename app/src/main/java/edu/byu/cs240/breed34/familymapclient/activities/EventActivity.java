package edu.byu.cs240.breed34.familymapclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.fragments.MapFragment;

/**
 * The activity for displaying a selected event
 * on the Google map.
 */
public class EventActivity extends AppCompatActivity {
    public static String EVENT_ID_KEY = "EventId";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup bundle with eventID.
        String eventId = getIntent().getStringExtra(EVENT_ID_KEY);
        Bundle bundle = new Bundle();
        bundle.putString(EVENT_ID_KEY, eventId);

        // Setup fragment
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        if (fragment == null) {
            fragment = new MapFragment();
        }

        fragment.setArguments(bundle);

        // Commit fragment.
        fragmentManager.beginTransaction()
                .add(R.id.fragmentFrameLayout, fragment)
                .commit();
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
}