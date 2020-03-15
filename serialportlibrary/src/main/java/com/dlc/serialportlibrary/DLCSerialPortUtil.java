package com.dlc.serialportlibrary;

import android.content.Context;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;


/**
 * Created by Administrator on 2018\5\14 0014.
 */

public class DLCSerialPortUtil {
    private static DLCSerialPortUtil instance;
    private String[] baudrates = new String[]{"110", "300", "600", "1200", "2400",
            "4800", "9600", "14400", "19200", "38400", "56000", "57600", "115200", "128000", "256000"};

    public static DLCSerialPortUtil getInstance() {
        if (instance == null) {
            instance = new DLCSerialPortUtil();
        }
        return instance;
    }

    public String[] getAllDevicesPath() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        return serialPortFinder.getAllDevicesPath();
    }

    public String[] getBaudrates() {
        return baudrates;
    }

    public SerialPortManager open(String devicePath, String baudrateString) {
        SerialPortManager portManager = new SerialPortManager();
        SerialPort port = portManager.open(devicePath, baudrateString);
        if (port != null) {
            return portManager;
        } else {
            return null;
        }
    }

//    public void close(SerialPortManager portManager){
//        portManager.close();
//    }
//
//    public void sendData(SerialPortManager portManager,byte[] datas) throws Exception {
//        portManager.sendData(datas);
//    }
//
//    public void setReceiveCallback(SerialPortManager portManager,ReceiveCallback receiveCallback) {
//        portManager.setReceiveCallback(receiveCallback);
//    }
//
//    public void removeReceiveCallback(SerialPortManager portManager) {
//        portManager.removeReceiveCallback();
//    }
}
