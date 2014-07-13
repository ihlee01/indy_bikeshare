package com.misabelleeli.pacers_bikeshare;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TimerFragment extends Fragment {

    private Button startButton;
    private Button resetButton;
    private Button vibrateOff;

    private TextView timerValue;
    private long startTime = 1800000; //milliseconds
    private String hms = "";
    private long delimiter = 28;
    private CounterClass timer = new CounterClass(startTime,1000);
    private NotificationCompat.Builder mBuilder;
    private NotificationManager nManager;
    private int requestID = 001;

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

        //listener handler
        View.OnClickListener handler = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotification();
            }
        };

        timerValue = (TextView) getView().findViewById(R.id.timerValue);
        startButton = (Button) getView().findViewById(R.id.startbutton);
        startButton.setOnClickListener(handler);


        resetButton = (Button) getView().findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                delimiter = 28;
                hms = "00:30:00";
                mBuilder.setOngoing(false);
                nManager.cancelAll();
                timerValue.setText(hms);
            }
        });

        vibrateOff = (Button) getView().findViewById(R.id.vibrate_btn);
        vibrateOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delimiter = 0;
                mBuilder.setVibrate(new long[]{0});
            }
        });
    }

    private void showNotification(){
        timer.start();

        String action_snooze = "com.misabelleeli.pacers_bikeshare.ACTION_SNOOZE";
        String action_dimiss = "com.misabelleeli.pacers_bikeshare.ACTION_DISMISS";

        mBuilder = new NotificationCompat.Builder(
                getActivity()).setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Pacers Bike Share Timer")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(hms));
        mBuilder.setAutoCancel(true).setPriority(2);

        Intent snoozeIntent = new Intent(getActivity(), MainActivity.class);
        snoozeIntent.setAction(action_snooze);
        PendingIntent snoozePIntent = PendingIntent.getService(getActivity(),0,snoozeIntent,0);

        Intent dismissIntent = new Intent(getActivity(), MainActivity.class);
        dismissIntent.setAction(action_dimiss);
        PendingIntent dismissPIntent = PendingIntent.getService(getActivity(),0,dismissIntent,0);


        mBuilder.addAction(R.drawable.ic_launcher, "Snooze", snoozePIntent)
                .addAction(R.drawable.ic_launcher, "Dismiss", dismissPIntent);

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

        if(dismissIntent.getAction().equals(action_dimiss))
        {
            mBuilder.setOngoing(false);
            mBuilder.setVibrate(new long[]{0});
            nManager.cancelAll();
        }
        else if(snoozeIntent.equals(action_snooze))
        {
            mBuilder.setOngoing(false);
            nManager.cancel(requestID);
        }


    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long milliseconds, long countDownInterval)
        {
            super(milliseconds,countDownInterval);
        }

        @Override
        public void onTick(long l) {
            long millisec = l;
            long temp = TimeUnit.MILLISECONDS.toMinutes(millisec) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisec));
            hms = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisec),
                    TimeUnit.MILLISECONDS.toMinutes(millisec) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisec)),
                    TimeUnit.MILLISECONDS.toSeconds(millisec) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisec)));
            timerValue.setText(hms);
            mBuilder.setContentText("Time: " + hms);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Time: " + hms));
            // mId allows you to update the notification later on.
            nManager.notify(0, mBuilder.build());
            String t = String.format("%02d",delimiter);

            if(delimiter == temp)
            {

                // Make the notification play the default notification sound:
                /*
                Uri alarmSound = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(alarmSound);
                */
                mBuilder.setVibrate(new long[]{500,500,500});
                mBuilder.setOngoing(true);

            }
        }

        @Override
        public void onFinish() {
        }
    }
}
