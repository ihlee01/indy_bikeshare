package com.misabelleeli.pacers_bikeshare;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RemoteViews;
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
/*
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread,0);
            }
        });
*/
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

    private void showNotification(){
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread,0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getActivity()).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Rescue Me ALARM")
                .setContentText("Press here to cancel the SOS SMS");

        // Make the notification play the default notification sound:
        Uri alarmSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(true);
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
        NotificationManager mNotificationManager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
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
                    + String.format("%02d",milliseconds));

            if(secs == 20)
            {
                //Intent intentVibrate =new Intent(getApplicationContext(),VibrateService.class);
                //startService(intentVibrate);
            }
            customHandler.postDelayed(this,0);

        }
    };
}
