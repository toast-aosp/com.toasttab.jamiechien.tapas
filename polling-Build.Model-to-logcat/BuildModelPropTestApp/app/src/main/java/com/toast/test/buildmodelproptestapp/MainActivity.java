package com.toast.test.buildmodelproptestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final BroadcastReceiver propertyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent == null || intent.getAction() == null) {
                        return;
                    }
                    if (intent.getAction() == PropertyPollingService.ACTION_PROP_UPDATE) {
                        ((TextView) findViewById(R.id.value))
                                .setText(intent.getStringExtra("build_model"));
                        Log.i(TAG, "Build.Model=" + intent.getStringExtra("build_model"));
                    }
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Intent serviceIntent = new Intent(this, PropertyPollingService.class);
        startForegroundService(serviceIntent);

        Intent intent = new Intent(this, PropertyPollingService.class);
        intent.setAction(PropertyPollingService.ACTION_PROP_UPDATE);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        registerBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        unregisterBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        Log.i(TAG, "registerBroadcastReceiver");
        registerReceiver(
                propertyReceiver,
                new IntentFilter(PropertyPollingService.ACTION_PROP_UPDATE),
                RECEIVER_NOT_EXPORTED);
    }

    private void unregisterBroadcastReceiver() {
        Log.i(TAG, "unregisterBroadcastReceiver");
        unregisterReceiver(propertyReceiver);
    }
}
