package edu.byu.cs240.breed34.familymapclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.adapter.SearchAdapter;
import edu.byu.cs240.breed34.familymapclient.asynchronous.HandlerBase;
import edu.byu.cs240.breed34.familymapclient.asynchronous.tasks.EvaluateSearchTask;
import models.Event;
import models.Person;

/**
 * Activity for handling searches.
 */
public class SearchActivity extends AppCompatActivity {
    /**
     * Filtered persons after search executed.
     */
    private List<Person> searchFilteredPersons;

    /**
     * Filtered events after search executed.
     */
    private List<Event> searchFilteredEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set default results.
        setDefaultSearchResults();
        setRecyclerResults();

        // Setup search view.
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onQueryTextChange(String s) {
                // Handle search.
                if (s.length() == 0) {
                    setDefaultSearchResults();
                    setRecyclerResults();
                } else {
                    search(s);
                }

                return true;
            }
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

    private void setRecyclerResults() {
        // Insert search results into RecyclerView.
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        SearchAdapter adapter = new SearchAdapter(searchFilteredPersons, searchFilteredEvents);
        recyclerView.setAdapter(adapter);
    }

    private void setDefaultSearchResults() {
        // Set search filtered persons and events to default values.
        searchFilteredPersons = new ArrayList<>();
        searchFilteredEvents = new ArrayList<>();
    }

    private void search(String searchString) {
        Handler evaluateSearchHandler = new HandlerBase(
            // Callback to execute if success.
            (bundle) -> {
                // Get search results from bundle.
                String eventsJson = bundle.getString(
                        EvaluateSearchTask.EVENTS_RESULTS_KEY);
                String personsJson = bundle.getString(
                        EvaluateSearchTask.PERSONS_RESULTS_KEY);

                // Deserialize data.
                Type eventsType = new TypeToken<ArrayList<Event>>(){}.getType();
                searchFilteredEvents = new Gson().fromJson(eventsJson, eventsType);
                Type personsType = new TypeToken<ArrayList<Person>>(){}.getType();
                searchFilteredPersons = new Gson().fromJson(personsJson, personsType);

                setRecyclerResults();
            },
            // Callback to execute if error.
            (bundle) -> {
                Toast.makeText(SearchActivity.this,
                        R.string.searchError,
                        Toast.LENGTH_SHORT).show();
            });

        // Setup and execute evaluate search task.
        EvaluateSearchTask searchTask = new EvaluateSearchTask(
                evaluateSearchHandler,
                searchString.toLowerCase());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(searchTask);
    }
}