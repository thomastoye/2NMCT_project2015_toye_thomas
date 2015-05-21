package be.thomastoye.findafrietkot;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidmapsextensions.AnimationSettings;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import be.thomastoye.findafrietkot.model.Frietkot;
import be.thomastoye.findafrietkot.model.FrietkotMarkerData;


public class TopFrietkotenFragment extends Fragment {
    private TopFrietkotenProvider topFrietkotenProvider;
    private ListView listView;
    private MainFragment.OnSelectFrietkotListener onSelectFrietkotListener;

    public TopFrietkotenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            topFrietkotenProvider = (TopFrietkotenProvider) activity;
            onSelectFrietkotListener = (MainFragment.OnSelectFrietkotListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TopFrietkotenProvider and onSelectFrietkotListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_top_frietkoten, container, false);

        List<Frietkot> topFrietkoten = topFrietkotenProvider.getTopFrietkoten();
        final TopFrietkotenAdapter adapter = new TopFrietkotenAdapter(getActivity().getBaseContext(), topFrietkoten);

        listView = (ListView) v.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                onSelectFrietkotListener.onSelectFrietkotMarker(adapter.getByIndex(position));
            }
        });


        return v;
    }

    public static TopFrietkotenFragment newInstance() {
        TopFrietkotenFragment fragment = new TopFrietkotenFragment();

        Bundle args = new Bundle();
        //args.putString(FRIETKOT_NAME, name);
        fragment.setArguments(args);

        return fragment;
    }

    public interface TopFrietkotenProvider {
        public List<Frietkot> getTopFrietkoten();
    }

    private class TopFrietkotenAdapter extends ArrayAdapter<Frietkot> {
        private List<Frietkot> values = new ArrayList<>();
        private Context context;

        public TopFrietkotenAdapter(Context context, List<Frietkot> objects) {
            super(context, R.layout.simple_frietkot_item, objects);
            this.context = context;
            this.values = objects;
        }

        @Override
        public long getItemId(int position) {
            Frietkot item = getItem(position);
            return values.indexOf(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public Frietkot getByIndex(int pos) {
            return values.get(pos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.simple_frietkot_item, parent, false);

            TextView name = (TextView) rowView.findViewById(R.id.frietkotName);
            name.setText(values.get(position).getName());

            TextView address = (TextView) rowView.findViewById(R.id.frietkotAddress);
            address.setText("In " + values.get(position).getGeocode().getCity());

            return rowView;
        }
    }

}
