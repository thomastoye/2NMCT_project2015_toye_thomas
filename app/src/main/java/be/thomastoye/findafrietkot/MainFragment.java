package be.thomastoye.findafrietkot;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
        map = mapFragment.getMap();


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
                    );
                } catch (NullPointerException e) {
                    Toast.makeText(getActivity().getBaseContext(), "NPE", Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            Toast.makeText(getActivity().getBaseContext(), "Could open/parse JSON", Toast.LENGTH_LONG).show();
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getActivity().getBaseContext(), "Marker clicked " + marker.getSnippet(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return v;
    }
}
