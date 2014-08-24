package com.misabelleeli.pacers_bikeshare;



import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private double myLat;
    private double myLong;

    private LayoutInflater Minflater;

    /*
    private double []latitudes = {39.76737,39.77224,39.77643,39.77475,39.77418,39.76885,
            39.78179,39.77964,39.77564,39.76595,39.76702,39.76720,39.76832,
            39.77338,39.77669,39.75241,39.75740,39.75893,39.76423,39.76593,
            39.76866,39.76722,39.77803,39.76481,39.77383};
    private double []longtitudes={-86.16474,-86.15260,-86.14727,-86.16984,-86.16348,
            -86.15736,-86.16590, -86.14212,-86.15217,-86.16689,-86.16016,-86.15832,
            -86.17027,
            -86.17543,-86.16119,-86.13995,-86.14549,-86.14700,-86.16161,
            -86.16216,-86.15284,-86.15416,-86.15631,-86.15650,-86.15043};
    private String []title = {"Indiana Government Center","Mass Ave. and Alabama",
            "Mass Ave. and Park","Michicgan and Blackford","Michigan and Senate",
            "Monument Circle","North End of Canal","North End of Mass Ave.",
            "North and Alabama","Victory Field","Washington and Illinois",
            "Washington and Meridian","White River State Park", "IUPUI Campus Center",
            "Glick Peace Walk","Fountain Square","Fletcher Place -Virginia and Norwood",
            "Fletcher Place -Virginia and Merrill","Convention Center at Georgia Street",
            "Convention Center - Maryland and Capitol", "City Market",
            "City County Building","Central Library", "Bankers Life Fieldhouse",
            "Athenaeum"};
    */

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

    private class JSONParser extends AsyncTask<Void, Void, Void> {

        private int numStations = 25;
        private String[] lat = new String[numStations];
        private String[] lon = new String[numStations];
        private String[] stationName = new String[numStations];
        private String[] street = new String[numStations];
        private String[] docks = new String[numStations];
        private String[] bikesAv = new String[numStations];
        private String[] miles = new String[numStations];
        private String[] streetNameOnly = new String[numStations];

        //You get Data here
        @Override
        protected Void doInBackground(Void... voids) {
            com.misabelleeli.pacers_bikeshare.JSONParser jp =
                    new com.misabelleeli.pacers_bikeshare.JSONParser();

            JSONArray bk = jp.getJSONFromUrl(url);
            if(bk != null)
            {
                try{
                    int temp = bk.length();

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
                        myLoc.setLatitude(myLat);
                        myLoc.setLongitude(myLong);

                        Location stationLoc = new Location("b");
                        stationLoc.setLatitude(Double.parseDouble(latitude));
                        stationLoc.setLongitude(Double.parseDouble(longitude));

                        float tempMiles = myLoc.distanceTo(stationLoc)* Float.parseFloat("0.000621371");
                        miles[i] = String.format("%.1f", tempMiles);
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
                        Integer.parseInt(bikesAv[i]),Integer.parseInt(docks[i]),Float.parseFloat(miles[i]));

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
        mMap = getMap();

        mMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                MIN_TIME,MIN_DISTANCE,this);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        getData();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //This will redirect it to GoogleMaps
                String url = "http://maps.google.com/maps?daddr="+currentLat+","+currentLong;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

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

            //TODO: get correct titles and descriptions.

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
