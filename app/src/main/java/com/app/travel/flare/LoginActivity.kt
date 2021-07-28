package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.travel.flare.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginBtn.setOnClickListener{
            val intent = Intent(this, ReportIncidentActivity::class.java)
            startActivity(intent)
        }
        binding.googleLoginBtn.setOnClickListener{
            var intent = Intent(this, NotifyIncidentActivity::class.java)
            startActivity(intent)
        }
    }
}