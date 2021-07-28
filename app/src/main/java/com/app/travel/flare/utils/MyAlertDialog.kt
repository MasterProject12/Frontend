package com.app.travel.flare.utils

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.app.travel.flare.R

class MyAlertDialog : OnClickListener {
    lateinit var alert : AlertDialog
    lateinit var listener : NotifyIncidentListener

     fun showAlertDialog(context: Context, title: String, msg : String, listener : NotifyIncidentListener){
        this.listener = listener
        val dialogBuilder = context?.let { AlertDialog.Builder(it) }
        dialogBuilder?.setMessage(msg)
        alert = dialogBuilder?.create()!!
        alert.setTitle(title)
        alert.setMessage(msg)
        alert.setButton(BUTTON_POSITIVE, context.getString(R.string.yes),this)
        alert.setButton(BUTTON_NEGATIVE, context.getString(R.string.no),this)

        alert.show()
    }

    fun dismissAlertDialog(){
        if(alert.isShowing){
            alert.dismiss()
        }
    }

    override fun onClick(btnId: DialogInterface?, id: Int) {
        if (btnId != null) {
            if(id == -2){
                dismissAlertDialog()
                listener.handleNegativeBtn()
            }
            if(id == -1){
                listener.handlePositiveBtn()
            }
        }
    }
}