package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.travel.flare.databinding.ActivityMainBinding
import com.app.travel.flare.notification.INCIDENT_DETAIL_MESSAGE
import com.app.travel.flare.notification.INCIDENT_PUSH
import com.app.travel.flare.viewModel.HomeActivityViewModel


class MainActivity : AppCompatActivity() {

    lateinit var viewModel : HomeActivityViewModel
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (intent != null) {
            if (intent.action != null && intent.action == INCIDENT_PUSH) {
                intent.getStringExtra(INCIDENT_DETAIL_MESSAGE)?.let { showMessageDialog(it) }
            }
        }
        binding.reportBtn.setOnClickListener{
            var intent = Intent(this, ReportIncidentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showMessageDialog(message: String) {
        AlertDialog.Builder(this).setMessage(message)
            .setPositiveButton(
                    "Ok"
            ) { dialog, _ -> dialog.dismiss() }.create().show()
    }

    companion object{
        var TAG : String = MainActivity::class.java.name
    }
}