package be.thomastoye.findafrietkot;

import android.app.Activity;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import be.thomastoye.findafrietkot.model.Frietkot;
import be.thomastoye.findafrietkot.model.FrietkotMarkerData;
import be.thomastoye.findafrietkot.model.RemoteImage;
import be.thomastoye.findafrietkot.model.RemoteImageFactory;


/**
 * This class is a custom adapter that turns Markers into Views
 * It's used to display Markers on a Google Maps fragment
 * Some tricks are used here:
 * - Google Maps extensions is used, it's the same as the Google Maps API but extends it
 * - One of the extension mechanism is used here: storing extra data. It's expected that you
 * store an instance of FrietkotMarkerDate in the Marker
 * - If an image exists, it will be set, after it's retrieved from the internet. An async task is
 * used for that
 * - Just setting the image doesn't do the trick, since the Google Maps API converts this View
 * into a static fragment (a static image), an has no way of knowing you updated the View
 * - So you have to call Marker.showInfoWindow() to signal you updated the View and that the
 * info window needs to be regenerated
 * - To get the images from the internet, Picasso is used. It's a library that caches images
 * so you don't need to request the same image twice
 * - Picasso needs some help because it doesn't remember if it already requested the image. For
 * this purpose, there's a field on FrietKotMarkerData that remembers this. I originally wanted
 * to go with a Tuple, but I believe this way is more extensible (also, type erasure)
 *
 * Overall, this answer on Stack Overflow was of tremendous help:
 * http://stackoverflow.com/questions/18938187/add-an-image-from-url-into-custom-infowindow-google-maps-v2
 */
public class FrietkotInfoWindowAdapter implements InfoWindowAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ImageView imageView;
    private TextView snippet;
    private TextView title;
    private Activity activity;

    FrietkotInfoWindowAdapter(LayoutInflater inflater, Context context, Activity activity) {
        this.inflater = inflater;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        final View v = inflater.inflate(R.layout.map_info_window, null);

        title = (TextView) v.findViewById(R.id.frietKotInfoWindowTitle);
        snippet = (TextView) v.findViewById(R.id.frietKotInfoWindowSnippet);
        imageView = (ImageView) v.findViewById(R.id.frietKotInfoWindowImage);

        Log.d(getClass().getSimpleName(), "Now about to draw an info window, the title is " + marker.getTitle());

        title.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());

        if (marker.getData() instanceof FrietkotMarkerData) {
            FrietkotMarkerData extraData = marker.getData();
            if (extraData.getFrietkot().getImageUrl() != null
                    && !extraData.getFrietkot().getImageUrl().isEmpty()
                    && RemoteImageFactory.createImage(extraData.getFrietkot().getImageUrl()) != null) {
                String remoteImageUrl = RemoteImageFactory.createImage(extraData.getFrietkot().getImageUrl()).getSmallImageUrl();

                if (extraData.getImageHasBeenRetrievedAlready()) {
                    Picasso.with(context).load(remoteImageUrl).into(imageView);
                } else {
                    // this is the first time loading the image
                    extraData.setImageHasBeenRetrievedAlready(true);
                    Picasso.with(context).load(remoteImageUrl).into(imageView, new PicassoCallback(marker, v));
                }
            }
        } else {
            return null;
        }

        return v;
    }


    /**
     * Callback class for Picasso
     * onSuccess will be called on successful request
     */
    private class PicassoCallback implements Callback {
        private Marker marker;
        private View v;

        private PicassoCallback(Marker marker, View v) {
            this.marker = marker;
            this.v = v;
        }

        @Override
        public void onSuccess() {
            Log.d(getClass().getSimpleName(),
                    "Image loaded for marker infoview, the title is " + marker.getTitle()
            );

            marker.showInfoWindow();
        }

        @Override
        public void onError() {
            Log.d(getClass().getSimpleName(),
                    "Was unable to get image from remote server for marker with title " + marker.getTitle()
            );
        }
    }
}
