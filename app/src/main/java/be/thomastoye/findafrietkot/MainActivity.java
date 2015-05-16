package be.thomastoye.findafrietkot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import be.thomastoye.findafrietkot.model.FrietkotMarkerData;


public class MainActivity extends Activity implements MainFragment.OnSelectFrietkotListener {

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectFrietkotMarker(FrietkotMarkerData data) {
        showFrietkotDetailsFragment(data);
    }

    private void showFrietkotDetailsFragment(FrietkotMarkerData data) {
        String name = data.getFrietkot().getName();
        double rating = data.getFrietkot().getRating();
        String address = data.getFrietkot().getGeocode().getAddress();
        String url = data.getFrietkot().getImageUrl();
        String lat = Double.toString(data.getFrietkot().getGeocode().getLatitude());
        String lon = Double.toString(data.getFrietkot().getGeocode().getLongitude());

        FrietkotDetailsFragment fragment = FrietkotDetailsFragment.newInstance(name, address, rating, url, lat, lon);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.container, fragment, "frietkotDetailsFragment");
        transaction.addToBackStack("mainFragment");

        transaction.commit();
    }
}
