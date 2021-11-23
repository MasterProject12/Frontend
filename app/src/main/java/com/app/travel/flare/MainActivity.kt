package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.app.travel.flare.databinding.ActivityMainBinding
import com.app.travel.flare.utils.Utils
import com.app.travel.flare.viewModel.MainActivityViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.nav_header.view.*

class MainActivity : AppCompatActivity(){

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var headerView: View
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var viewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        headerView = binding.navView.getHeaderView(0)

        Utils.cacheData(true,Utils.IS_LOGGED_IN,this)
        NavigationUI.setupWithNavController(binding.navView, navController)

        var username = Utils.getStringData(Utils.USER_NAME,this)
        var email = Utils.getStringData(Utils.EMAIL_ID,this)
        headerView = binding.navView.getHeaderView(0)
        headerView.nameTv.text = username
        headerView.emailTV.text = email
        var avatar = headerView.avatar
        Glide
            .with(this)
            .load(R.drawable.logo)
            .centerCrop()
            .into(avatar)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_items,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logoutBtn){
            Utils.cacheData(false, Utils.IS_LOGGED_IN,this)
            var intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        private const val TAG = "MainActivity"
    }
}