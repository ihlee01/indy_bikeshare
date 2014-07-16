package com.misabelleeli.pacers_bikeshare;



import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class StationFragment extends Fragment {
    private List<Station> stations = new ArrayList<Station>();
    private Map<String, Station> stationMap = new HashMap<String, Station>();
    private ArrayList<String> searchList = new ArrayList<String>();
    private ArrayAdapter<Station> adapter;
    public StationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(stations.size() == 0)
            populateStations();

        View rootView = inflater.inflate(R.layout.fragment_station, container, false);


        final ListView myListView = (ListView) rootView.findViewById(R.id.stationsListview);

        final AutoCompleteTextView searchView = (AutoCompleteTextView) rootView.findViewById(R.id.search_view);
        AutoCompleteAdapter autoAdapter = new AutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, searchList);
        searchView.setAdapter(autoAdapter);

        //When user clear the search box -> Refresh listview
        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String input = searchView.getText() + "";
                if(input.equals(""))
                {
                    generateList(myListView);
                }
                return false;
            }
        });

        //Search implementation
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    //Hide Keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    String input = searchView.getText()+"";
                    if(stationMap.containsKey(input))
                    {
                        //Search Match
                        List<Station> result_array = new ArrayList<Station>();
                        result_array.add(stationMap.get(input));
                        Log.w("",stationMap.get(input).getAddress());
                        adapter = new MyListAdapter(result_array);
                        myListView.setAdapter(adapter);
                        return true;
                    }
                    else if(input.equals(""))
                    {
                        generateList(myListView);
                    }
                    else {
                        Toast.makeText(getActivity(), "No Search Result", Toast.LENGTH_SHORT).show();
                        myListView.setAdapter(null);
                    }
                    handled = true;
                }

                return handled;
            }
        });

        generateList(myListView);

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
        for(int i = 0; i < stations.size(); i++) {
            searchList.add(stations.get(i).getAddress());
            stationMap.put(stations.get(i).getAddress(), stations.get(i));
        }
    }
    private void generateList(ListView view) {
        adapter = new MyListAdapter(stations);
        view.setAdapter(adapter);
    }




    private class MyListAdapter extends ArrayAdapter<Station> {
        List<Station> list;
        public MyListAdapter(List<Station> list) {
            super(getActivity(), R.layout.station_view, list);
            this.list = list;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.station_view, parent, false);
            }
            Station curStation = list.get(position);
            TextView address_view = (TextView)rowView.findViewById(R.id.address_view);
            TextView status_view = (TextView)rowView.findViewById(R.id.status_view);
            TextView distance_view = (TextView)rowView.findViewById(R.id.distance_view);
            final ImageButton favorite_button = (ImageButton)rowView.findViewById(R.id.favorite_button);
            ImageView icon_view = (ImageView)rowView.findViewById(R.id.bike_icon);

            address_view.setText(curStation.getAddress());

            String status = "Bikes: <b><font color=\"#0075B0\">" + curStation.getBikes() + "</font></b> | Docks: <b><font color=\"#E05206\">" + curStation.getDocks()+"</font></b>";
            status_view.setText(Html.fromHtml(status));

            //Icon setting
            float totalBike = curStation.getBikes() + curStation.getDocks();
            double bike_ratio =Math.floor(curStation.getBikes()/totalBike*100);
            if(bike_ratio == 0) {
                icon_view.setImageResource(R.drawable.bike_icon_empty);
            }
            else if(bike_ratio < 33.3) {
                icon_view.setImageResource(R.drawable.bike_icon_green_1);
            }
            else if(bike_ratio < 66.6) {
                icon_view.setImageResource(R.drawable.bike_icon_green_2);
            }
            else {
                icon_view.setImageResource(R.drawable.bike_icon_full);
            }

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



                    } else {
                        curStation.setFavorite(true);
                        favorite_button.setBackgroundResource(R.drawable.favorite);
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
