package com.dlc.serialportutildemo;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        ActionInWhiteList.getInstance().init(this);
//        AutoLaunchManager.startAutoLaunch(this);
        Toast.makeText(this, "ActionInWhiteList.getInstance().init", Toast.LENGTH_SHORT).show();
        startTimer();
    }

    private void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 2000, 2000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getApplicationContext(), "Action!", Toast.LENGTH_SHORT).show();
        }
    };
}
