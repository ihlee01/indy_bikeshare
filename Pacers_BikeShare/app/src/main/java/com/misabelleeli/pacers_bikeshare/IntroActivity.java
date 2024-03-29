package com.misabelleeli.pacers_bikeshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class IntroActivity extends Activity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full page logo screen - remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);

        // Delay for 1 sec
        handler = new Handler();
        handler.postDelayed(irun, 1200);
    }

    Runnable irun = new Runnable() {
        public void run() {
            Intent i = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(i);
            finish();

            // Screen fade in & out effect
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(irun);
    }
}
