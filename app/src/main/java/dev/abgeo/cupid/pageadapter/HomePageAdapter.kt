package dev.abgeo.cupid.pageadapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import dev.abgeo.cupid.fragment.ProfileFragment
import dev.abgeo.cupid.fragment.MatchFragment
import dev.abgeo.cupid.fragment.MatchesFragment

class HomePageAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int = 3

    override fun getItem(i: Int): Fragment = when (i) {
        0 -> ProfileFragment()
        1 -> MatchFragment()
        else -> MatchesFragment()
    }
}
