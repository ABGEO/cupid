package dev.abgeo.cupid.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dev.abgeo.cupid.R
import dev.abgeo.cupid.entity.User

class SettingsFragment : Fragment() {
    private val TAG = this::class.qualifiedName
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val rbMale = view.findViewById<RadioButton>(R.id.rbMale)
        val rbFemale = view.findViewById<RadioButton>(R.id.rbFemale)
        val rbBoth = view.findViewById<RadioButton>(R.id.rbBoth)
        val btnSignOut = view.findViewById<Button>(R.id.btnSignOut)
        val rbMapping = mapOf(
                1 to rbMale,
                2 to rbFemale,
                3 to rbBoth,
        )

        auth.currentUser?.let {
            db.reference.child("users").child(it.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue<User>()?.let { u ->
                        rbMapping[u.interestedIn]?.let { k ->
                            k.isChecked = true
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                }
            })
        }

        rbMale.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Log.d(TAG, "onCheckedChangeListener: Male checked")
                updateInterestedIn(1)
            }
        }

        rbFemale.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Log.d(TAG, "onCheckedChangeListener: Female checked")
                updateInterestedIn(2)
            }
        }

        rbBoth.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Log.d(TAG, "onCheckedChangeListener: Both checked")
                updateInterestedIn(3)
            }
        }

        btnSignOut.setOnClickListener{
            auth.signOut()
            findNavController().navigate(R.id.action_navSettingsFragment_to_navEntryPointFragment)
        }

        return view
    }

    private fun updateInterestedIn(value: Int) {
        auth.currentUser?.let {
            val currentUserDb = db.reference.child("users").child(it.uid)
            val userInfo = mapOf(
                    "interestedIn" to value
            )

            currentUserDb.updateChildren(userInfo)

            // TODO: Post Live Data with user object.
        }
    }

}