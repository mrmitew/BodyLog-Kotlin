package com.github.mrmitew.bodylog.framework.dashboard.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mrmitew.bodylog.R
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setListeners()
    }

    private fun setListeners() {
        dashboard_bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    println("Home clicked")
                    true
                }
                R.id.action_profile -> {
                    println("Profile clicked")
                    true
                }
                else -> false
            }
        }
    }
}