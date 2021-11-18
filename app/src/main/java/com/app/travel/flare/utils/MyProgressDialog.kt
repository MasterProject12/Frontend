package com.app.travel.flare.utils

import android.app.ProgressDialog
import android.content.Context

class MyProgressDialog {

    lateinit var progressDialog : ProgressDialog
    fun showProgressDialog(context: Context, title: String, msg : String){

        progressDialog = ProgressDialog(context)
        progressDialog.setTitle(title)
        //progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()
    }

    fun dismissProgressDialog(){
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
    }
}