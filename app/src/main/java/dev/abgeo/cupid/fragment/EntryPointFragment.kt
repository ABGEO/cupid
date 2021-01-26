package dev.abgeo.cupid.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
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
import dev.abgeo.cupid.viewmodel.UserViewModel

class EntryPointFragment : Fragment() {
    private val TAG = this::class.qualifiedName
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
    }

    override fun onStart() {
        super.onStart()

        auth.currentUser?.let {
            Log.d(TAG, "onStart: User ${it.email} is signed-in")

            // Get user information from database and post to VM.
            db.reference.child("users").child(it.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue<User>()?.let { u -> userViewModel.postCurrentUser(u) }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "onCancelled", databaseError.toException())
                }
            })

            findNavController().navigate(R.id.action_navEntryPointFragment_to_navHomeFragment)
        }
    }

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