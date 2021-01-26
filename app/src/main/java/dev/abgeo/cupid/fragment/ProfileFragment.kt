package dev.abgeo.cupid.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dev.abgeo.cupid.R
import dev.abgeo.cupid.viewmodel.UserViewModel

class ProfileFragment : Fragment() {
    private lateinit var storage: FirebaseStorage

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val avatar = view.findViewById<ImageView>(R.id.avatar)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val ibSettings = view.findViewById<ImageButton>(R.id.ibSettings)
        val ibEdit = view.findViewById<ImageButton>(R.id.ibEdit)

        userViewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            tvName.text = it.name

            storage.reference.child("avatars/${it.id}.png").downloadUrl.addOnSuccessListener { uri ->
                Glide.with(activity as Activity)
                        .load(uri)
                        .error(R.drawable.ic_account_64)
                        .into(avatar)
            }
        })

        ibSettings.setOnClickListener {
            findNavController().navigate(R.id.action_navHomeFragment_to_navSettingsFragment)
        }

        ibEdit.setOnClickListener {
            findNavController().navigate(R.id.action_navHomeFragment_to_navEditProfileFragment)
        }

        return view
    }
}