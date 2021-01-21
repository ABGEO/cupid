package dev.abgeo.cupid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import dev.abgeo.cupid.R

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val ibSettings = view.findViewById<ImageButton>(R.id.ibSettings)

        ibSettings.setOnClickListener {
            findNavController().navigate(R.id.action_navHomeFragment_to_navSettingsFragment)
        }

        return view
    }
}