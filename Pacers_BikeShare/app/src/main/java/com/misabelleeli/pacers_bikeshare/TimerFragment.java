package com.misabelleeli.pacers_bikeshare;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TimerFragment extends Fragment implements TimerCountDown{

    private Button startButton;
    private Button stopButton;
    private ImageButton addButton;

    private TextView timerValue;
    public static long startTime = 1800000; //milliseconds
    private String hms = "";
    private long delimiter = 10;
    public  CounterClass timer;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager nManager;
    private int requestID = 001;
    private boolean vibrate = false;

    public TimerFragment() {
        // Required empty public constructor
        timer = new CounterClass(startTime,1000, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    public void stopTimer()
    {
        timer.cancel();
        stopButton.setVisibility(View.GONE);
        startButton.setText("START");
        startButton.setVisibility(View.VISIBLE);
        delimiter = 10;
        hms = "30:00";
        timerValue.setText(hms);
        mBuilder.setContentText("Time " + hms);
        // mId allows you to update the notification later on.
        nManager.notify(0, mBuilder.build());
        nManager.cancelAll();
        timer = new CounterClass(startTime, 1000,(TimerCountDown)TimerFragment.this);
    }

    public void addTime()
    {
        startButton.setText("START");
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        timer = new CounterClass(startTime*2, 1000,(TimerCountDown)TimerFragment.this);
        delimiter = 10;
        hms = "60:00";
        timerValue.setText(hms);
        mBuilder.setContentText("Time " + hms);
        // mId allows you to update the notification later on.
        nManager.notify(0, mBuilder.build());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RelativeLayout timerLayout = (RelativeLayout) getView().findViewById(R.id.timerLayout);

        //listener handler
        View.OnClickListener start_handler = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!vibrate) {
                    timer.start();
                }
                else
                {
                    startButton.setText("START");
                    vibrate = true;
                    if(delimiter == 5)
                    {
                        delimiter = 1;
                        mBuilder.setVibrate(new long[]{0});
                    }
                    else if (delimiter == 10)
                    {
                        delimiter = 5;
                        mBuilder.setVibrate(new long[]{0});
                    }
                    else if(delimiter == 1)
                    {
                        delimiter = -1;
                        mBuilder.setVibrate(new long[]{0});
                    }
                }
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                showNotification();
            }

        };

        View.OnClickListener stop_handler = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
            }
        };

        timerValue = (TextView) getView().findViewById(R.id.timerValue);

        startButton = (Button) getView().findViewById(R.id.startbutton);
        startButton.setOnClickListener(start_handler);

        stopButton = (Button) getView().findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(stop_handler);

        addButton = (ImageButton) getView().findViewById(R.id.reset);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTime();
            }
        });
    }

    private void showNotification(){

        String action_snooze = "com.misabelleeli.pacers_bikeshare.ACTION_SNOOZE";
        String action_dismiss = "com.misabelleeli.pacers_bikeshare.ACTION_DISMISS";

        mBuilder = new NotificationCompat.Builder(
                getActivity()).setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Pacers Bike Share Timer");
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(hms));
        mBuilder.setAutoCancel(true).setPriority(2);
        mBuilder.setContentText("Time " + hms);

        /*
        Intent snoozeIntent = new Intent(getActivity(), MainActivity.class);
        snoozeIntent.setAction(action_snooze);
        PendingIntent snoozePIntent = PendingIntent.getService(getActivity(),0,snoozeIntent,0);

        Intent dismissIntent = new Intent(getActivity(), MainActivity.class);
        dismissIntent.setAction(action_dismiss);
        PendingIntent dismissPIntent = PendingIntent.getService(getActivity(),0,dismissIntent,0);


        mBuilder.addAction(R.drawable.ic_launcher, "Snooze", snoozePIntent)
                .addAction(R.drawable.ic_launcher, "Dismiss", dismissPIntent);
        */
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getActivity(), MainActivity.class);

        // This somehow makes sure, there is only 1 CountDownTimer going if the notification is pressed:
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Pass the unique ID to the resultPendingIntent:
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getActivity(), requestID, resultIntent, 0);

        mBuilder.setContentIntent(resultPendingIntent);
        nManager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(0,mBuilder.getNotification());

        /*
        Log.d("DismissIntent", dismissIntent.getAction());
        Log.d("SnoozeIntent",snoozeIntent.getAction());
        Log.d("SnoozeIntent",action_snooze);

        if(dismissIntent.getAction().equals(action_dismiss))
        {
            Log.d("test","inside2");
            mBuilder.setOngoing(true);
            mBuilder.setVibrate(new long[]{0});
            nManager.cancelAll();
        }

        if(snoozeIntent.equals(action_snooze))
        {
            Log.d("test","inside1");
            mBuilder.setOngoing(true);
            nManager.cancel(requestID);
        }
        */

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void updateTime(String time, long temp) {
        timerValue.setText(time);
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        mBuilder.setContentText("Time: " + time);
//        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Time: " + time));
        // mId allows you to update the notification later on.
        nManager.notify(0, mBuilder.build());

        if(delimiter == temp)
        {
            mBuilder.setVibrate(new long[]{500,500,500});
            mBuilder.setOngoing(true);
            vibrate = true;
            stopButton.setVisibility(View.GONE);
            startButton.setVisibility(View.VISIBLE);
            startButton.setText("SNOOZE");
        }
    }
}
