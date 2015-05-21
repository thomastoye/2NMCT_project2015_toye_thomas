package be.thomastoye.findafrietkot;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import be.thomastoye.findafrietkot.model.RemoteImage;
import be.thomastoye.findafrietkot.model.RemoteImageFactory;


public class FrietkotDetailsFragment extends Fragment {
    public static final String FRIETKOT_NAME = "be.thomastoye.findafrietkot.FRIETKOT_NAME";
    public static final String FRIETKOT_RATING = "be.thomastoye.findafrietkot.FRIETKOT_RATING";
    public static final String FRIETKOT_ADDRESS = "be.thomastoye.findafrietkot.FRIETKOT_ADDRESS";
    public static final String FRIETKOT_IMAGE_URL = "be.thomastoye.findafrietkot.FRIETKOT_IMAGE_URL";
    public static final String FRIETKOT_LAT = "be.thomastoye.findafrietkot.FRIETKOT_LAT";
    public static final String FRIETKOT_LON = "be.thomastoye.findafrietkot.FRIETKOT_LON";

    public static final String PREFS_NAME = "FrietkotPrefsFile";

    private String name;
    private double rating;
    private String address;
    private RemoteImage image;
    private String lat;
    private String lon;

    TextView frietkotName;
    TextView frietkotRating;
    TextView frietkotAddress;
    ImageView frietkotImage;
    Button frietkotNavigate;

    public FrietkotDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            name = getArguments().getString(FRIETKOT_NAME);
            rating = getArguments().getDouble(FRIETKOT_RATING);
            address = getArguments().getString(FRIETKOT_ADDRESS);
            image = RemoteImageFactory.createImage(getArguments().getString(FRIETKOT_IMAGE_URL));
            lat = getArguments().getString(FRIETKOT_LAT);
            lon = getArguments().getString(FRIETKOT_LON);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frietkot_details, container, false);

        frietkotName = (TextView) v.findViewById(R.id.frietkotName);
        frietkotRating = (TextView) v.findViewById(R.id.frietkotRating);
        frietkotAddress = (TextView) v.findViewById(R.id.frietkotAddress);
        frietkotImage = (ImageView) v.findViewById(R.id.frietkotImage);

        if (image != null) {
            Log.d(getClass().getSimpleName(), image.getBigImageUrl());
            Picasso.with(getActivity().getBaseContext()).load(image.getBigImageUrl()).into(frietkotImage);
        }

        frietkotName.setText(name);
        frietkotAddress.setText(address);
        if (rating == 0) {
            frietkotRating.setText("Niet gewaardeerd");
        } else {
            frietkotRating.setText(Double.toString(rating) + " op 5");
        }

        return v;
    }

    public static FrietkotDetailsFragment newInstance(String name, String address, double rating,
                                                      String imageUrl, String lat, String lon) {
        FrietkotDetailsFragment fragment = new FrietkotDetailsFragment();

        // save in Bundle
        Bundle args = new Bundle();
        args.putString(FRIETKOT_NAME, name);
        args.putString(FRIETKOT_ADDRESS, address);
        args.putDouble(FRIETKOT_RATING, rating);
        args.putString(FRIETKOT_IMAGE_URL, imageUrl);
        args.putString(FRIETKOT_LAT, lat);
        args.putString(FRIETKOT_LON, lon);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigate:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + lat + "," + lon));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
