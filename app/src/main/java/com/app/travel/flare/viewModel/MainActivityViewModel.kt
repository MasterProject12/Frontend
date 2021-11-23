package com.app.travel.flare.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.travel.flare.network.Networking
import com.app.travel.flare.utils.HttpMethods
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject

class MainActivityViewModel : ViewModel() {

    lateinit var token:String
    var subscribeCityLiveData = MutableLiveData<Boolean>()

    init{
        fetchToken()
    }
    fun fetchToken(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    token = task.result
                    Log.d("FirebaseMessaging", "Firebase Token: $token")
                }
            })
    }

    fun subscribe(city: String?) {
        val url = "https://1bgkyjhncb.execute-api.us-west-2.amazonaws.com/prod/subscribe"
        Log.d(TAG, "City : $city Token : $token")
        val jsonObj = JsonObject()
        jsonObj.addProperty("city",city)
        jsonObj.addProperty("token",token)

        var map = HashMap<String,String>()
        map["Content-Type"] = "application/json"

        Networking.INSTANCE.asyncConnection(
            url,
            HttpMethods.POST.name,
            jsonObj.toString(),
            map,
            object : Networking.ResponseHandler {
                override fun onSuccess(response: String?) {
                    Log.d(TAG, "Subscribe Success : $response")
                    subscribeCityLiveData.postValue(true)
                }

                override fun onFailure(failureType: Networking.ResponseHandler.FailureType?, responseCode: Int, reason: String?) {
                    Log.d(TAG, "Subscribe Failed: $failureType")
                    subscribeCityLiveData.postValue(false)
                }
            })
    }

    companion object{
        val TAG : String = MainActivityViewModel::class.java.name
    }
}
