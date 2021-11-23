package com.app.travel.flare.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.travel.flare.network.Networking
import com.app.travel.flare.utils.HttpMethods
import com.app.travel.flare.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class LoginActivityViewModel : ViewModel() {

    var loginResultLiveData = MutableLiveData<String>()

    fun loginUser(email: String?, password: String?) {
        val url = "https://1bgkyjhncb.execute-api.us-west-2.amazonaws.com/prod/authenticate"

        val jsonObj = JsonObject()
        jsonObj.addProperty("email_id",email)
        jsonObj.addProperty("password",password)

        var map = HashMap<String,String>()
        map["Content-Type"] = "application/json"

        Networking.INSTANCE.asyncConnection(
            url,
            HttpMethods.POST.name,
            jsonObj.toString(),
            map,
            object : Networking.ResponseHandler {
                override fun onSuccess(response: String?) {
                    Log.d(TAG, "Login Success Response: $response")

                    loginResultLiveData.postValue(response)
                }

                override fun onFailure(failureType: Networking.ResponseHandler.FailureType?, responseCode: Int, reason: String?) {
                    Log.d(TAG, "Login Failed: $failureType")
                    loginResultLiveData.postValue(reason)
                }
            })
    }



    companion object{
        val TAG : String = LoginActivityViewModel::class.java.name
    }
}