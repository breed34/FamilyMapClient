package edu.byu.cs240.breed34.familymapclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.asynchronous.HandlerBase;
import edu.byu.cs240.breed34.familymapclient.asynchronous.tasks.EvaluateNewSettingsTask;
import edu.byu.cs240.breed34.familymapclient.client.DataCache;

/**
 * Activity for controlling settings for the application.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup life story lines switch.
        Switch lifeStoryLinesSwitch = findViewById(R.id.lifeStoryLinesSwitch);
        lifeStoryLinesSwitch.setChecked(
                DataCache.getInstance().getSettings().showLifeStoryLines());
        lifeStoryLinesSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            DataCache.getInstance().getSettings().setShowLifeStoryLines(isChecked);
            evaluateNewSettings();
        });

        // Setup family tree lines switch.
        Switch familyTreeLinesSwitch = findViewById(R.id.familyTreeLinesSwitch);
        familyTreeLinesSwitch.setChecked(
                DataCache.getInstance().getSettings().showFamilyTreeLines());
        familyTreeLinesSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            DataCache.getInstance().getSettings().setShowFamilyTreeLines(isChecked);
            evaluateNewSettings();
        });

        // Setup spouse lines switch.
        Switch spouseLinesSwitch = findViewById(R.id.spouseLinesSwitch);
        spouseLinesSwitch.setChecked(
                DataCache.getInstance().getSettings().showSpouseLines());
        spouseLinesSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            DataCache.getInstance().getSettings().setShowSpouseLines(isChecked);
            evaluateNewSettings();
        });

        // Setup father's side switch.
        Switch fathersSideSwitch = findViewById(R.id.fathersSideSwitch);
        fathersSideSwitch.setChecked(
                DataCache.getInstance().getSettings().showFatherSide());
        fathersSideSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            DataCache.getInstance().getSettings().setShowFatherSide(isChecked);
            evaluateNewSettings();
        });

        // Setup mother's switch.
        Switch mothersSideSwitch = findViewById(R.id.mothersSideSwitch);
        mothersSideSwitch.setChecked(
                DataCache.getInstance().getSettings().showMotherSide());
        mothersSideSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            DataCache.getInstance().getSettings().setShowMotherSide(isChecked);
            evaluateNewSettings();
        });

        // Setup male events switch.
        Switch maleEventsSwitch = findViewById(R.id.maleEventsSwitch);
        maleEventsSwitch.setChecked(
                DataCache.getInstance().getSettings().showMaleEvents());
        maleEventsSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            DataCache.getInstance().getSettings().setShowMaleEvents(isChecked);
            evaluateNewSettings();
        });

        // Setup female events switch.
        Switch femaleEventsSwitch = findViewById(R.id.femaleEventsSwitch);
        femaleEventsSwitch.setChecked(
                DataCache.getInstance().getSettings().showFemaleEvents());
        femaleEventsSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            DataCache.getInstance().getSettings().setShowFemaleEvents(isChecked);
            evaluateNewSettings();
        });

        // Setup logout item.
        LinearLayout logoutItem = findViewById(R.id.logoutItem);
        logoutItem.setOnClickListener(view -> {
            // Logout the user.
            DataCache.getInstance().setCurrentUserToken(null);
            DataCache.getInstance().setCurrentUserPersonID(null);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
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

    /**
     * Handles all changes to settings.
     */
    private void evaluateNewSettings() {
        Handler newSettingsHandler = new HandlerBase(
            // Do nothing if successful.
            (bundle) -> {},
            // Callback to execute if error.
            (bundle) -> {
                Toast.makeText(SettingsActivity.this,
                        R.string.settingsError,
                        Toast.LENGTH_SHORT).show();
            });

        // Setup and start evaluate new settings task.
        EvaluateNewSettingsTask newSettingsTask = new EvaluateNewSettingsTask(
                newSettingsHandler);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(newSettingsTask);
    }
}