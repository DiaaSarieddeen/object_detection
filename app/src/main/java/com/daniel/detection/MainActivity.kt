package com.daniel.detection

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.daniel.detection.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.fragment_home.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

        @SuppressLint("IntentReset")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            //first inflate the binding. Then set content view and bind all elements of xml
            //binding = ActivityMainBinding.inflate(layoutInflater)
            //val view = binding.root

            //setContentView(view)
            val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            navHostFragment.navController

        }
}