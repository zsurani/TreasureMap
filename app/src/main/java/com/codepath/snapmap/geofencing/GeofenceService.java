package com.codepath.snapmap.geofencing;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.codepath.snapmap.MainActivity;
import com.codepath.snapmap.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by mbakhiet on 7/27/16.
 */
public class GeofenceService extends IntentService {

    public static final String TAG = "GeofenceService";

    public GeofenceService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            //TODO: handle error
            Log.e(TAG, "event error");
        } else {

            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();

            for (Geofence geofence : geofences){
                String requestId = geofence.getRequestId();

                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
                        Log.d(TAG, "Entering geofence - " + requestId);
                        // Set the notification text and send the notification
                        String contextText = String.format(this.getResources().getString(R.string.Notification_Text), geofence.getRequestId());
                        MainActivity.createNotification(ParseUser.getCurrentUser().getString("push_id"), contextText);

                } else if (transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                    Log.d(TAG, "Dwelling in geofence - " + requestId);

                } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    Log.d(TAG, "Exiting geofence - " + requestId);
                }
            }
        }
    }
}
