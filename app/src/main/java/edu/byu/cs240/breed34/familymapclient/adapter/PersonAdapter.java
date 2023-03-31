package edu.byu.cs240.breed34.familymapclient.adapter;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.activities.EventActivity;
import edu.byu.cs240.breed34.familymapclient.activities.PersonActivity;
import edu.byu.cs240.breed34.familymapclient.client.models.FamilyMember;
import models.Event;
import models.Person;

/**
 * The expandable list view adapter for the person activity.
 */
public class PersonAdapter extends BaseExpandableListAdapter {
    public static final int LIFE_EVENTS_GROUP_POSITION = 0;
    public static final int FAMILY_MEMBERS_GROUP_POSITION = 1;

    /**
     * The selected person.
     */
    private Person person;

    /**
     * The life events of the selected person.
     */
    private List<Event> lifeEvents;

    /**
     * The family members of the selected person.
     */
    private List<FamilyMember> familyMembers;

    public PersonAdapter(Person person,
            List<Event> lifeEvents,
            List<FamilyMember> familyMembers) {

        this.person = person;
        this.lifeEvents = lifeEvents;
        this.familyMembers = familyMembers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGroupCount() {
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        switch (groupPosition) {
            case LIFE_EVENTS_GROUP_POSITION:
                return lifeEvents.size();
            case FAMILY_MEMBERS_GROUP_POSITION:
                return familyMembers.size();
            default:
                throw new IllegalArgumentException("Unrecognized group position: " +
                        groupPosition);
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        switch (groupPosition) {
            case LIFE_EVENTS_GROUP_POSITION:
                return "LIFE EVENTS";
            case FAMILY_MEMBERS_GROUP_POSITION:
                return "FAMILY";
            default:
                throw new IllegalArgumentException("Unrecognized group position: " +
                        groupPosition);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case LIFE_EVENTS_GROUP_POSITION:
                return lifeEvents.get(childPosition);
            case FAMILY_MEMBERS_GROUP_POSITION:
                return familyMembers.get(childPosition);
            default:
                throw new IllegalArgumentException("Unrecognized group position: " +
                        groupPosition);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getGroupView(int groupPosition,
            boolean isExpanded,
            View convertView,
            ViewGroup parent) {

        // Check to initialize convertView.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_group_title, parent, false);
        }

        // Set up title of list.
        TextView titleView = convertView.findViewById(R.id.listViewTitle);
        switch (groupPosition) {
            case LIFE_EVENTS_GROUP_POSITION:
                titleView.setText(R.string.personLifeEvents);
                break;
            case FAMILY_MEMBERS_GROUP_POSITION:
                titleView.setText(R.string.personFamily);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized group position: " +
                        groupPosition);
        }

        return convertView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getChildView(int groupPosition,
            int childPosition,
            boolean isLastChild,
            View convertView,
            ViewGroup parent) {

        // Setup child views.
        View itemView;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (groupPosition) {
            case LIFE_EVENTS_GROUP_POSITION:
                itemView = inflater.inflate(R.layout.item_event, parent, false);
                initializeLifeEventView(itemView, childPosition);
                break;
            case FAMILY_MEMBERS_GROUP_POSITION:
                itemView = inflater.inflate(R.layout.item_person_details, parent, false);
                initializeFamilyMemberView(itemView, childPosition);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized group position: " +
                        groupPosition);
        }

        return itemView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @SuppressLint("DefaultLocale")
    private void initializeLifeEventView(View lifeEventView, int childPosition) {
        // Set text for event details and person name.
        Event event = lifeEvents.get(childPosition);
        TextView eventDetails = lifeEventView.findViewById(R.id.eventItemDetails);
        eventDetails.setText(String.format("%S: %s, %s (%d)",
                event.getEventType(),
                event.getCity(),
                event.getCountry(),
                event.getYear()));

        TextView eventPersonName = lifeEventView.findViewById(R.id.eventItemName);
        eventPersonName.setText(String.format("%s %s",
                person.getFirstName(),
                person.getLastName()));

        // Setup on click listener.
        lifeEventView.setTag(event.getEventID());
        lifeEventView.setOnClickListener(view -> {
            // Start event activity.
            Intent intent = new Intent(view.getContext(), EventActivity.class);
            intent.putExtra(EventActivity.EVENT_ID_KEY, view.getTag().toString());
            view.getContext().startActivity(intent);
        });
    }

    private void initializeFamilyMemberView(View familyMemberView, int childPosition) {
        // Set text for family member name.
        FamilyMember familyMember = familyMembers.get(childPosition);
        TextView personName = familyMemberView.findViewById(R.id.personDetailsName);
        personName.setText(String.format("%s %s",
                familyMember.getFirstName(),
                familyMember.getLastName()));

        // Set text for family member relationship.
        TextView relationship = familyMemberView.findViewById(R.id.personRelationship);
        relationship.setText(familyMember.getRelationship().toString());

        // Set family member icon.
        String gender = familyMember.getGender();
        int drawableId = gender.equals("m") ? R.drawable.icon_man : R.drawable.icon_woman;
        ImageView icon = familyMemberView.findViewById(R.id.personDetailsIcon);
        icon.setImageDrawable(getDrawable(familyMemberView.getResources(),
                drawableId,
                null));


        familyMemberView.setTag(familyMember.getPersonID());
        familyMemberView.setOnClickListener(view -> {
            // Start person activity.
            Intent intent = new Intent(view.getContext(), PersonActivity.class);
            intent.putExtra(PersonActivity.PERSON_ID_KEY, view.getTag().toString());
            view.getContext().startActivity(intent);
        });
    }
}
