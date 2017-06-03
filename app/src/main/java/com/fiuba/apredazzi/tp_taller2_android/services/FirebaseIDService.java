package com.fiuba.apredazzi.tp_taller2_android.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by apredazzi on 5/15/17.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String myId = settings.getString("myId", null);
        if (myId != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("user_" + myId);
        }

        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(final String refreshedToken) {

    }
}
