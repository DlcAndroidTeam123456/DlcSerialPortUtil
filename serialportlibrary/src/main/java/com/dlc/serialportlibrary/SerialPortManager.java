package com.dlc.serialportlibrary;

import android.content.Context;
import android.database.Observable;
import android.os.HandlerThread;
import android.serialport.SerialPort;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.LogManager;


/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class SerialPortManager {
    //    private Context context;
    private SerialReadThread mReadThread;
    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;
    private SerialPort mSerialPort;
    private String devicePath, baudrateString;
    private boolean isOpenSuccess;

//    public SerialPortManager(Context context) {
//        this.context = context;
//    }

    /**
     * 打开串口
     *
     * @param device
     * @return
     */
    public SerialPort open(Device device) {
        return open(device.getPath(), device.getBaudrate());
    }

    /**
     * 打开串口
     *
     * @param devicePath
     * @param baudrateString
     * @return
     */
    public SerialPort open(String devicePath, String baudrateString) {
        return open(devicePath, baudrateString, 0, 8, 1);
    }

    /**
     * 打开串口
     *
     * @param devicePath
     * @param baudrateString
     * @param parity         校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
     * @param dataBits       数据位,默认8；可选值为5~8
     * @param stopBits       停止位，默认1；1:1位停止位；2:2位停止位
     * @return
     */
    public SerialPort open(String devicePath, String baudrateString, int parity, int dataBits, int stopBits) {
        this.devicePath = devicePath;
        this.baudrateString = baudrateString;
        if (mSerialPort != null) {
            close();
        }

        try {
            mSerialPort = SerialPort.newBuilder(devicePath, Integer.parseInt(baudrateString)).
                    parity(parity).dataBits(dataBits).stopBits(stopBits).build();

            mReadThread = new SerialReadThread(devicePath, baudrateString, mSerialPort.getInputStream());
            mReadThread.start();

            mOutputStream = mSerialPort.getOutputStream();

            mWriteThread = new HandlerThread("write-thread");
            mWriteThread.start();
//            mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());
            isOpenSuccess = true;
            return mSerialPort;
        } catch (Throwable tr) {
//            LogPlus.e(TAG, "打开串口失败", tr);
            isOpenSuccess = false;
            close();
            return null;
        }
    }

    public void setReceiveCallback(ReceiveCallback receiveCallback) {
        mReadThread.setReceiveCallback(receiveCallback);
    }

    public void removeReceiveCallback() {
        mReadThread.removeReceiveCallback();
    }

    /**
     * 关闭串口
     */
    public void close() {
        if (mReadThread != null) {
            mReadThread.close();
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mWriteThread != null) {
            mWriteThread.quit();
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    /**
     * 发送数据
     *
     * @param datas
     * @return
     */
    public void sendData(byte[] datas) throws Exception {
        mOutputStream.write(datas);
    }

    public void sendData(String hexString) throws Exception {
        sendData(hexStringToBytes(hexString));
    }

    public String getDevicePath() {
        return devicePath;
    }

    public String getBaudrateString() {
        return baudrateString;
    }

    public boolean isOpenSuccess() {
        return isOpenSuccess;
    }

    /**
     * 十六进制字符串转字节数组
     *
     * @param hex
     * @return
     */
    private byte[] hexStringToBytes(String hex) {
        byte[] buf = new byte[hex.length() / 2];
        int j = 0;
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) ((Character.digit(hex.charAt(j++), 16) << 4) | Character
                    .digit(hex.charAt(j++), 16));
        }
        return buf;
    }
}
