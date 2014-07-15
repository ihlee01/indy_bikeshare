package com.misabelleeli.pacers_bikeshare;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class StationFragment extends Fragment {
    private List<Station> stations = new ArrayList<Station>();


    public StationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        populateStations();

        View rootView = inflater.inflate(R.layout.fragment_station, container, false);

        ListView myListView = (ListView) rootView.findViewById(R.id.stationsListview);

        ArrayAdapter<Station> adapter = new MyListAdapter();
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.w("FUCK", "FUCK CLICKED");
                Toast.makeText(getActivity(), "You click" + stations.get(i).getAddress(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void populateStations() {
        stations.add(new Station("401 E. Michigan St", 8, 3, 4));
        stations.add(new Station("Bankers Life Fieldhouse", 5, 12, 16));
        stations.add(new Station("40 E. St. Clair St", 6, 3, 21));
        stations.add(new Station("City County Building", 6, 8, 23));
        stations.add(new Station("City market", 17, 5, 25));
        stations.add(new Station("Convention Center", 5, 9, 26));
        stations.add(new Station("Fountain Square", 10, 13, 30));
    }

    private class MyListAdapter extends ArrayAdapter<Station> {
        public MyListAdapter() {
            super(getActivity(), R.layout.station_view, stations);
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.station_view, parent, false);
            }
            Station curStation = stations.get(position);
            TextView address_view = (TextView)rowView.findViewById(R.id.address_view);
            TextView status_view = (TextView)rowView.findViewById(R.id.status_view);
            TextView distance_view = (TextView)rowView.findViewById(R.id.distance_view);
            final ImageButton favorite_button = (ImageButton)rowView.findViewById(R.id.favorite_button);



            ////
            //Design debug purpose. Erase when it's confirmed
            final ImageView bike_icon = (ImageView)rowView.findViewById(R.id.bike_icon);
            //////



            address_view.setText(curStation.getAddress());

            String status = "Bikes: " + curStation.getBikes() + " | Docks: " + curStation.getDocks();
            status_view.setText(status);

            distance_view.setText(curStation.getDistance() + " mi");

            if(curStation.getFavorite()) {
                favorite_button.setBackgroundResource(R.drawable.favorite);
            }
            else {
                favorite_button.setBackgroundResource(R.drawable.unfavorite);
            }
            favorite_button.setTag(position);
            favorite_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) favorite_button.getTag();
                    Station curStation = stations.get(position);
                    if (curStation.getFavorite()) {
                        curStation.setFavorite(false);
                        favorite_button.setBackgroundResource(R.drawable.unfavorite);

                        ////
                        //Design debug purpose. Erase when it's confirmed
                        bike_icon.setImageResource(R.drawable.bike_icon2);
                        /////


                    } else {
                        curStation.setFavorite(true);
                        favorite_button.setBackgroundResource(R.drawable.favorite);
                        bike_icon.setImageResource(R.drawable.bike_icon);
                    }

                }
            });
            rowView.setTag(curStation.getAddress());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //To do...

                    //When the row is clicked. view.getTag() returns the address name.

                    //Toast.makeText(getActivity(), ""+view.getTag(), Toast.LENGTH_SHORT).show();
                }
            });



            return rowView;
        }

    }
}
