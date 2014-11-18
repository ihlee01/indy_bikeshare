package com.misabelleeli.pacers_bikeshare;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TimerFragment extends Fragment implements TimerCountDown{

    private Button startButton;
    private Button stopButton;
    private ImageButton addButton;
    private ImageButton minusButton;

    private TextView timerValue;
    public static long startTime = 1800000; //milliseconds
    public long curr_Time = 1800000;
    private String hms = "";
    private String current_Hms = "30:00";
    private long timeout = 5;

    public  static CounterClass timer;
    private NotificationCompat.Builder mBuilder;
    public static NotificationManager nManager;
    private int requestID = 001;
    private boolean vibrate = false;

    private boolean isEnabled = false;

    public TimerFragment() {
        // Required empty public constructor
        timer = new CounterClass(startTime, 1000, this);
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
        startButton.setVisibility(View.VISIBLE);
        isEnabled = true;
        timeout = 5;
        hms = "30:00";
        startTime = 1800000;
        curr_Time = startTime;
        current_Hms = hms;
        timerValue.setText(hms);
        mBuilder.setContentText("Time " + hms);

        // mId allows you to update the notification later on.
        nManager.notify(0, mBuilder.build());
        nManager.cancelAll();
        timer = new CounterClass(curr_Time, 1000,(TimerCountDown)TimerFragment.this);
    }

    public void addTime()
    {
        timer.cancel();
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        timeout = 5;

        if(current_Hms.equals("30:00"))
        {
            startTime = curr_Time +300000;
            curr_Time = startTime;
            current_Hms = "35:00";
        }
        else if(current_Hms.equals("35:00"))
        {
            startTime = curr_Time +300000;
            curr_Time = startTime;
            current_Hms = "40:00";
        }
        else if(current_Hms.equals("40:00"))
        {
            startTime = curr_Time +300000;
            curr_Time = startTime;
            current_Hms = "45:00";
        }
        else if(current_Hms.equals("45:00"))
        {
            startTime = curr_Time +300000;
            curr_Time = startTime;
            current_Hms = "50:00";
        }
        else if(current_Hms.equals("50:00"))
        {
            startTime = curr_Time +300000;
            curr_Time = startTime;
            current_Hms = "55:00";
        }
        else if(current_Hms.equals("55:00"))
        {
            startTime = curr_Time +300000;
            curr_Time = startTime;
            current_Hms = "60:00";
        }
        else if(current_Hms.equals("25:00"))
        {
            startTime = curr_Time +300000;
            curr_Time = startTime;
            current_Hms = "30:00";
        }
        else if(current_Hms.equals("20:00"))
        {
            startTime = curr_Time +300000;
            curr_Time = startTime;
            current_Hms = "25:00";
        }
        else
        {
            curr_Time = 1800000;
            startTime = curr_Time *2;
            current_Hms = "60:00";
        }

        hms = current_Hms;
        timer = new CounterClass(startTime, 1000, (TimerCountDown) TimerFragment.this);
        timerValue.setText(hms);

        if(mBuilder != null)
        {
            mBuilder.setContentText("Time " + hms);
            nManager.notify(0, mBuilder.build());
        }
    }
    public void minusTime()
    {
        timer.cancel();
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);

        if(current_Hms.equals("30:00"))
        {
            startTime = curr_Time -300000;
            curr_Time = startTime;
            current_Hms = "25:00";
        }
        else if(current_Hms.equals("25:00"))
        {
            startTime = curr_Time -300000;
            curr_Time = startTime;
            current_Hms = "20:00";
        }
        else if(current_Hms.equals("35:00"))
        {
            startTime = curr_Time -300000;
            curr_Time = startTime;
            current_Hms = "30:00";
        }
        else if(current_Hms.equals("40:00"))
        {
            startTime = curr_Time -300000;
            curr_Time = startTime;
            current_Hms = "35:00";
        }
        else if(current_Hms.equals("45:00"))
        {
            startTime = curr_Time -300000;
            curr_Time = startTime;
            current_Hms = "40:00";
        }
        else if(current_Hms.equals("50:00"))
        {
            startTime = curr_Time -300000;
            curr_Time = startTime;
            current_Hms = "45:00";
        }
        else if(current_Hms.equals("55:00"))
        {
            startTime = curr_Time -300000;
            curr_Time = startTime;
            current_Hms = "50:00";
        }
        else if(current_Hms.equals("60:00"))
        {
            startTime = curr_Time -300000;
            curr_Time = startTime;
            current_Hms = "55:00";
        }
        else
        {
            curr_Time = 1200000;
            startTime = curr_Time;
            current_Hms = "20:00";
        }

        timer = new CounterClass(startTime, 1000,(TimerCountDown)TimerFragment.this);
        timeout = 5;
        hms = current_Hms;
        timerValue.setText(hms);

        if(mBuilder != null)
        {
            mBuilder.setContentText("Time " + hms);
            nManager.notify(0, mBuilder.build());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RelativeLayout timerLayout = (RelativeLayout) getView().findViewById(R.id.timerLayout);

        timerValue = (TextView) getView().findViewById(R.id.timerValue);

        startButton = (Button) getView().findViewById(R.id.startbutton);
        startButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.setBackgroundColor(view.getResources().getColor(R.color.green_pressed));
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.setBackgroundColor(view.getResources().getColor(R.color.start_green));
                        if (!vibrate) {
                            timer.start();
                            isEnabled = false;
                        } else {
                            startButton.setText("START");
                            vibrate = true;
                            if (timeout == 5) {
                                timeout = -1;
                                mBuilder.setVibrate(new long[]{0});
                            }
                        }
                        startButton.setVisibility(View.GONE);
                        stopButton.setVisibility(View.VISIBLE);
                        showNotification();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        stopButton = (Button) getView().findViewById(R.id.stopbutton);
        stopButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.setBackgroundColor(view.getResources().getColor(R.color.red_pressed));
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.setBackgroundColor(view.getResources().getColor(R.color.stop_red));
                        stopTimer();
                        stopButton.setVisibility(View.GONE);
                        startButton.setVisibility(View.VISIBLE);
                        view.invalidate();

                        break;
                    }
                }
                return false;
            }
        });

        addButton = (ImageButton) getView().findViewById(R.id.plus_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEnabled) {
                    Toast.makeText(getActivity(), "Please, stop timer to change the time.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    addTime();
                }
            }
        });
        minusButton = (ImageButton) getView().findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEnabled) {
                    Toast.makeText(getActivity(), "Please, stop timer to change the time.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    minusTime();
                }
            }
        });

    }

    private void showNotification(){

        String action_snooze = "com.misabelleeli.pacers_bikeshare.ACTION_SNOOZE";
        String action_dismiss = "com.misabelleeli.pacers_bikeshare.ACTION_DISMISS";

        mBuilder = new NotificationCompat.Builder(
                getActivity()).setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Pacers Bikeshare Timer");
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
    public void updateTime(String time, long min, long sec) {
        timerValue.setText(time);
        mBuilder.setContentText("Time: " + time);
//        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Time: " + time));
        // mId allows you to update the notification later on.
        nManager.notify(0, mBuilder.build());

        if(timeout == min)
        {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setOngoing(true);
            mBuilder.setVibrate(new long[]{500,500,500});
            mBuilder.setOngoing(true);
            vibrate = true;
            stopButton.setVisibility(View.GONE);
            startButton.setVisibility(View.VISIBLE);
            startButton.setText("OK");
        }
    }
}
