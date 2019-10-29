package com.dlc.serialportutildemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dlc.serialportlibrary.DLCSerialPortUtil;
import com.dlc.serialportlibrary.SerialPortManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DLCSerialPortUtil.getInstance().getAllDevicesPath();
        SerialPortManager manager = DLCSerialPortUtil.getInstance().open("/dev/ttyS0", "115200");
        Log.e("SerialPortManager","manager:"+manager.isOpenSuccess());
    }
}
