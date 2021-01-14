package com.amroz.ystore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.amroz.ystore.Fragments.Catogrey_Fragment
import com.amroz.ystore.Fragments.ProductByCatFragment
import com.amroz.ystore.Fragments.ProductsFragment

//import com.amroz.ystore.Fragments.ReportFragment


class MainActivity : AppCompatActivity()  {
    private lateinit var nanHost:FrameLayout
    private  lateinit var  navView: BottomNavigationView
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
   /*     val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()


                .add(R.id.fragmentContainer, UsersFragment.newInstance("Users"))

                .commit()
        }*/
        nanHost=findViewById(R.id.nav_host_fragment)
        navView = findViewById(R.id.nav_view)



        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.products_nv, R.id.cats_nv, R.id.report_nv
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        nanHost.visibility=View.VISIBLE


    }


}


//
