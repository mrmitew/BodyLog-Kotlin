package com.github.mrmitew.bodylog.framework.dashboard.view

import android.os.Bundle
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.framework.common.view.InjectableActivity
import com.github.mrmitew.bodylog.framework.dashboard.di.DashboardActivityComponent
import com.github.mrmitew.bodylog.framework.di.activity.HasActivitySubcomponentBuilders
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : InjectableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setListeners()
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) =
            (hasActivitySubcomponentBuilders.getActivityComponentBuilder(DashboardActivity::class.java) as DashboardActivityComponent.Builder)
                    .activityModule(DashboardActivityComponent.ComponentModule(this))
                    .build()
                    .injectMembers(this)

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