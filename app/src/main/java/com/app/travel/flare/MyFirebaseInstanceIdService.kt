package com.app.travel.flare

import android.media.session.MediaSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceIdService : FirebaseMessagingService()
{

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
    }
}