package com.smartlab.remoteattendance_v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    // ...
    public static final String EXTRA_IN = "1";
    public static final String EXTRA_OUT = "0";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();
    public static final String ACTION_MyIntentService = "com.example.androidintentservice.RESPONSE";
    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
        {
            Log.i("hoi","hoi");
        }
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.i("itik2","itik");
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        Log.i("itik",String.valueOf(geoFenceTransition));



    }



}