package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.travel.flare.databinding.ActivityRegisterBinding
import com.app.travel.flare.viewModel.RegisterActivityViewModel
import com.app.travel.flare.viewModel.ReportIncidentViewModel

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var  model : RegisterActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        model = ViewModelProvider(this).get(RegisterActivityViewModel::class.java)

        binding.registerBtn.setOnClickListener {
            val userName = binding.usernameRegister.text.toString()
            val email = binding.emailRegister.text.toString()
            val pwd = binding.passwordRegister.text.toString()
            model.registerUser(userName,email,pwd)
        }

        model.registerResultLiveData.observe(this,
            Observer<Boolean> { aBoolean ->
                if (aBoolean) {
                    Log.d(ReportIncidentActivity.TAG, "Registration successfully")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed. Try again later.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}