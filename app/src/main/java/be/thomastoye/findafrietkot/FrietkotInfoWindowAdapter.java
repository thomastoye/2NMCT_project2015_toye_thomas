package be.thomastoye.findafrietkot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.Circle;
import com.androidmapsextensions.CircleOptions;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap.InfoWindowAdapter;
import com.androidmapsextensions.GoogleMap.OnInfoWindowClickListener;
import com.androidmapsextensions.GoogleMap.OnMapClickListener;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import be.thomastoye.findafrietkot.model.Frietkot;

public class FrietkotInfoWindowAdapter implements InfoWindowAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ImageView imageView;
    private TextView snippet;
    private TextView title;

    FrietkotInfoWindowAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        final View v = inflater.inflate(R.layout.map_info_window, null);

        title = (TextView) v.findViewById(R.id.frietKotInfoWindowTitle);
        snippet = (TextView) v.findViewById(R.id.frietKotInfoWindowSnippet);
        imageView = (ImageView) v.findViewById(R.id.frietKotInfoWindowImage);

        title.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());

        if (marker.getData() instanceof Frietkot) {
            Frietkot extraData = (Frietkot) marker.getData();
            if (extraData.getImageUrl() != null && !extraData.getImageUrl().isEmpty()) {
                try {
                    imageView.setImageBitmap(new DownloadImageTask(imageView).execute(extraData.getImageUrl()).get(1000, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }

        return v;
    }

    /*
        The following class downloads an image from the internet to use as a source in an ImageView
        It was taken from the following SO answer:
        http://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
        //I modified it to call showInfoWindow() on the info window after it finished loading to make it redraw
    */

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
