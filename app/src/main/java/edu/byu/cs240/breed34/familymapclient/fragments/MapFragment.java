package edu.byu.cs240.breed34.familymapclient.fragments;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.asynchronous.HandlerBase;
import edu.byu.cs240.breed34.familymapclient.asynchronous.tasks.GetEventConnectionsTask;
import edu.byu.cs240.breed34.familymapclient.client.DataCache;
import edu.byu.cs240.breed34.familymapclient.client.models.ConnectionType;
import edu.byu.cs240.breed34.familymapclient.client.models.EventConnection;
import models.Event;
import models.Person;

/**
 * The fragment for displaying family history
 * data on a Google map.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    /**
     * Array of the colors to be used for event markers.
     */
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

    /**
     * View for the details icon.
     */
    private ImageView detailsIcon;

    /**
     * View for the details text.
     */
    private TextView detailsText;

    /**
     * The lines on the map.
     */
    private List<Polyline> lines;

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
            addLines(googleMap, eventID);

            return true;
        });
    }

    private void addEventMarkers(GoogleMap googleMap) {
        for (Event event : DataCache.getInstance().getFilteredEvents().values()) {
            // Get color by event type hash code
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

        // Set details text
        detailsText.setText(getString(R.string.eventDetails,
                eventPerson.getFirstName(),
                eventPerson.getLastName(),
                event.getEventType(),
                event.getCity(),
                event.getCountry(),
                event.getYear()));

        // Set details icon
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

    private void addLines(GoogleMap googleMap, String eventID) {
        Handler connectionsHandler = new HandlerBase(
                // Callback to execute if success.
                (bundle) -> {
                    // Get connections from bundle
                    String connectionJson = bundle.getString(
                            GetEventConnectionsTask.CONNECTIONS_KEY);
                    Type connectionsType = new TypeToken<ArrayList<EventConnection>>(){}.getType();
                    List<EventConnection> connections = new Gson().fromJson(connectionJson,
                            connectionsType);

                    addLinesToMap(connections, googleMap);
                },
                // Callback to execute if error.
                (bundle) -> {
                    Toast.makeText(getActivity(),
                            R.string.connectionsError,
                            Toast.LENGTH_SHORT).show();
                });

        GetEventConnectionsTask connectionsTask = new GetEventConnectionsTask(
                connectionsHandler,
                eventID);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(connectionsTask);
    }

    private void addLinesToMap(List<EventConnection> connections, GoogleMap googleMap) {
        clearLines();

        for (EventConnection connection : connections) {
            LatLng start = new LatLng(connection.getFirstEvent().getLatitude(),
                    connection.getFirstEvent().getLongitude());
            LatLng end = new LatLng(connection.getSecondEvent().getLatitude(),
                    connection.getSecondEvent().getLongitude());

            // Handle each connection type
            float defaultWidth = 20.0f;
            switch (connection.getConnectionType()) {
                case SPOUSE:
                    lines.add(googleMap.addPolyline(new PolylineOptions()
                            .add(start)
                            .add(end)
                            .color(Color.MAGENTA)
                            .width(defaultWidth)));
                    break;
                case LIFE_STORY:
                    lines.add(googleMap.addPolyline(new PolylineOptions()
                            .add(start)
                            .add(end)
                            .color(Color.BLUE)
                            .width(defaultWidth)));
                    break;
                case FAMILY_TREE:
                    float width = (defaultWidth / connection.getGeneration());
                    lines.add(googleMap.addPolyline(new PolylineOptions()
                            .add(start)
                            .add(end)
                            .color(Color.GREEN)
                            .width(width)));
                    break;
                default:
                    break;
            }
        }
    }

    private void clearLines() {
        if (lines == null) {
            lines = new ArrayList<>();
        }

        for (Polyline line : lines) {
            line.remove();
        }
    }
}