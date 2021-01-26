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
import androidx.navigation.navGraphViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dev.abgeo.cupid.R
import dev.abgeo.cupid.entity.User
import dev.abgeo.cupid.viewmodel.UserViewModel

class SettingsFragment : Fragment() {
    private val TAG = this::class.qualifiedName
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var user: User

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

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

        userViewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            user = it

            rbMapping[user.interestedIn]?.let { k ->
                k.isChecked = true
            }
        })

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
        user.id?.let {
            user.interestedIn = value
            db.reference.child("users").child(it).setValue(user)
            userViewModel.postCurrentUser(user)
        }
    }

}