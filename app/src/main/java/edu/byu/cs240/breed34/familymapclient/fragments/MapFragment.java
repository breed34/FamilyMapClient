package edu.byu.cs240.breed34.familymapclient.fragments;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import models.Event;
import models.Person;

/**
 * The fragment for displaying family history
 * data on a Google map.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final float[] COLORS = new float[]{
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_AZURE,
    };

    private ImageView detailsIcon;
    private TextView detailsText;

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_map, container, false);

        // Set default details icon
        detailsIcon = view.findViewById(R.id.details_icon);
        detailsIcon.setImageDrawable(getDrawable(getResources(),
                R.drawable.icon_android,
                null));

        // Set default details message
        detailsText = view.findViewById(R.id.details_text);
        detailsText.setText(R.string.clickToSeeDetails);

        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up map
        SupportMapFragment mapFragment =
                (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        addEventMarkers(googleMap);
        googleMap.setOnMarkerClickListener((marker) -> {
            String eventID = (String)marker.getTag();
            updateDetails(eventID);
            return true;
        });
    }

    private void addEventMarkers(GoogleMap googleMap) {
        for (Event event : DataCache.getInstance().getFilteredEvents().values()) {
            float hue = COLORS[event.getEventType().hashCode() % COLORS.length];
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));

            marker.setTag(event.getEventID());
        }
    }

    private void updateDetails(String eventID) {
        Event event = DataCache.getInstance()
                .getEvents().get(eventID);
        Person eventPerson = DataCache.getInstance()
                .getPersons().get(event.getPersonID());

        detailsText.setText(getString(R.string.eventDetails,
                eventPerson.getFirstName(),
                eventPerson.getLastName(),
                event.getEventType(),
                event.getCity(),
                event.getCountry(),
                event.getYear()));

        switch (eventPerson.getGender()) {
            case "m":
                detailsIcon.setImageDrawable(getDrawable(getResources(),
                        R.drawable.icon_man,
                        null));
                break;
            case "f":
                detailsIcon.setImageDrawable(getDrawable(getResources(),
                        R.drawable.icon_woman,
                        null));
                break;
            default:
                break;
        }
    }
}