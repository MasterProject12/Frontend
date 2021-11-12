package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.flare.utils.Utils


class SplashScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        findViewById<ImageView>(R.id.splashImage).startAnimation(slideAnimation)

        Handler().postDelayed({

            if(Utils.getCacheData(Utils.IS_LOGGED_IN,this)){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, LoginActivity::class.java)

                startActivity(intent)
            }
            finish()
        }, 3000)
    }


}