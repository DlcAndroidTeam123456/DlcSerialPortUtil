package com.dlc.serialportlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;


/**
 * 读串口线程
 */
public class SerialReadThread extends Thread {
    private static final String TAG = "SerialReadThread";
//    private Context context;
    private BufferedInputStream mInputStream;
    private String devicePath, baudrateString;
    private ReceiveCallback receiveCallback;

    public SerialReadThread(String devicePath, String baudrateString, InputStream is) {
//        this.context = context;
        this.devicePath = devicePath;
        this.baudrateString = baudrateString;
        mInputStream = new BufferedInputStream(is);
    }

    public void setReceiveCallback(ReceiveCallback receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    public void removeReceiveCallback(){
        receiveCallback = null;
    }

    @Override
    public void run() {
        byte[] received = new byte[1024];
        int size;

//        LogPlus.e("开始读线程");

        while (true) {

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {

                int available = mInputStream.available();

                if (available > 0) {
                    size = mInputStream.read(received);
                    if (size > 0) {
                        onDataReceive(received, size);
                    }
                } else {
                    // 暂停一点时间，免得一直循环造成CPU占用率过高
                    SystemClock.sleep(1);
                }
            } catch (IOException e) {
//                LogPlus.e("读取数据失败", e);
            }
            //Thread.yield();
        }

//        LogPlus.e("结束读进程");
    }

    /**
     * 处理获取到的数据
     *
     * @param received
     * @param size
     */
    private void onDataReceive(byte[] received, int size) {
        // TODO: 2018/3/22 解决粘包、分包等
//        String hexStr = ByteUtil.bytes2HexStr(received, 0, size);
//        LogManager.instance().post(new RecvMessage(hexStr));
//        sendCommand(COMMAND_RECEIVE_DATA, received, size);
        if (receiveCallback!=null){
            receiveCallback.onReceive(devicePath,baudrateString,received,size);
        }
    }

    /**
     * 停止读线程
     */
    public void close() {

        try {
            mInputStream.close();
        } catch (IOException e) {
//            LogPlus.e("异常", e);
        } finally {
            super.interrupt();
        }
    }

//    private void sendCommand(int command, byte[] received, int size) {
//        Intent intent = new Intent();
//        intent.setAction(COM_DLC_SERIAL_PORT_LIBRARY_SERIAL_BD);
//        intent.putExtra("received", received);
//        intent.putExtra("size", size);
//        intent.putExtra("devicePath", devicePath);
//        intent.putExtra("baudrateString", baudrateString);
//        context.sendBroadcast(intent);
//    }
}
