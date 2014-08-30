package com.misabelleeli.pacers_bikeshare;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StationFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{
    private static List<Station> stations = new ArrayList<Station>();
    private static List<String> stationNamesList = new ArrayList<String>();
    private List<Station> favorites = new ArrayList<Station>();
    private SwingBottomInAnimationAdapter swing;
    private ArrayAdapter<Station> adapter;
    private SwipeRefreshLayout swipe_layout;
    private ListView myListView;
    private TextView defaultBackgroundView;
    private EditText searchView;

    private SharedPreferences mPrefs;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;

    private View rootView;

    private final String TAG_LOC = "Location";
    private final String TAG_Name = "Name";
    private final String TAG_ADDR = "Address";
    private final String TAG_Bikes = "BikesAvailable";
    private final String TAG_Docks = "DocksAvailable";
    private final String TAG_TotalDocks = "TotalDocks";
    String url = "https://publicapi.bcycle.com" +
            "/api/1.0/ListProgramKiosks/75";

    public StationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_station, container, false);

        mPrefs = getActivity().getSharedPreferences("favorite", Context.MODE_PRIVATE);

        if(stations.size() == 0)
        {
            //getData();
        }

        //Ascending order by distance.
        Collections.sort(stations);

        final ToggleButton favoriteToggle = (ToggleButton) rootView.findViewById(R.id.favoriteToggle);
        favoriteToggle.setOnCheckedChangeListener(this);

        defaultBackgroundView = (TextView) rootView.findViewById(R.id.defaultBackgroundView);
        swipe_layout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipe_layout.setColorScheme(
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                R.color.blue);
        myListView = (ListView) rootView.findViewById(R.id.stationsListview);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        if(!searchView.getText().toString().equals("")) {
                            searchView.setText("");
                        }
                        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        if(favoriteToggle.isChecked()) {
                            favorites = readObject("favorites");
                            if(favorites.size() == 0) {
                                defaultBackgroundView.setVisibility(View.VISIBLE);
                                myListView.setAdapter(null);
                            }
                            else {
                                //sort by distance
                                Collections.sort(favorites);
                                adapter = new MyListAdapter(getActivity().getBaseContext(), R.layout.station_view3, favorites);
                                swing = new SwingBottomInAnimationAdapter(adapter);
                                swing.setAbsListView(myListView);
                                myListView.setAdapter(swing);
                                myListView.setTextFilterEnabled(true);
                            }
                        } else {
                            adapter = new MyListAdapter(getActivity().getBaseContext(), R.layout.station_view3, stations);
                            swing = new SwingBottomInAnimationAdapter(adapter);
                            swing.setAbsListView(myListView);
                            myListView.setAdapter(swing);
                            myListView.setTextFilterEnabled(true);
                        }
                        swipe_layout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        adapter = new MyListAdapter(getActivity().getBaseContext(), R.layout.station_view3, stations);

        generateList(myListView);

        searchView = (EditText) rootView.findViewById(R.id.search_view);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(!searchView.getText().toString().equals("")) {
                    adapter.getFilter().filter(charSequence.toString());
                }
                else {
                    generateList(myListView);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static void populateStations(String stationName, String addr, int bike, int dock, float miles) {

        if(stationNamesList.contains(stationName))
        {
        }
        else {
            stationNamesList.add(stationName);
            stations.add(new Station(stationName, addr, bike, dock, miles));
        }
        /*
        stations.add(new Station("IUPUI Campus Center", "401 Univesity Blvd", 7, 9, 4));
        stations.add(new Station("North End of Canal", "1325 Canal Walk", 7, 4, 8));
        stations.add(new Station("Michigan/Blackford", "525 N. Blackford St", 12, 4, 12));
        stations.add(new Station("White River State Park", "650 s. Washington St", 4, 9, 20));
        stations.add(new Station("Victory Field", "99 S. West St", 14, 2, 25));
        stations.add(new Station("Government Center", "364 W. Washington St", 2, 10, 30));
        stations.add(new Station("Convention Center", "151 W. Georgia St", 14, 1, 40));
        stations.add(new Station("Michigan/Senate", "300 N. Michigan St", 4, 8, 45));
        stations.add(new Station("Glick Peace Walk", "625 N. Capitol Ave", 12, 4, 52));
        stations.add(new Station("Convention Center", "50 S. Capitol Ave", 7, 5, 58));
        stations.add(new Station("Washington/Illinois", "101 W. Washington St", 12, 0, 62));
        stations.add(new Station("Washington/Meredian", "2 W. Washington St", 4, 10, 76));
        stations.add(new Station("City County Building", "200 E. Washington St", 10, 5, 80));
        stations.add(new Station("Central Library", "40 E. St. Clair St", 1, 5, 82));
        stations.add(new Station("City Market", "108 N. Alabama St", 2, 8, 90));
        stations.add(new Station("Athenaeum", "401 E. Michigan St", 12, 4, 93));
        stations.add(new Station("Monument Circle", "121 Monument Circle", 2, 8, 99));
        stations.add(new Station("Bankers Life Fieldhouse", "169 s. Pennsylvania St",6, 8, 30));
        stations.add(new Station("Fletcher Place", "531 Virginia Ave", 4, 8, 60));
        stations.add(new Station("Mass Ave/Alabama", "372 N. Alabama St", 5, 5, 14));
        stations.add(new Station("Fletcher Place", "749 Virginia Ave", 12, 2, 65));
        stations.add(new Station("Mass Ave/Park", "680 Mass Ave", 1, 9, 24));
        stations.add(new Station("North Mass Ave", "949 Mass Ave", 5, 9, 85));
        stations.add(new Station("Fountain Square", "1066 Virginia Ave", 12, 2, 95));

        */
    }
    private void generateList(ListView view) {

        //Update Favorites
        favorites = readObject("favorites");
        for(int i = 0; i < stations.size(); i++) {
            if(favorites.size() == 0) {
                stations.get(i).setFavorite(false);
            }
            for(int j = 0 ; j < favorites.size(); j++) {
                if(stations.get(i).getAddress().equals(favorites.get(j).getAddress())) {
                    stations.get(i).setFavorite(true);
                    break;
                }
                else {
                    stations.get(i).setFavorite(false);
                }
            }
        }
        defaultBackgroundView.setVisibility(View.GONE);
        adapter = new MyListAdapter(getActivity().getBaseContext(), R.layout.station_view3, stations);
        view.setAdapter(adapter);
        view.setTextFilterEnabled(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        favorites = readObject("favorites");
        if(b) {
            if(favorites.size() == 0) {
                defaultBackgroundView.setVisibility(View.VISIBLE);
                myListView.setAdapter(null);
                return;
            }
            //sort by distance
            Collections.sort(favorites);
            adapter = new MyListAdapter(getActivity().getBaseContext(), R.layout.station_view3, favorites);
            swing = new SwingBottomInAnimationAdapter(adapter);
            swing.setAbsListView(myListView);
            myListView.setAdapter(swing);
        }
        else {
            defaultBackgroundView.setVisibility(View.GONE);
            adapter = new MyListAdapter(getActivity().getBaseContext(), R.layout.station_view3, stations);
            swing = new SwingBottomInAnimationAdapter(adapter);
            swing.setAbsListView(myListView);
            myListView.setAdapter(swing);
            myListView.setTextFilterEnabled(true);
        }
    }
    public List<Station> readObject(String key) {
        ByteArrayInputStream bais;
        List<Station> result = new ArrayList<Station>();
        if (!mPrefs.getString(key, null).equals("initial")) {
            try {
                String encodedString = mPrefs.getString(key, null);
                byte[] input = Base64.decode(encodedString, Base64.DEFAULT);
                bais = new ByteArrayInputStream(input);
                ois = new ObjectInputStream(bais);

                result = (ArrayList<Station>) ois.readObject();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
    public void storeObject(String key, List<Station> obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] output = baos.toByteArray();
            String encodedString = Base64.encodeToString(output, Base64.DEFAULT);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.remove(key);
            editor.putString(key, encodedString);
            editor.commit();



        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class MyListAdapter extends ArrayAdapter<Station> implements Filterable {
        List<Station> list;
        List<Station> original_list;
        private StationFilter filter;
        public MyListAdapter(Context context, int resourceId, List<Station> list) {
            super(context, resourceId, list);
            this.list = list;
            this.original_list = new ArrayList<Station>(list);
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new StationFilter();
            }
            return filter;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.station_view3, parent, false);
            }
            Station curStation = list.get(position);
            TextView station_name = (TextView)rowView.findViewById(R.id.station_name);
            TextView station_address = (TextView)rowView.findViewById(R.id.station_address);
            TextView station_bikes = (TextView)rowView.findViewById(R.id.station_bikes);
            TextView station_docks = (TextView)rowView.findViewById(R.id.station_docks);
            TextView address_view = (TextView)rowView.findViewById(R.id.address_view);
            TextView status_view = (TextView)rowView.findViewById(R.id.status_view);
            TextView distance_view = (TextView)rowView.findViewById(R.id.distance_view);
            final ImageButton favorite_button = (ImageButton)rowView.findViewById(R.id.favorite_button);
            ImageView bike_avail = (ImageView)rowView.findViewById(R.id.bike_avail);
            ImageView dock_avail = (ImageView)rowView.findViewById(R.id.dock_avail);

            //Station name
            station_name.setText(curStation.getName());

            //Station Address
            station_address.setText(curStation.getAddress());

            //Station Bikes
            station_bikes.setText(curStation.getBikes()+"");

            //Station Docks
            station_docks.setText(curStation.getDocks()+"");

            float totalBike = curStation.getBikes() + curStation.getDocks();
            float bike_ratio = (curStation.getBikes()/totalBike*100);
            float dock_ratio = 100 - bike_ratio;
            bike_avail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, bike_ratio));
            dock_avail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, dock_ratio));


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
                    favorites = readObject("favorites");
                    Station curStation = list.get(position);
                    if (curStation.getFavorite()) {
                        curStation.setFavorite(false);
                        favorite_button.setBackgroundResource(R.drawable.unfavorite);
                        ArrayList<Station> finalFavorites = new ArrayList<Station>();
                        for(int i = 0 ; i < favorites.size(); i++) {
                            if(!curStation.getAddress().equals(favorites.get(i).getAddress()))
                            {
                                finalFavorites.add(favorites.get(i));
                            }
                        }
                        storeObject("favorites", finalFavorites);
                    } else {
                        curStation.setFavorite(true);
                        favorite_button.setBackgroundResource(R.drawable.favorite);
                        favorites.add(curStation);
                        storeObject("favorites", favorites);
                    }

                }
            });

            rowView.setTag(curStation.getName());
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
        private class StationFilter extends Filter{

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                charSequence = charSequence.toString().toLowerCase();
                if(charSequence == null || charSequence.length() == 0) {
                    results.values = original_list;
                    results.count = original_list.size();
                }
                else {
                    List<Station> result = new ArrayList<Station>();
                    for (Station station : original_list) {
                        if(station.getAddress().toLowerCase().contains(charSequence)) {
                            result.add(station);
                        }
                    }
                    results.values = result;
                    results.count = result.size();
                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults.count == 0)
                {
                    list = original_list;
                    notifyDataSetInvalidated();
                }
                else
                {
                    list = (List<Station>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        }
    }
}
