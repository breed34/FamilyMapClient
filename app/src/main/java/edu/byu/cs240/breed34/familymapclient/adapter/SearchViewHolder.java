package edu.byu.cs240.breed34.familymapclient.adapter;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.activities.EventActivity;
import edu.byu.cs240.breed34.familymapclient.activities.PersonActivity;
import edu.byu.cs240.breed34.familymapclient.activities.SearchActivity;
import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import models.Event;
import models.Person;

/**
 * The view holder for searches.
 */
public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    /**
     * The icon of the item.
     */
    private ImageView icon;

    /**
     * The name of the person associated with the item.
     */
    private TextView name;

    /**
     * The event details of an event item.
     *
     * NOTE: This should only be used for event items.
     */
    private TextView eventDetails;

    /**
     * The item view type;
     */
    private int viewType;

    /**
     * The person associated with the item.
     */
    private Person person;

    /**
     * The event associated with the item.
     *
     * NOTE: This should only be used for event items.
     */
    private Event event;

    public SearchViewHolder(View view, int viewType) {
        super(view);
        this.viewType = viewType;
        itemView.setOnClickListener(this);

        // Set additional items according to view type.
        if (viewType == SearchAdapter.PERSON_ITEM_VIEW_TYPE) {
            icon = itemView.findViewById(R.id.personItemIcon);
            name = itemView.findViewById(R.id.personItemName);
            eventDetails = null;
        } else {
            icon = itemView.findViewById(R.id.eventItemIcon);
            name = itemView.findViewById(R.id.eventItemName);
            eventDetails = itemView.findViewById(R.id.eventItemDetails);
        }
    }

    /**
     * Binds to a person view.
     *
     * @param person the person associated with the view.
     */
    public void bind(Person person) {
        this.person = person;
        itemView.setTag(person.getPersonID());

        // Set icon and name.
        String gender = person.getGender();
        int drawableId = gender.equals("m") ? R.drawable.icon_man : R.drawable.icon_woman;
        icon.setImageDrawable(getDrawable(itemView.getResources(),
                drawableId,
                null));

        name.setText(String.format("%s %s", person.getFirstName(), person.getLastName()));
    }

    /**
     * Binds to an event view.
     *
     * @param event the event associated with the view.
     */
    @SuppressLint("DefaultLocale")
    public void bind(Event event) {
        this.event = event;
        this.person = DataCache.getInstance().getPersons().get(event.getPersonID());
        itemView.setTag(event.getEventID());

        // Set name and event details.
        name.setText(String.format("%s %s", person.getFirstName(), person.getLastName()));
        eventDetails.setText(String.format("%S: %s, %s (%d)",
                event.getEventType(),
                event.getCity(),
                event.getCountry(),
                event.getYear()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view) {
        if (viewType == SearchAdapter.PERSON_ITEM_VIEW_TYPE) {
            // Start person activity.
            Intent intent = new Intent(view.getContext(), PersonActivity.class);
            intent.putExtra(PersonActivity.PERSON_ID_KEY, view.getTag().toString());
            view.getContext().startActivity(intent);
        }
        else {
            // Start event activity.
            Intent intent = new Intent(view.getContext(), EventActivity.class);
            intent.putExtra(EventActivity.EVENT_ID_KEY, view.getTag().toString());
            view.getContext().startActivity(intent);
        }
    }
}
