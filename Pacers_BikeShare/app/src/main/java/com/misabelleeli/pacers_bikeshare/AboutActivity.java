package com.misabelleeli.pacers_bikeshare;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Example);
        setContentView(R.layout.activity_about);
        TextView checkout_content = (TextView)findViewById(R.id.checkout_content);
        TextView checkout_content2 = (TextView)findViewById(R.id.checkout_content2);
        TextView checkout_content3 = (TextView)findViewById(R.id.checkout_content3);
        TextView return_content = (TextView)findViewById(R.id.return_content);
        TextView return_content2 = (TextView)findViewById(R.id.return_content2);
        TextView map_link = (TextView)findViewById(R.id.map_link);
        TextView api_content = (TextView)findViewById(R.id.api_content);

        checkout_content.setText(Html.fromHtml(getResources().getString(R.string.checkout_content)));
        checkout_content2.setText(Html.fromHtml(getResources().getString(R.string.checkout_content2)));
        checkout_content3.setText(Html.fromHtml(getResources().getString(R.string.checkout_content3)));
        return_content.setText(Html.fromHtml(getResources().getString(R.string.return_content)));
        return_content2.setText(Html.fromHtml(getResources().getString(R.string.return_content2)));
        map_link.setText(Html.fromHtml(getResources().getString(R.string.map_link)));
        map_link.setMovementMethod(LinkMovementMethod.getInstance());
        api_content.setText(Html.fromHtml(getResources().getString(R.string.api_content)));
        api_content.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
