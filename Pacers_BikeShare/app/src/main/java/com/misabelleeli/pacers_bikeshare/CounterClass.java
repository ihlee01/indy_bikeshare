package com.misabelleeli.pacers_bikeshare;

import android.os.CountDownTimer;
import android.view.View;

import java.util.concurrent.TimeUnit;

/**
 * Created by M.Isabel on 7/22/2014.
 */
public class CounterClass extends CountDownTimer {

    TimerCountDown callback;

    public CounterClass(long milliseconds, long countDownInterval, TimerCountDown callback)
    {
        super(milliseconds,countDownInterval);
        this.callback = callback;
    }

    @Override
    public void onTick(long l) {
            /*
            timerValue.setText(hms);
            mBuilder.setContentText("Time: " + hms);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Time: " + hms));
            // mId allows you to update the notification later on.
            nManager.notify(0, mBuilder.build());*/
        long millisec = l;
        long temp = TimeUnit.MILLISECONDS.toMinutes(millisec) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisec));
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisec) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisec)),
                TimeUnit.MILLISECONDS.toSeconds(millisec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisec)));
        callback.updateTime(hms, temp);
    }

    @Override
    public void onFinish() {
    }
}