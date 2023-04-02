package edu.byu.cs240.breed34.familymapclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.byu.cs240.breed34.familymapclient.R;
import models.Event;
import models.Person;

/**
 * The recycler view adapter for the search activity.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    public static final int PERSON_ITEM_VIEW_TYPE = 0;
    public static final int EVENT_ITEM_VIEW_TYPE = 1;

    /**
     * The persons to be displayed.
     */
    private List<Person> persons;

    /**
     * The events to be displayed.
     */
    private List<Event> events;

    public SearchAdapter(List<Person> persons, List<Event> events) {
        this.persons = persons;
        this.events = events;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemViewType(int position) {
        // Calculate item type.
        return position < persons.size() ? PERSON_ITEM_VIEW_TYPE :
                EVENT_ITEM_VIEW_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate appropriate item.
        if (viewType == PERSON_ITEM_VIEW_TYPE) {
            view = inflater.inflate(R.layout.item_person_search, parent, false);
        }
        else {
            view = inflater.inflate(R.layout.item_event, parent, false);
        }

        return new SearchViewHolder(view, viewType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        // Bind to appropriate item.
        if (position < persons.size()) {
            holder.bind(persons.get(position));
        }
        else {
            holder.bind(events.get(position - persons.size()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        // Calculate item count.
        return persons.size() + events.size();
    }
}
