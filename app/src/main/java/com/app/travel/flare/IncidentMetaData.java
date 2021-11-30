package com.app.travel.flare;

import com.google.android.gms.maps.model.LatLng;
import android.util.Log;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;

public class IncidentMetaData {
    private static final String TAG = "IncidentMetaData";
    public static class IncidentInfo {
        public IncidentInfo(LatLng point, String reason) {
            latLng = point;
            createReason = reason;
            geofenceRequestId = "";
        }
        public LatLng latLng;
        public String createReason;
        public String geofenceRequestId;
        public String toString() {
            return "Latitude, Longitude: " + latLng + ", create reason: " + createReason;
        }
    }
    public HashMap<String, IncidentInfo> incidentInfoMap = new HashMap<String, IncidentInfo>();

    // Static variable reference of incident_metadata class
    // of type IncidentMetaData
    private static IncidentMetaData incident_metadata = null;

    // Static method
    // Static method to create instance of IncidentMetaData class
    public static IncidentMetaData getInstance()
    {
        if (incident_metadata == null)
            incident_metadata = new IncidentMetaData();

        return incident_metadata;
    }

    public static Set<String> getIncidentKeys() {
        IncidentMetaData incMetaData = getInstance();

        return incMetaData.incidentInfoMap.keySet();
    }

    public static ArrayList<String> getGeofenceRequestIds(ArrayList<String> latlongKeys) {
        ArrayList<String> geofenceRequestIds = new ArrayList<>();
        IncidentMetaData incMetaData = getInstance();
        for (String latlongKey : latlongKeys) {
            if (!incMetaData.incidentInfoMap.containsKey(latlongKey)) {
                Log.d(TAG, "Cannot find key " + latlongKey + " in incident map to get request ID");
                continue;
            }
            geofenceRequestIds.add(latlongKey);
        }

        return geofenceRequestIds;
    }

    public static boolean hasIncidentInfo(String latLongKey) {
        IncidentMetaData incMetaData = getInstance();
        if (incMetaData.incidentInfoMap.containsKey(latLongKey)) {
            return true;
        }

        return false;
    }

    public static IncidentInfo getIncidentInfo(String latLongKey) {
        IncidentMetaData incMetaData = getInstance();
        if (incMetaData.incidentInfoMap.containsKey(latLongKey)) {
            return incMetaData.incidentInfoMap.get(latLongKey);
        }

        return new IncidentInfo(new LatLng(0.0, 0.0), "");
    }

    public static void addIncidentInfo(String latLongKey, IncidentInfo info) {
        IncidentMetaData incMetaData = getInstance();
        incMetaData.incidentInfoMap.put(latLongKey, info);
    }

    public static void removeIncidentInfo(String latLongKey) {
        IncidentMetaData incMetaData = getInstance();
        if (incMetaData.incidentInfoMap.containsKey(latLongKey)) {
            incMetaData.incidentInfoMap.remove(latLongKey);
        }
    }
}
