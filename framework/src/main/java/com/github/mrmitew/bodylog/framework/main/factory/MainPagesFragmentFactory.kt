package com.github.mrmitew.bodylog.framework.main.factory

import android.support.v4.app.Fragment
import com.github.mrmitew.bodylog.framework.dashboard.view.DashboardFragment
import com.github.mrmitew.bodylog.framework.profile.details.view.ProfileDetailsFragment

object MainPagesFragmentFactory {
    val fragments = arrayOf(DashboardFragment::class.java,
            ProfileDetailsFragment::class.java)

    fun create(pagePosition: Int): Fragment {
        if (pagePosition + 1 <= fragments.size) {
            return fragments[pagePosition].newInstance()
        }

        throw RuntimeException("Invalid page position: " + pagePosition)
    }
}
