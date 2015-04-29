package be.thomastoye.findafrietkot;


import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.MapFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.androidmapsextensions.Circle;
import com.androidmapsextensions.CircleOptions;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.InfoWindowAdapter;
import com.androidmapsextensions.GoogleMap.OnInfoWindowClickListener;
import com.androidmapsextensions.GoogleMap.OnMapClickListener;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;

import java.io.IOException;
import java.util.List;

import be.thomastoye.findafrietkot.model.Frietkot;


public class MainFragment extends Fragment {

    GoogleMap map;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.mapView);
        MapFragment mapFragment = (MapFragment) fragment;
        map = mapFragment.getExtendedMap();

        map.setInfoWindowAdapter(new FrietkotInfoWindowAdapter(inflater, getActivity().getBaseContext()));

        map.setClustering(new ClusteringSettings().enabled(true).minMarkersCount(15));

        moveMapCenterToCurrentPositionOrDefault();

        try {
            ObjectMapper mapper = new ObjectMapper();

            List<Frietkot> list = mapper.readValue(getResources().openRawResource(R.raw.friturengeocoded), new TypeReference<List<Frietkot>>() {
            });

            for (Frietkot frietkot : list) {
                try {
                    map.addMarker(new MarkerOptions()
                                    .position(
                                            new LatLng(frietkot.getGeocode().getLatitude(), frietkot.getGeocode().getLongitude())
                                    )
                                    .draggable(false)
                                    .title(frietkot.getName())
                                    .snippet(frietkot.getGeocode().getAddress())
                    ).setData(frietkot);
                } catch (NullPointerException e) {
                    Toast.makeText(getActivity().getBaseContext(), "NPE", Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            Toast.makeText(getActivity().getBaseContext(), "Could open/parse JSON", Toast.LENGTH_LONG).show();
        }

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {


            }
        });

        /*map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getActivity().getBaseContext(), "Marker clicked " + marker.getSnippet(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/
        return v;
    }

    private void moveMapCenterToCurrentPositionOrDefault() {
        LocationManager mng = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));

        List<String> providers = mng.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mng.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }

        location = bestLocation;

        double lat, lon;

        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
        } else {
            // couldn't get current location - go to default
            lat = 50.835757;
            lon = 3.282668;
            Toast.makeText(getActivity().getBaseContext(), "Could not get location", Toast.LENGTH_SHORT).show();
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10);
        map.animateCamera(cameraUpdate);
    }
}
