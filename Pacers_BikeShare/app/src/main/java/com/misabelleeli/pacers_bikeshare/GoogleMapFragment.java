package com.misabelleeli.pacers_bikeshare;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleMapFragment extends SupportMapFragment implements LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    public static double myLat;
    public static double myLong;

    private LayoutInflater Minflater;

    private final String TAG_LOC = "Location";
    private final String TAG_Name = "Name";
    private final String TAG_ADDR = "Address";
    private final String TAG_Bikes = "BikesAvailable";
    private final String TAG_Docks = "DocksAvailable";
    String url = "https://publicapi.bcycle.com" +
            "/api/1.0/ListProgramKiosks/75";

    private List<Station> stations = new ArrayList<Station>();

    boolean isUpdated = false;

    public GoogleMapFragment() {
        // Required empty public constructor
    }

    public void getData() {

        new JSONParser().execute();
    }

    private class JSONParser extends AsyncTask<Void, String, Void> {

        private int numStations = 25;
        private String[] lat = new String[numStations];
        private String[] lon = new String[numStations];
        private String[] stationName = new String[numStations];
        private String[] street = new String[numStations];
        private String[] docks = new String[numStations];
        private String[] bikesAv = new String[numStations];
        private float[] miles = new float[numStations];
        private String[] streetNameOnly = new String[numStations];

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        //You get Data here
        @Override
        protected Void doInBackground(Void... voids) {
            com.misabelleeli.pacers_bikeshare.JSONParser jp =
                    new com.misabelleeli.pacers_bikeshare.JSONParser();


            JSONArray bk = jp.getJSONFromUrl(url);
            if (bk != null) {
                try {
                    stations = new ArrayList<Station>();
                    for (int i = 0; i < bk.length(); i++) {
                        JSONObject bike = bk.getJSONObject(i);

                        //Get the necessary values needed here
                        JSONObject loc = bike.getJSONObject(TAG_LOC);
                        String latitude = loc.getString("Latitude");
                        String longitude = loc.getString("Longitude");

                        JSONObject addr = bike.getJSONObject(TAG_ADDR);
                        String streetName = addr.getString("Street");

                        String title = bike.getString(TAG_Name);

                        //Statino name filtering
                        if(title.contains("Fletcher")) {
                            if(title.contains("Norwood")) {
                                title = "Virginia/Norwood";
                            }
                            else {
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

                        String bikesAvail = bike.getString(TAG_Bikes);
                        String docksAvail = bike.getString(TAG_Docks);


                        lat[i] = latitude;
                        lon[i] = longitude;
                        street[i] = bikesAvail +","+docksAvail+","+streetName+"";
                        stationName[i] = title;
                        docks[i] = docksAvail;
                        bikesAv[i] = bikesAvail;
                        streetNameOnly[i] = streetName;

                        Location myLoc = new Location("a");
                        myLoc.setLatitude(myLat);
                        myLoc.setLongitude(myLong);

                        Location stationLoc = new Location("b");
                        stationLoc.setLatitude(Double.parseDouble(latitude));
                        stationLoc.setLongitude(Double.parseDouble(longitude));

                        String tempMiles = String.format("%.1f", myLoc.distanceTo(stationLoc) * Float.parseFloat("0.000621371"));
                        miles[i] = Float.parseFloat(tempMiles);
                        publishProgress(street[i]);

                        Station curStation = new Station(title, streetName, Integer.parseInt(bikesAvail), Integer.parseInt(docksAvail), Float.parseFloat(tempMiles), Double.parseDouble(latitude), Double.parseDouble(longitude));

                        stations.add(curStation);
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
            int imgName;
            for (int i = 0; i < numStations; i++) {
                /*
                if (bikesAv[i].equals("0")) {
                    imgName = R.drawable.ic_launcher_grey;
                } else {
                    imgName = R.drawable.ic_launcher;
                }*/
                imgName = R.drawable.ic_launcher;
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(lat[i]), Double.parseDouble(lon[i])))
                        .title(stationName[i])
                        .snippet(street[i])
                        .icon(BitmapDescriptorFactory.fromResource(imgName)));
            }
            StationFragment.populateStations(stations);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Minflater = inflater;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMap = getMap();

        mMap.setMyLocationEnabled(true);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        onLocationChanged(new Location("start"));

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                MIN_TIME, MIN_DISTANCE, this);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        getData();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                updateMarkerSnippet(marker);
                return false;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //This will redirect it to GoogleMaps

                String url = "http://maps.google.com/maps?daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
    }

    private void updateMarkerSnippet(final Marker marker) {
        final String markerTitle = marker.getTitle();


        AsyncTask<Void, Void, Void> update = new AsyncTask<Void, Void, Void>() {
            private String snippet = "";
            //Previous # of bike
            private int bikes = Integer.parseInt(marker.getSnippet().split(",")[0]);
            private String original_street = marker.getSnippet().split(",")[2];

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


                            String title = bike.getString(TAG_Name);

                            JSONObject addr = bike.getJSONObject(TAG_ADDR);
                            String streetName = addr.getString("Street");

                            //Statino name filtering
                            if(title.contains("Fletcher")) {
                                if(title.contains("Norwood")) {
                                    title = "Virginia/Norwood";
                                }
                                else {
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
                            if (streetName.equals(original_street)) {
                                String bikesAvail = bike.getString(TAG_Bikes);

                                //if # of bikes, No need to update - don't refresh
                                if(bikes == Integer.parseInt(bikesAvail)) {
                                    isUpdated = false;
                                }
                                else {
                                    isUpdated = true;
                                }


                                String docksAvail = bike.getString(TAG_Docks);


                                JSONObject loc = bike.getJSONObject(TAG_LOC);
                                myLat = loc.getDouble("Latitude");
                                myLong = loc.getDouble("Longitude");


                                bikes = Integer.parseInt(bikesAvail);
                                snippet = bikesAvail +","+docksAvail+","+streetName;

                                break;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("MarkerUpdate", "Error, no data");
                }

                return null;
            }

            //Update GUI here
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if(isUpdated) {
                    int imgName = 0;
                    /*
                    if (bikes == 0) {
                        imgName = R.drawable.ic_launcher_grey;
                    } else {
                        imgName = R.drawable.ic_launcher;
                    }
                    */
                    imgName = R.drawable.ic_launcher;
                    marker.setPosition(new LatLng(myLat, myLong));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(imgName));
                    marker.setSnippet(snippet);
                    marker.showInfoWindow();
                    isUpdated = false;
                }
            }
        };
        update.execute((Void[]) null);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Start from Indianapolis
        LatLng latLng = new LatLng(39.768403, -86.15806800000001);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        //Custom WindowInfo
        @Override
        public View getInfoContents(Marker marker) {
            // Getting view from the layout file info_window_layout
            View v = Minflater.inflate(R.layout.custom_infowindow, null);

            // Getting the position from the marker
            String name = marker.getTitle();
            String desp = marker.getSnippet();

            // Getting reference to the TextView to set latitude
            TextView station_name = (TextView) v.findViewById(R.id.map_station_name);
            TextView station_address = (TextView) v.findViewById(R.id.map_station_address);
            TextView station_bikes = (TextView) v.findViewById(R.id.map_bikes);
            TextView station_docks = (TextView) v.findViewById(R.id.map_docks);

            ImageView bike_avail = (ImageView) v.findViewById(R.id.map_bike_avail);
            ImageView dock_avail = (ImageView) v.findViewById(R.id.map_dock_avail);


            // Getting reference to the TextView to set longitude
            //TextView description = (TextView) v.findViewById(R.id.snippet);

            station_name.setText(name);
            String[] result = desp.split(",");
            String bikes = result[0];
            String docks = result[1];
            String address = result[2];
            station_address.setText(address);
            station_bikes.setText(bikes);
            station_docks.setText(docks);

            float totalBike = Integer.parseInt(bikes) + Integer.parseInt(docks);
            float bike_ratio = (Integer.parseInt(bikes)/totalBike*100);
            float dock_ratio = 100 - bike_ratio;
            bike_avail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, bike_ratio));
            dock_avail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, dock_ratio));

            // Returning the view containing InfoWindow contents
            return v;

        }
    }
}
