package be.thomastoye.findafrietkot;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidmapsextensions.MapFragment;

import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import be.thomastoye.findafrietkot.model.Frietkot;
import be.thomastoye.findafrietkot.model.FrietkotMarkerData;


public class MainFragment extends Fragment {

    private static View view;
    private GoogleMap map;
    private OnSelectFrietkotListener onSelectFrietkotListener;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onSelectFrietkotListener = (OnSelectFrietkotListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSelectFrietkotListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.fragment_main, container, false);

            Fragment fragment = getChildFragmentManager().findFragmentById(R.id.mapView);
            MapFragment mapFragment = (MapFragment) fragment;
            map = mapFragment.getExtendedMap();

            map.setInfoWindowAdapter(new FrietkotInfoWindowAdapter(inflater, getActivity().getBaseContext(), getActivity()));

            map.setClustering(new ClusteringSettings().enabled(true).minMarkersCount(15));

            moveMapCenterToCurrentPositionOrDefault();

            try {
                List<Frietkot> list = Frietkot.getAll(getResources().openRawResource(R.raw.friturengeocoded));

                for (Frietkot frietkot : list) {
                    try {
                        map.addMarker(new MarkerOptions()
                                        .position(new LatLng(frietkot.getGeocode().getLatitude(), frietkot.getGeocode().getLongitude()))
                                        .title(frietkot.getName())
                                        .snippet(frietkot.getGeocode().getAddress())
                        ).setData(new FrietkotMarkerData(frietkot));
                    } catch (NullPointerException e) {
                        // do nothing
                    }
                }
            } catch (IOException e) {
                Toast.makeText(getActivity().getBaseContext(), "Could open or parse JSON", Toast.LENGTH_LONG).show();
            }

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    onSelectFrietkotListener.onSelectFrietkotMarker((FrietkotMarkerData) marker.getData());

                }
            });
        } catch (InflateException e) {
            // map already exists, return as is
        }
        return view;
    }

    // This code was inspired by http://stackoverflow.com/questions/6181704/good-way-of-getting-the-users-location-in-android
    //  and seems to have better accuracy than default LocationManager.getBestProvider()
    private void moveMapCenterToCurrentPositionOrDefault() {
        LocationManager mng = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

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

    public interface OnSelectFrietkotListener {
        public void onSelectFrietkotMarker(FrietkotMarkerData data);
    }

}
