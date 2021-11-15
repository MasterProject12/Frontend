package com.app.travel.flare.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.travel.flare.network.Networking
import com.app.travel.flare.utils.HttpMethods
import com.google.gson.JsonObject


class RegisterActivityViewModel : ViewModel() {

    var registerResultLiveData = MutableLiveData<Boolean>()

    fun registerUser(userName: String?, email: String?, password: String?) {
        val url = "https://1bgkyjhncb.execute-api.us-west-2.amazonaws.com/prod/register"

        val jsonObj = JsonObject()
        jsonObj.addProperty("user_name",userName)
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
                    Log.d(TAG, "Registration Success Response: $response")
                    registerResultLiveData.postValue(true)
                }

                override fun onFailure(failureType: Networking.ResponseHandler.FailureType?, responseCode: Int, reason: String?) {
                    Log.d(TAG, "Registration Failed: $failureType")
                    registerResultLiveData.postValue(false)
                }
            })
    }

    companion object{
        val TAG : String = RegisterActivityViewModel::class.java.name
    }
}
