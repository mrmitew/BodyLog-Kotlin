package com.github.mrmitew.bodylog.framework.main.view

import android.os.Bundle
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.framework.common.view.InjectableActivity
import com.github.mrmitew.bodylog.framework.di.activity.HasActivitySubcomponentBuilders
import com.github.mrmitew.bodylog.framework.main.adapter.MainPagerAdapter
import com.github.mrmitew.bodylog.framework.main.di.MainActivityComponent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : InjectableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPager()
        setListeners()
    }

    private fun setupPager() =
            viewPager.apply {
                adapter = MainPagerAdapter(supportFragmentManager)
                currentItem = 0
                offscreenPageLimit = 2
            }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) =
            (hasActivitySubcomponentBuilders.getActivityComponentBuilder(MainActivity::class.java) as MainActivityComponent.Builder)
                    .activityModule(MainActivityComponent.ComponentModule(this))
                    .build()
                    .injectMembers(this)

    private fun setListeners() {
        main_bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_dashboard -> {
                    println("Home clicked")
                    viewPager.currentItem = 0
                    true
                }
                R.id.action_profile -> {
                    println("Profile clicked")
                    viewPager.currentItem = 1
                    true
                }
                else -> false
            }
        }

        fab.setOnClickListener { println("Fab clicked") }
    }
}