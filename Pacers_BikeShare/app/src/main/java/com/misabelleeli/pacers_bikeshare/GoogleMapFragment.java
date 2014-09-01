package com.misabelleeli.pacers_bikeshare;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
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
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class GoogleMapFragment extends SupportMapFragment implements LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private double currentLat;
    private double currentLong;
    public static double myLat;
    public static double myLong;

    private LayoutInflater Minflater;

    private final String TAG_LOC = "Location";
    private final String TAG_Name = "Name";
    private final String TAG_ADDR = "Address";
    private final String TAG_Bikes = "BikesAvailable";
    private final String TAG_Docks = "DocksAvailable";
    private final String TAG_TotalDocks = "TotalDocks";
    String url = "https://publicapi.bcycle.com" +
            "/api/1.0/ListProgramKiosks/75";

    public GoogleMapFragment() {
        // Required empty public constructor
    }

    public void getData()
    {

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
            if(bk != null)
            {
                try{
                    for(int i = 0; i < bk.length(); i++)
                    {
                        JSONObject bike = bk.getJSONObject(i);

                        //Get the necessary values needed here
                        JSONObject loc = bike.getJSONObject(TAG_LOC);
                        String latitude = loc.getString("Latitude");
                        String longitude = loc.getString("Longitude");

                        JSONObject addr = bike.getJSONObject(TAG_ADDR);
                        String streetName = addr.getString("Street");

                        String title = bike.getString(TAG_Name);

                        String bikesAvail = bike.getString(TAG_Bikes);
                        String docksAvail = bike.getString(TAG_Docks);
                        String totalDocks = bike.getString(TAG_TotalDocks);

                        lat[i] = latitude;
                        lon[i] = longitude;
                        street[i] = "\n"+ TAG_Bikes+": "+bikesAvail
                                + "\n" + TAG_Docks+": "+ docksAvail
                                + "\n" + TAG_TotalDocks+": "+ totalDocks;
                        stationName[i] = title;
                        docks[i] = docksAvail;
                        bikesAv[i] = bikesAvail;
                        streetNameOnly[i] = streetName;

                        Location myLoc = new Location("a");
                        //myLat = 39.76789474;
                        //myLong = -86.15843964;
                        myLoc.setLatitude(myLat);
                        myLoc.setLongitude(myLong);

                        Location stationLoc = new Location("b");
                        stationLoc.setLatitude(Double.parseDouble(latitude));
                        stationLoc.setLongitude(Double.parseDouble(longitude));

                        String tempMiles = String.format("%.1f", myLoc.distanceTo(stationLoc)* Float.parseFloat("0.000621371"));
                        //miles[i] = String.format("%.1f", tempMiles);
                        miles[i] = Float.parseFloat(tempMiles);
                        publishProgress(street[i]);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Log.e("JSONParser", "Error, no data");
            }

            return null;
        }

        //Update GUI here
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            int imgName = 0;

            for(int i = 0; i < numStations; i++) {

                if(docks[i].equals("0"))
                {
                    imgName = R.drawable.ic_launcher_grey;
                }
                else
                {
                    imgName = R.drawable.ic_launcher;
                }
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(lat[i]), Double.parseDouble(lon[i])))
                        .title(stationName[i])
                        .snippet(street[i])
                        .icon(BitmapDescriptorFactory.fromResource(imgName)));

                StationFragment.populateStations(stationName[i], streetNameOnly[i],
                        Integer.parseInt(bikesAv[i]),Integer.parseInt(docks[i]),miles[i]);

            }
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

        if(!isOnline(getActivity())){
            buildAlertMessageNoInternet();
        }
        else {

            mMap = getMap();

            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }
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
                    String url = "http://maps.google.com/maps?daddr=" + currentLat + "," + currentLong;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });
        }
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;

    }

    private void buildAlertMessageNoInternet(){
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setMessage("No network Connection.\nPlease Exit the application and connect " +
                "to Wifi or Enable Network to continue. Then re-open the app.");
        b.setTitle("Internet Connection");
        b.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
           public void onClick(DialogInterface dialog, int which){

            }
        });

        final AlertDialog alert = b.create();
        alert.show();
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("GPS Location");
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateMarkerSnippet(final Marker marker) {
        final String markerTitle = marker.getTitle();

        AsyncTask<Void, Void, Void> update = new AsyncTask<Void, Void, Void>() {
            private String street = "";
            private int docks = 0;
            //You get Data here
            @Override
            protected Void doInBackground(Void... voids) {
                com.misabelleeli.pacers_bikeshare.JSONParser jp =
                        new com.misabelleeli.pacers_bikeshare.JSONParser();

                JSONArray bk = jp.getJSONFromUrl(url);
                if(bk != null)
                {
                    try{
                        for(int i = 0; i < bk.length(); i++)
                        {
                            JSONObject bike = bk.getJSONObject(i);

                            String title = bike.getString(TAG_Name);

                            if(title.equals(markerTitle)) {
                                String bikesAvail = bike.getString(TAG_Bikes);
                                String docksAvail = bike.getString(TAG_Docks);
                                String totalDocks = bike.getString(TAG_TotalDocks);

                                docks = Integer.parseInt(docksAvail);
                                street = "\n" + TAG_Bikes + ": " + bikesAvail
                                        + "\n" + TAG_Docks + ": " + docksAvail
                                        + "\n" + TAG_TotalDocks + ": " + totalDocks;
                                break;
                            }
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    Log.e("MarkerUpdate", "Error, no data");
                }

                return null;
            }

            //Update GUI here
            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
                int imgName = 0;
                if(docks == 0)
                {
                    imgName = R.drawable.ic_launcher_grey;
                }
                else
                {
                    imgName = R.drawable.ic_launcher;
                }
                marker.setIcon(BitmapDescriptorFactory.fromResource(imgName));
                marker.setSnippet(street + "\nUpdated");
                marker.showInfoWindow();
            }
        };
        update.execute((Void[]) null);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLat = location.getLatitude();
        myLong = location.getLongitude();
        LatLng latLng = new LatLng(myLat,myLong);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
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
            LatLng latLng = marker.getPosition();
            String name = marker.getTitle();
            String desp = marker.getSnippet();

            // Getting reference to the TextView to set latitude
            TextView title = (TextView) v.findViewById(R.id.title);

            // Getting reference to the TextView to set longitude
            TextView description = (TextView) v.findViewById(R.id.snippet);

            currentLat = latLng.latitude;
            currentLong = latLng.longitude;

            title.setText(name);

            description.setText(desp+"\n\nClick for Directions.");

            // Returning the view containing InfoWindow contents
            return v;

        }
    }
}
