package com.misabelleeli.pacers_bikeshare;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class GoogleMapFragment extends SupportMapFragment {

    private GoogleMap mMap;

    public GoogleMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mLocationClient = new LocationClient(this,this,this);

        mMap = getMap();

        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();
	        /*if(mLocationClient.getLastLocation() == null) {
	            Log.e("LocationClient", "Last location is null!");
	            mLocationClient.disconnect();
	            return;
	        }*/
        cameraPositionBuilder.target(new LatLng(39.773914,-86.157555));
        cameraPositionBuilder.zoom((float) 13);
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPositionBuilder.build()));

        double []latitudes = {39.76737,39.77224,39.77643,39.77475,39.77418,39.76885,
                39.78179,39.77964,39.77564,39.76595,39.76702,39.76720,39.76832,
                39.77338,39.77669,39.75241,39.75740,39.75893,39.76423,39.76593,
                39.76866,39.76722,39.77803,39.76481,39.77383};
        double []longtitudes={-86.16474,-86.15260,-86.14727,-86.16984,-86.16348,
                -86.15736,-86.16590, -86.14212,-86.15217,-86.16689,-86.16016,-86.15832,
                -86.17027,
                -86.17543,-86.16119,-86.13995,-86.14549,-86.14700,-86.16161,
                -86.16216,-86.15284,-86.15416,-86.15631,-86.15650,-86.15043};
        String []title = {"Indiana Government Center","Mass Ave. and Alabama",
                "Mass Ave. and Park","Michicgan and Blackford","Michigan and Senate",
                "Monument Circle","North End of Canal","North End of Mass Ave.",
                "North and Alabama","Victory Field","Washington and Illinois",
                "Washington and Meridian","White River State Park", "IUPUI Campus Center",
                "Glick Peace Walk","Fountain Square","Fletcher Place -Virginia and Norwood",
                "Fletcher Place -Virginia and Merrill","Convention Center at Georgia Street",
                "Convention Center - Maryland and Capitol", "City Market",
                "City County Building","Central Library", "Bankers Life Fieldhouse",
                "Athenaeum"};
        String []address ={};

        /*Once there is a database access implement the following:
            1. If docks are full, have marker color change.
            2. If dock is about to be full, have another maker color change.
            3. Add a better infobox.
        */
        for(int i = 0; i < title.length;i++)
        {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitudes[i], longtitudes[i]))
                    .title(title[i]))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
                    //.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        }
        mMap.setMyLocationEnabled(true);
    }
}
