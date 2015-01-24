package com.misabelleeli.pacers_bikeshare;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener{

    public ActionBar actionBar;
    public ViewPager viewPager;
    public TabsPagerAdapter mAdapter;
    private SharedPreferences mPrefs;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Example);

        setContentView(R.layout.activity_main);

        if (!isOnline(getApplicationContext())) {
            //Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
            buildAlertMessageNoInternet();
            //MainActivity.this.finish();
            //exitApp();
        }
        else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getApplicationContext(), "GPS is currently OFF", Toast.LENGTH_LONG).show();
            }

            mPrefs = this.getSharedPreferences("favorite", MODE_PRIVATE);
            SharedPreferences.Editor editor = mPrefs.edit();
            if (mPrefs.getString("favorites", null) == null) {
                editor.putString("favorites", "initial");
                editor.commit();
            }


            viewPager = (ViewPager) findViewById(R.id.pager);
            mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
            actionBar = getActionBar();
            viewPager.setAdapter(mAdapter);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.Tab stationTab = actionBar.newTab();
            stationTab.setText("Stations");
            stationTab.setTabListener(this);

            ActionBar.Tab mapTab = actionBar.newTab();
            mapTab.setText("Map");
            mapTab.setTabListener(this);

            ActionBar.Tab timerTab = actionBar.newTab();
            timerTab.setText("Timer");
            timerTab.setTabListener(this);

            ActionBar.Tab aboutTab = actionBar.newTab();
            aboutTab.setText("About");
            aboutTab.setTabListener(this);

            actionBar.addTab(mapTab);
            actionBar.addTab(timerTab);
            actionBar.addTab(stationTab);

            // on swiping the viewpager make respective tab selected
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    // on changing the page
                    // make respected tab selected
                    actionBar.setSelectedNavigationItem(position);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
        }
    }


    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            exitApp();
        }
        else{
            Toast.makeText(getBaseContext(), "Press again to exit", Toast.LENGTH_LONG).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    private void exitApp() {
        if(TimerFragment.timer != null) {
            TimerFragment.timer.cancel();
        }
        if(TimerFragment.nManager != null) {
            TimerFragment.nManager.cancelAll();
        }
        MainActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_settings).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
        View focus = getCurrentFocus();
        if (focus != null) {
            hiddenKeyboard(focus);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        View focus = getCurrentFocus();
        if (focus != null) {
            hiddenKeyboard(focus);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        View focus = getCurrentFocus();
        if (focus != null) {
            hiddenKeyboard(focus);
        }
    }
    private void hiddenKeyboard(View v) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

    private void buildAlertMessageNoInternet() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("No Network Connection.\nPlease connect " +
                "to Wifi or Enable Network to continue.");
        b.setTitle("Internet Connection");
        b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exitApp();
            }
        });

        final AlertDialog alert = b.create();
        alert.show();
    }


}
