package com.dlc.serialportlibrary;

/**
 * Created by Administrator on 2018\5\14 0014.
 */

public interface ReceiveCallback {
    void onReceive(String devicePath,String baudrateString,byte[] received, int size);
}
