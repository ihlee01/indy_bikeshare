package com.misabelleeli.pacers_bikeshare;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StationFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private static List<Station> stations = new ArrayList<Station>();
    private static List<Station> favorites = new ArrayList<Station>();
    private static SwingBottomInAnimationAdapter swing;
    private static ArrayAdapter<Station> adapter;
    private SwipeRefreshLayout swipe_layout;
    private static ListView myListView;
    private TextView defaultBackgroundView;
    private EditText searchView;

    private SharedPreferences mPrefs;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;

    private View rootView;

    private static final String TAG_LOC = "Location";
    private static final String TAG_Name = "Name";
    private static final String TAG_ADDR = "Address";
    private static final String TAG_Bikes = "BikesAvailable";
    private static final String TAG_Docks = "DocksAvailable";
    private static String url = "https://publicapi.bcycle.com" +
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

        if (stations.size() == 0) {
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
                    @Override
                    public void run() {
                        if (!searchView.getText().toString().equals("")) {
                            searchView.setText("");
                        }
                        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        if (favoriteToggle.isChecked()) {
                            favorites = readObject("favorites");
                            if (favorites.size() == 0) {
                                defaultBackgroundView.setVisibility(View.VISIBLE);
                                myListView.setAdapter(null);
                            } else {
                                //sort by distance
                                Collections.sort(favorites);
                                for (int i = 0; i < stations.size(); i++) {
                                    if (favorites.size() == 0) {
                                        stations.get(i).setFavorite(false);
                                    }
                                    for (int j = 0; j < favorites.size(); j++) {
                                        if (stations.get(i).getAddress().equals(favorites.get(j).getAddress())) {
                                            stations.get(i).setFavorite(true);
                                            break;
                                        } else {
                                            stations.get(i).setFavorite(false);
                                        }
                                    }
                                }
                                adapter = new MyListAdapter(getActivity().getBaseContext(), R.layout.station_view3, favorites);
                                swing = new SwingBottomInAnimationAdapter(adapter);
                                swing.setAbsListView(myListView);
                                myListView.setAdapter(swing);
                                myListView.setTextFilterEnabled(true);
                            }
                        } else {
                            //Getting updated data here
                            updateStations();
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
                if (!searchView.getText().toString().equals("")) {
                    adapter.getFilter().filter(charSequence.toString());
                } else {
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

    public void updateStations() {
        AsyncTask<Void, Void, Void> update = new AsyncTask<Void, Void, Void>() {

            private List<Station> updatedStations = new ArrayList<Station>();

            //You get Data here
            @Override
            protected Void doInBackground(Void... voids) {
                com.misabelleeli.pacers_bikeshare.JSONParser jp =
                        new com.misabelleeli.pacers_bikeshare.JSONParser();


                JSONArray bk = jp.getJSONFromUrl(url);
                if (bk != null) {
                    try {
                        for (int i = 0; i < bk.length(); i++) {
                            JSONObject bike = bk.getJSONObject(i);

                            //Get the necessary values needed here
                            //Get Location info
                            JSONObject loc = bike.getJSONObject(TAG_LOC);
                            String latitude = loc.getString("Latitude");
                            String longitude = loc.getString("Longitude");

                            //Get Street
                            JSONObject addr = bike.getJSONObject(TAG_ADDR);
                            String streetName = addr.getString("Street");

                            //Get Station Name
                            String title = bike.getString(TAG_Name);

                            //Statino name filtering
                            if (title.contains("Fletcher")) {
                                if (title.contains("Norwood")) {
                                    title = "Virginia/Norwood";
                                } else {
                                    title = "Virginia/Merrill";
                                }
                            }
                            if (title.contains("-")) {
                                title = title.substring(0, title.indexOf("-") - 1);
                            }
                            if (title.contains(" at ")) {
                                title = title.substring(0, title.indexOf("at") - 1);
                            }
                            if (title.contains("Indiana")) {
                                title = "Government Center";
                            }
                            if (title.contains(" and ")) {
                                title = title.replace(" and ", "/");
                            }
                            if (title.contains(".")) {
                                title = title.replace(".", "");
                            }
                            if (title.contains("North End")) {
                                title = "North Canal";
                            }

                            //Get station status
                            String bikesAvail = bike.getString(TAG_Bikes);
                            String docksAvail = bike.getString(TAG_Docks);




                            Station curStation;
                            //IF GPS is turned off, then the current Location -> 0, 0
                            if(GoogleMapFragment.myLat == 0 && GoogleMapFragment.myLong == 0) {
                                //Assign a negative number as distance so that it can be recognized from the station Fragment
                                curStation = new Station(title, streetName, Integer.parseInt(bikesAvail), Integer.parseInt(docksAvail), -1, Double.parseDouble(latitude), Double.parseDouble(longitude));

                            }
                            else {
                                Location myLoc = new Location("a");
                                myLoc.setLatitude(GoogleMapFragment.myLat);
                                myLoc.setLongitude(GoogleMapFragment.myLong);

                                Location stationLoc = new Location("b");
                                stationLoc.setLatitude(Double.parseDouble(latitude));
                                stationLoc.setLongitude(Double.parseDouble(longitude));

                                String tempMiles = String.format("%.1f", myLoc.distanceTo(stationLoc) * Float.parseFloat("0.000621371"));

                                curStation = new Station(title, streetName, Integer.parseInt(bikesAvail), Integer.parseInt(docksAvail), Float.parseFloat(tempMiles), Double.parseDouble(latitude), Double.parseDouble(longitude));
                            }
                            updatedStations.add(curStation);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("JSONParser", "Error, no data");
                }

                return null;
            }

            //Update GUI here
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                populateStations(updatedStations);
                Collections.sort(stations);
                Collections.sort(favorites);
                for (int i = 0; i < stations.size(); i++) {
                    if (favorites.size() == 0) {
                        stations.get(i).setFavorite(false);
                    }
                    for (int j = 0; j < favorites.size(); j++) {
                        if (stations.get(i).getAddress().equals(favorites.get(j).getAddress())) {
                            stations.get(i).setFavorite(true);

                            //update favorites too
                            favorites.get(j).setBikes(stations.get(i).getBikes());
                            favorites.get(j).setDocks(stations.get(i).getDocks());

                            break;
                        } else {
                            stations.get(i).setFavorite(false);
                        }
                    }
                }
                storeObject("favorites", favorites);
                swing = new SwingBottomInAnimationAdapter(adapter);
                swing.setAbsListView(myListView);
                myListView.setAdapter(swing);
                myListView.setTextFilterEnabled(true);
            }
        };
        update.execute((Void[]) null);
    }

    public static void populateStations(List<Station> stationList) {
        stations = stationList;
    }

    private void generateList(ListView view) {

        //Update Favorites
        favorites = readObject("favorites");
        for (int i = 0; i < stations.size(); i++) {
            if (favorites.size() == 0) {
                stations.get(i).setFavorite(false);
            }
            for (int j = 0; j < favorites.size(); j++) {
                if (stations.get(i).getAddress().equals(favorites.get(j).getAddress())) {
                    stations.get(i).setFavorite(true);
                    break;
                } else {
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
        if (b) {
            if (favorites.size() == 0) {
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
        } else {
            defaultBackgroundView.setVisibility(View.GONE);
            for (int i = 0; i < stations.size(); i++) {
                if (favorites.size() == 0) {
                    stations.get(i).setFavorite(false);
                }
                for (int j = 0; j < favorites.size(); j++) {
                    if (stations.get(i).getAddress().equals(favorites.get(j).getAddress())) {
                        stations.get(i).setFavorite(true);
                        break;
                    } else {
                        stations.get(i).setFavorite(false);
                    }
                }
            }
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


        } catch (IOException e) {
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
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.station_view3, parent, false);
            }
            Station curStation = list.get(position);
            TextView station_name = (TextView) rowView.findViewById(R.id.station_name);
            TextView station_address = (TextView) rowView.findViewById(R.id.station_address);
            TextView station_bikes = (TextView) rowView.findViewById(R.id.station_bikes);
            TextView station_docks = (TextView) rowView.findViewById(R.id.station_docks);
            TextView distance_view = (TextView) rowView.findViewById(R.id.distance_view);
            final ImageButton favorite_button = (ImageButton) rowView.findViewById(R.id.favorite_button);
            ImageView bike_avail = (ImageView) rowView.findViewById(R.id.bike_avail);
            ImageView dock_avail = (ImageView) rowView.findViewById(R.id.dock_avail);

            //Station name
            station_name.setText(curStation.getName());

            //Station Address
            station_address.setText(curStation.getAddress());

            //Station Bikes
            station_bikes.setText(curStation.getBikes() + "");

            //Station Docks
            station_docks.setText(curStation.getDocks() + "");

            float totalBike = curStation.getBikes() + curStation.getDocks();
            float bike_ratio = (curStation.getBikes() / totalBike * 100);
            float dock_ratio = 100 - bike_ratio;
            bike_avail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, bike_ratio));
            dock_avail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, dock_ratio));

            if(curStation.getDistance() < 0) {
                //GPS is turned off
                distance_view.setText("");
            }
            else {
                distance_view.setText(curStation.getDistance() + " mi");
            }

            if (curStation.getFavorite()) {
                favorite_button.setBackgroundResource(R.drawable.favorite);
            } else {
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
                        for (int i = 0; i < favorites.size(); i++) {
                            if (!curStation.getAddress().equals(favorites.get(i).getAddress())) {
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

            rowView.setTag(curStation.getLatitude() + "," + curStation.getLongtitude());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double lat = Double.parseDouble(view.getTag().toString().split(",")[0]);
                    Double lon = Double.parseDouble(view.getTag().toString().split(",")[1]);

                    String url = "http://maps.google.com/maps?daddr=" + lat + "," + lon;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });
            return rowView;
        }

        private class StationFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                charSequence = charSequence.toString().toLowerCase();
                if (charSequence.length() == 0) {
                    results.values = original_list;
                    results.count = original_list.size();
                } else {
                    List<Station> result = new ArrayList<Station>();
                    for (Station station : original_list) {
                        if (station.getAddress().toLowerCase().contains(charSequence)) {
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
                if (filterResults.count == 0) {
                    list = original_list;
                    notifyDataSetInvalidated();
                } else {
                    list = (List<Station>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        }
    }
}
