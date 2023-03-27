package edu.byu.cs240.breed34.familymapclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.adapter.SearchAdapter;
import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import models.Event;
import models.Person;

public class SearchActivity extends AppCompatActivity {
    public static final int PERSON_ITEM_VIEW_TYPE = 0;
    public static final int EVENT_ITEM_VIEW_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        Collection<Person> personsCollection = DataCache.getInstance().getPersons().values();
        Collection<Event> eventsCollection = DataCache.getInstance().getFilteredEvents().values();

        List<Person> persons = new ArrayList<>(personsCollection);
        List<Event> events = new ArrayList<>(eventsCollection);

        SearchAdapter adapter = new SearchAdapter(persons, events);
        recyclerView.setAdapter(adapter);
    }
}