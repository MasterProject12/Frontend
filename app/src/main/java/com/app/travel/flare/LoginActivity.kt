package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.travel.flare.databinding.ActivityLoginBinding
import com.app.travel.flare.utils.Utils
import com.app.travel.flare.viewModel.LoginActivityViewModel
import com.app.travel.flare.viewModel.RegisterActivityViewModel

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var  model : LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        model = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        binding.loginBtn.setOnClickListener{
            val email = binding.emailLogin.text.toString()
            val pwd = binding.passwordLogin.text.toString()
            model.loginUser(email,pwd)

//            val intent = Intent(this, HomeActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
//                    Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
        }

        binding.registerTV.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        model.loginResultLiveData.observe(this,
            Observer<Boolean> { aBoolean ->
                if (aBoolean) {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "Login failed. Try again later.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}