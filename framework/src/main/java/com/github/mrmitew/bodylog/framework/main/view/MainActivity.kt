package com.github.mrmitew.bodylog.framework.main.view

import android.os.Bundle
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.framework.common.view.InjectableActivity
import com.github.mrmitew.bodylog.framework.di.activity.HasActivitySubcomponentBuilders
import com.github.mrmitew.bodylog.framework.main.adapter.MainPagerAdapter
import com.github.mrmitew.bodylog.framework.main.di.MainActivityComponent
import com.github.mrmitew.bodylog.framework.measurement.view.LogMeasurementDialogFragment
import com.github.mrmitew.bodylog.framework.measurement.view.MeasurementLogLayout
import com.github.mrmitew.bodylog.framework.weight.view.LogWeightDialogFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : InjectableActivity(), MeasurementLogLayout.MeasurementLogInjectionProvider {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPager()
        setListeners()
    }

    lateinit var mainActivityComponent: MainActivityComponent

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        mainActivityComponent = (hasActivitySubcomponentBuilders.getActivityComponentBuilder(MainActivity::class.java) as MainActivityComponent.Builder)
                .activityModule(MainActivityComponent.ComponentModule(this))
                .build()
        mainActivityComponent
                .injectMembers(this)
    }

    override fun measurementLogProviderComponent() = mainActivityComponent

    private fun setupPager() =
            viewPager.apply {
                adapter = MainPagerAdapter(supportFragmentManager)
                currentItem = 0
                offscreenPageLimit = 2
            }

    private fun setListeners() {
        main_bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_dashboard -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.action_profile -> {
                    viewPager.currentItem = 1
                    true
                }
                else -> false
            }
        }

        fab.setMainFabOnClickListener {
            if (fab.isOptionsMenuOpened) {
                fab.closeOptionsMenu()
            }
        }

        fab.setMiniFabSelectedListener {
            when (it.itemId) {
                R.id.action_log_weight -> LogWeightDialogFragment.startFragment(fragmentManager = supportFragmentManager)
                R.id.action_log_measurement -> LogMeasurementDialogFragment.startFragment(fragmentManager = supportFragmentManager)
            }
            fab.closeOptionsMenu()
        }
    }
}
