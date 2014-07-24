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
        long min = TimeUnit.MILLISECONDS.toMinutes(millisec) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisec));
        long sec = TimeUnit.MILLISECONDS.toSeconds(millisec) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisec));
        String hms = String.format("%02d:%02d",min,sec);
        callback.updateTime(hms, min, sec);
    }

    @Override
    public void onFinish() {
    }
}