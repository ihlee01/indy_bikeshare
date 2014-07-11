package com.misabelleeli.pacers_bikeshare;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
    private TextView timerValue;
    private long startTime = 1800000; //milliseconds
    private String hms = "";
    private long delimiter = 28;
    final CounterClass timer = new CounterClass(startTime,1000);
    private NotificationCompat.Builder mBuilder;
    private NotificationManager nManager;

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
                timerValue.setText("00:00:00");
            }
        });
    }

    private void showNotification(){
        timer.start();

        mBuilder = new NotificationCompat.Builder(
                getActivity()).setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Pacers Bike Share Timer");



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

        // Make this unique ID to make sure there is not generated just a brand new intent with new extra values:
        int requestID = (int) System.currentTimeMillis();

        // Pass the unique ID to the resultPendingIntent:
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getActivity(), requestID, resultIntent, 0);

        mBuilder.setContentIntent(resultPendingIntent);
        nManager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);

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
            // mId allows you to update the notification later on.
            nManager.notify(0, mBuilder.build());

            if(delimiter == temp || 1 == temp)
            {
                // Make the notification play the default notification sound:
                Uri alarmSound = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(alarmSound);
                mBuilder.setOngoing(true);
                mBuilder.setAutoCancel(true);
            }
        }

        @Override
        public void onFinish() {
            timerValue.setText("00:00:00");
        }
    }
}
