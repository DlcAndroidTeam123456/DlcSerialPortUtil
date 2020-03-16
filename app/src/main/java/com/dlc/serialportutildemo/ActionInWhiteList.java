package com.dlc.serialportutildemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/**
 * 本类 操作来源于 com.android.server.am.AmigoSystemManagerAppBootManager
 * 主要是取消系统中action不能唤醒应用的限制。使得push的action可以拉起各个应用
 */

public class ActionInWhiteList {
    private static final String TAG = "ActionInWhiteList";

    private static final String WHITE_LIST_USE_TYPE_AUTO_BOOT_ALLOW = "autobootallow";
    private static final String WHITE_LIST_USE_TYPE_ALLOW_BOOT = "allowboot";

    private static final String WHITE_LIST_STATUE_ENABLE = "1";

    private Uri mRosterProviderUri = Uri.parse("content://com.amigo.settings.RosterProvider/rosters");

    private static ActionInWhiteList mActionInWhiteList;

    public static ActionInWhiteList getInstance() {
        if(null == mActionInWhiteList) {
            synchronized (ActionInWhiteList.class) {
                if(null == mActionInWhiteList) {
                    mActionInWhiteList = new ActionInWhiteList();
                }
            }
        }
        return mActionInWhiteList;
    }

    public void init(Context context) {
        try {
            boolean hasDbUpdated = addProviderToDb(context);
            sendBroadcast(context);
            if (hasDbUpdated) {
            }
        } catch (Exception e) {

        }
    }

    private void sendBroadcast(Context context) {
        Intent intent = new Intent("com.gionee.intent.action.UPDATE_ALLOW_BOOT_APP_LIST");
        context.sendBroadcast(intent);
    }

    private boolean addProviderToDb(Context context) {
        boolean addAutoBootAllowResult = addProviderToDb(context, WHITE_LIST_USE_TYPE_AUTO_BOOT_ALLOW);
        boolean addAllowBootResult = addProviderToDb(context, WHITE_LIST_USE_TYPE_ALLOW_BOOT);
        return addAutoBootAllowResult || addAllowBootResult;
    }

    private boolean addProviderToDb(Context context, String whiteListType) {
        int queryCount = queryProviderWhiteListCount(context, whiteListType);
        if (queryCount == HAS_DATA_STATUE_ENABLE_COUNT) {
            return false;
        } else if (queryCount == NO_DATA_COUNT) {
            insertProviderToRoster(context, whiteListType);
            return true;
        } else {
            updateProviderStatus(context, whiteListType);
            return true;
        }
    }

    private void updateProviderStatus(Context context, String whiteListType) {
        ContentValues updateValues = new ContentValues();
        updateValues.put("status", WHITE_LIST_STATUE_ENABLE);
        int updateCount = context.getContentResolver().update(mRosterProviderUri, updateValues, "packagename = ? AND usertype = ?", new String[]{context.getPackageName(), whiteListType});
    }

    private void insertProviderToRoster(Context context, String whiteListType) {
        ContentValues insertValues = new ContentValues();
        insertValues.put("usertype", whiteListType);
        insertValues.put("packagename", context.getPackageName());
        insertValues.put("status", WHITE_LIST_STATUE_ENABLE);
        context.getContentResolver().insert(mRosterProviderUri, insertValues);
    }

    private static final int HAS_DATA_STATUE_ENABLE_COUNT = 1;
    private static final int HAS_DATA_STATUE_DISABLE_COUNT = -1;
    private static final int NO_DATA_COUNT = 0;

    private int queryProviderWhiteListCount(Context context, String whiteListType) {
        int weatherProviderWhiteListCount = HAS_DATA_STATUE_DISABLE_COUNT;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(mRosterProviderUri, null, "packagename = ? AND usertype = ? ", new String[]{context.getPackageName(), whiteListType}, null);
            if (cursor == null) {
                weatherProviderWhiteListCount = NO_DATA_COUNT;
                return weatherProviderWhiteListCount;
            }
            int cursorCount = cursor.getCount();
            if (cursorCount == 0) {
                weatherProviderWhiteListCount = NO_DATA_COUNT;
            } else {
                cursor.moveToFirst();
                String providerStatus = "";
                int statusColumnIndex = cursor.getColumnIndex("status");
                do {
                    providerStatus = cursor.getString(statusColumnIndex);
                    if (WHITE_LIST_STATUE_ENABLE.equals(providerStatus)) {
                        weatherProviderWhiteListCount = HAS_DATA_STATUE_ENABLE_COUNT;
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            weatherProviderWhiteListCount = NO_DATA_COUNT;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weatherProviderWhiteListCount;
    }
}