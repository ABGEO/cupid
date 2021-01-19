package dev.abgeo.cupid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import dev.abgeo.cupid.R

class EntryPointFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entry_point, container, false)
        val bntSignIn = view.findViewById<Button>(R.id.btnSignIn)
        val bntSignUp = view.findViewById<Button>(R.id.btnSignUp)

        bntSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_navEntryPointFragment_to_navLoginFragment)
        }

        bntSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_navEntryPointFragment_to_navRegistrationFragment)
        }

        return view
    }
}