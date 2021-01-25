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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dev.abgeo.cupid.R
import dev.abgeo.cupid.entity.User

class ProfileFragment : Fragment() {
    private val TAG = this::class.qualifiedName
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
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

        auth.currentUser?.let {
            db.reference.child("users").child(it.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue<User>()?.let { u ->
                        tvName.setText(u.name)

                        storage.reference.child("avatars/${u.id}.png").downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(activity as Activity)
                                .load(uri)
                                .error(R.drawable.ic_account_64)
                                .into(avatar)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                }
            })
        }

        ibSettings.setOnClickListener {
            findNavController().navigate(R.id.action_navHomeFragment_to_navSettingsFragment)
        }

        ibEdit.setOnClickListener {
            findNavController().navigate(R.id.action_navHomeFragment_to_navEditProfileFragment)
        }

        return view
    }
}