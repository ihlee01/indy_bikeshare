package com.misabelleeli.pacers_bikeshare;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TimerFragment extends Fragment {

    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private TextView timerValue;
    private long startTime = 0;
    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0;
    long timeSwapBuff = 0;
    long updatedTime = 0;
    long five_minutes = 500; //300000;

    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timerValue = (TextView) getView().findViewById(R.id.timerValue);
        startButton = (Button) getView().findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread,0);
            }
        });

        pauseButton = (Button) getView().findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
            }
        });

        resetButton = (Button) getView().findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customHandler.removeCallbacks(updateTimerThread);
                timeInMilliseconds = 0;
                timeSwapBuff = 0;
                updatedTime = 0;
                timerValue.setText("00:00:00");
            }
        });

    }

    private Runnable updateTimerThread = new Runnable(){
        public void run()
        {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int)(updatedTime/1000);
            int mins = secs/60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime%1000);
            timerValue.setText(""+mins+":"+String.format("%02d",secs)+":"
                    + String.format("%03d",milliseconds));

            if(secs == 20)
            {
                //Intent intentVibrate =new Intent(getApplicationContext(),VibrateService.class);
                //startService(intentVibrate);
            }
            customHandler.postDelayed(this,0);

        }
    };
}
