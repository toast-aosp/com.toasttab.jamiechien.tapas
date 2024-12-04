package com.toast.test.buildmodelproptestapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class PropertyPollingService extends Service {
    private static final String TAG = PropertyPollingService.class.getSimpleName();
    public static final String ACTION_PROP_UPDATE = "com.toast.test.PROP_UPDATE";
    private static final String CHANNEL_ID = "PropertyPollingServiceChannel";

    private Handler mHandler = null;
    private Runnable pollingPropBuildModel =
            new Runnable() {
                @Override
                public void run() {
                    getBuildModel();
                    mHandler.postDelayed(pollingPropBuildModel, 1000);
                }
            };

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForegroundService();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "Property Polling Service Channel",
                        NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void startForegroundService() {
        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Property Polling Service")
                        .setContentText("Running...")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .build();
        // Starting FGS without a type will cause a warning in the logcat
        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
    }

    private void getBuildModel() {
        Intent intent = new Intent(ACTION_PROP_UPDATE);
        intent.setPackage(getPackageName());
        intent.putExtra("build_model", Build.MODEL);
        sendBroadcast(intent);
        Log.i(TAG, "Build.MODEL=" + Build.MODEL);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        Toast.makeText(this, "Property Polling Service Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_PROP_UPDATE.equals(intent.getAction())) {
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(pollingPropBuildModel);
        }
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
