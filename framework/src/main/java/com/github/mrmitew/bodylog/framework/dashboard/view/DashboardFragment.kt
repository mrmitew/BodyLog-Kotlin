package com.github.mrmitew.bodylog.framework.dashboard.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mrmitew.bodylog.R

class DashboardFragment : Fragment() {
    companion object {
        fun newInstance() = DashboardFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_dashboard, container, false)
    }
}
