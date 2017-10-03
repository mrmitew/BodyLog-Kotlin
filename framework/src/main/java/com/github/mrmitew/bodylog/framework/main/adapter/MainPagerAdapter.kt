package com.github.mrmitew.bodylog.framework.main.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.github.mrmitew.bodylog.framework.main.factory.MainPagesFragmentFactory

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = MainPagesFragmentFactory.create(position)
    override fun getCount(): Int = MainPagesFragmentFactory.fragments.size
}