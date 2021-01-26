package dev.abgeo.cupid.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import dev.abgeo.cupid.helper.setErrorWithFocus
import dev.abgeo.cupid.viewmodel.UserViewModel

class LoginFragment : Fragment() {
    private val TAG = this::class.qualifiedName
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

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
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val btnSignIn = view.findViewById<Button>(R.id.btnSignIn)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)

        btnSignIn.setOnClickListener {
            // TODO: Implement loader.

            when {
                etEmail.text.isEmpty() -> {
                    etEmail.setErrorWithFocus(getText(R.string.email_is_empty))
                }
                etPassword.text.isEmpty() -> {
                    etPassword.setErrorWithFocus(getText(R.string.password_is_empty))
                }
                else -> {
                    auth.signInWithEmailAndPassword(
                            etEmail.text.toString(),
                            etPassword.text.toString()
                    )
                            .addOnCompleteListener(activity as Activity) { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "createUserWithEmail: success")

                                    auth.currentUser?.let {
                                        db.reference.child("users").child(it.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                dataSnapshot.getValue<User>()?.let { u -> userViewModel.postCurrentUser(u) }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                Log.w(TAG, "onCancelled", databaseError.toException())
                                            }
                                        })
                                    }

                                    findNavController().navigate(R.id.action_navLoginFragment_to_navHomeFragment)
                                } else {
                                    Log.w(TAG, "createUserWithEmail: failure", task.exception)

                                    Toast.makeText(context, getText(R.string.invalid_credentials), Toast.LENGTH_SHORT).show()
                                }
                            }
                }
            }
        }

        return view
    }
}
