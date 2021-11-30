package com.app.travel.flare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.app.travel.flare.IncidentMetaData.IncidentInfo;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    private void print_local_intent(Intent intent) {
        Log.d(TAG, "      Intent received");
        Log.d(TAG, "      Component name: " + intent.getComponent().toString());
        Log.d(TAG, "      Action: " + intent.getAction());
        Log.d(TAG, "      Data str: " + intent.getDataString());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Toast.makeText(context, "Geofence Triggered..", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        // print_local_intent(intent);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);


        if (geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error in receiving the geofence event..");
            return;
        }

        List<Geofence> geofenceList= geofencingEvent.getTriggeringGeofences();
        StringBuilder reasonStr = new StringBuilder("");

        for (Geofence geofence: geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());

            String incidentKey = geofence.getRequestId();
            IncidentInfo info = IncidentMetaData.getIncidentInfo(incidentKey);
            Log.d(TAG, "Looked up meta data for geofence with request ID: " + incidentKey + "  data: " + info);
            reasonStr.append(info.createReason);
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "onReceive: Geofence_ENTER occured");
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER..", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("USER HAS ENTERED THE GEOFENCE",  reasonStr.toString(), MapsActivity.class);
                break;

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                //Log.d(TAG, "onReceive: Geofence_DWELL occured");
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL..", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("USER IS IN THE GEOFENCE - DWELL", "Drive Safe!", MapsActivity.class);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                //Log.d(TAG, "onReceive: Geofence_EXIT occured");
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT..", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("USER HAS EXITED THE GEOFENCE", "", MapsActivity.class);
                break;
        }
    }
}
