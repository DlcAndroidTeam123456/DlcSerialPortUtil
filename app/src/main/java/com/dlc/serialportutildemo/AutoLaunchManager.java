package com.dlc.serialportutildemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cy on 19-7-13.
 */

public class AutoLaunchManager {

    private static final String TAG = AutoLaunchManager.class.getSimpleName();

    private static final String TRUST_URI = "content://com.amigo.settings.PermissionProvider/whitelist";
    private static final String URI = "content://com.amigo.settings.PermissionProvider/permissions";

    public static void startAutoLaunch(Context context) {
//        try {
//            updatePermission(context, context.getPackageName());
//            updateTrust(context, context.getPackageName());
//            DebugLog.d(TAG, "通过Settings代码授予权限");
//        } catch (Throwable e) {
//        }
//
//        grantRuntimePermission(context, context.getPackageName(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        grantRuntimePermission(context, context.getPackageName(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
//        grantRuntimePermission(context, context.getPackageName(), Manifest.permission.READ_PHONE_STATE);
//        grantRuntimePermission(context, context.getPackageName(), Manifest.permission.ACCESS_WIFI_STATE);
////        grantRuntimePermission(context, context.getPackageName(), Manifest.permission.ACCESS_COARSE_LOCATION);
////        grantRuntimePermission(context, context.getPackageName(), Manifest.permission.ACCESS_FINE_LOCATION);
//
//        ActionInWhiteList.getInstance().init(context);


        addOneCleanWhiteApp(context.getPackageName(), context);
    }


    public static boolean updateTrust(Context context, String pkgName) {
        ContentValues cv = new ContentValues();
        cv.put("status", 1);
        if (context.getContentResolver().update(Uri.parse(TRUST_URI), cv, "packagename = ?", new String[]{pkgName}) > 0) {
            return true;
        }
        return false;
    }

    public static boolean updatePermission(Context context, String pkgName) {
        ContentValues cv = new ContentValues();
        cv.put("status", 1);
        if (context.getContentResolver().update(Uri.parse(URI), cv, " packagename =? ", new String[]{pkgName}) > 0) {
            return true;
        }
        return false;
    }

    public static boolean grantRuntimePermission(Context context, String packageName, String permissionName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        PackageManager pm = context.getPackageManager();

        int granted = pm.checkPermission(permissionName, packageName);
        int uid = getApplicationUid(context, packageName);

        if (granted == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        try {
            Method grantRuntimePermission = PackageManager.class.getMethod("grantRuntimePermission",
                    String.class, String.class,
                    UserHandle.class);
            grantRuntimePermission.setAccessible(true);

            if (Build.VERSION.SDK_INT >= 24) {
                grantRuntimePermission.invoke(pm, packageName, permissionName, getUserHandle(uid));
            } else {
                Constructor<UserHandle> constructor = UserHandle.class.getConstructor(int.class);
                constructor.setAccessible(true);
                grantRuntimePermission.invoke(pm, packageName, permissionName, constructor.newInstance(uid));
            }

            boolean result = pm.checkPermission(permissionName, packageName) == PackageManager.PERMISSION_GRANTED;
            return result;
        } catch (Throwable e) {
        }
        return false;
    }

    public static UserHandle getUserHandle(int uid) {
        try {
            Method m;
            Class<?> classType = UserHandle.class;
            m = classType.getDeclaredMethod("getUserHandleForUid", int.class);
            return (UserHandle) m.invoke(null, uid);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserHandle getUserHandler(Context context, String packageName) {
        int uid = getApplicationUid(context, packageName);
        if (Build.VERSION.SDK_INT >= 24) {
            return getUserHandle(uid);
        } else {
            Constructor<UserHandle> constructor = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    constructor = UserHandle.class.getConstructor(int.class);
                    constructor.setAccessible(true);
                    return (UserHandle) constructor.newInstance(uid);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public static int getApplicationUid(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void addOneCleanWhiteApp(String packageName, Context context) {
        try {
            List<String> whiteApps = loadWhiteApps(context);
            if (whiteApps != null && whiteApps.contains(packageName)) {
                return;
            }
            ContentValues cv = new ContentValues();
            cv.put("packagename", packageName);
            cv.put("usertype", "oneclean");
            cv.put("status", "2");
            context.getContentResolver().insert(Uri.parse("content://com.amigo.settings.RosterProvider/rosters"), cv);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static List<String> loadWhiteApps(Context context) {
        Uri uri = Uri.parse("content://com.amigo.settings.RosterProvider/rosters");
        ArrayList<String> whiteApps = new ArrayList<>();
        Cursor cursor = null;
        try {
            /**
             * Note:
             * user white apps status = 2
             * screen off white apps status = 10
             */
            cursor = context.getContentResolver().query(uri, new String[]{"packagename"},
                    "usertype='oneclean' and (status='10' or status='2')", null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    whiteApps.add(cursor.getString(0));
                } while (cursor.moveToNext());
                if (cursor != null) {
                }
            } else if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return whiteApps;
    }
}
