package be.thomastoye.findafrietkot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import be.thomastoye.findafrietkot.model.Frietkot;
import be.thomastoye.findafrietkot.model.FrietkotMarkerData;


public class MainActivity extends Activity implements MainFragment.OnSelectFrietkotListener, TopFrietkotenFragment.TopFrietkotenProvider {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getBaseContext())
                .addApi(LocationServices.API);

        GoogleApiClient apiClient = builder.build();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSelectFrietkotMarker(Frietkot data) {
        showFrietkotDetailsFragment(data);
    }

    private void showFrietkotDetailsFragment(Frietkot data) {
        String name = data.getName();
        double rating = data.getRating();
        String address = data.getGeocode().getAddress();
        String url = data.getImageUrl();
        String lat = Double.toString(data.getGeocode().getLatitude());
        String lon = Double.toString(data.getGeocode().getLongitude());

        FrietkotDetailsFragment fragment = FrietkotDetailsFragment.newInstance(name, address, rating, url, lat, lon);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.container, fragment, "frietkotDetailsFragment");
        transaction.addToBackStack("mainFragment");

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.topFrietkoten:
                TopFrietkotenFragment fragment = new TopFrietkotenFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.container, fragment, "topFrietkotenFragment");
                transaction.addToBackStack("mainFragment");

                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public List<Frietkot> getTopFrietkoten() {
        try{
            return Frietkot.getAll(getResources().openRawResource(R.raw.friturengeocoded)).subList(0,10);
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "Could not get top frietkoten");
            return null;
        }
    }
}
