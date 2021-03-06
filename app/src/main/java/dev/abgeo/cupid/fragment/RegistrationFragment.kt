package dev.abgeo.cupid.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dev.abgeo.cupid.R
import dev.abgeo.cupid.entity.User
import dev.abgeo.cupid.helper.setErrorWithFocus
import dev.abgeo.cupid.helper.showProgressDisableButton
import dev.abgeo.cupid.viewmodel.UserViewModel

class RegistrationFragment : Fragment() {
    private val TAG = this::class.qualifiedName
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var gender = 0

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
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val btnSignUp = view.findViewById<Button>(R.id.btnSignUp)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etRepeatPassword = view.findViewById<EditText>(R.id.etRepeatPassword)
        val rbMale = view.findViewById<RadioButton>(R.id.rbMale)
        val rbFemale = view.findViewById<RadioButton>(R.id.rbFemale)

        rbMale.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Log.d(TAG, "onCheckedChangeListener: Male gender checked")
                gender = 1
            }
        }

        rbFemale.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Log.d(TAG, "onCheckedChangeListener: Female gender checked")
                gender = 2
            }
        }

        btnSignUp.setOnClickListener {
            showProgressDisableButton(progressBar, btnSignUp, true)

            when {
                etName.text.isEmpty() -> {
                    etName.setErrorWithFocus(getText(R.string.name_is_invalid))
                    showProgressDisableButton(progressBar, btnSignUp, false)
                }
                etEmail.text.isEmpty() -> {
                    etEmail.setErrorWithFocus(getText(R.string.email_is_empty))
                    showProgressDisableButton(progressBar, btnSignUp, false)
                }
                !etEmail.text.endsWith("@cu.edu.ge") -> {
                    etEmail.setErrorWithFocus(getText(R.string.not_cu_email))
                    showProgressDisableButton(progressBar, btnSignUp, false)
                }
                etPassword.text.length < 6 -> {
                    etPassword.setErrorWithFocus(getText(R.string.password_is_invalid))
                    showProgressDisableButton(progressBar, btnSignUp, false)
                }
                etPassword.text.toString() != etRepeatPassword.text.toString() -> {
                    etRepeatPassword.setErrorWithFocus(getText(R.string.passwords_mismatch))
                    showProgressDisableButton(progressBar, btnSignUp, false)
                }
                gender == 0 -> {
                    rbMale.setErrorWithFocus(getText(R.string.gender_is_empty))
                    showProgressDisableButton(progressBar, btnSignUp, false)
                }
                else -> {
                    auth.createUserWithEmailAndPassword(
                        etEmail.text.toString(),
                        etPassword.text.toString()
                    )
                        .addOnCompleteListener(activity as Activity) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "createUserWithEmail: success")

                                auth.currentUser?.let {
                                    val user = User(
                                            it.uid,
                                            etName.text.toString(),
                                            etEmail.text.toString(),
                                            gender
                                    )
                                    db.reference.child("users").child(it.uid).setValue(user)
                                    userViewModel.postCurrentUser(user)
                                }

                                findNavController().navigate(R.id.action_navRegistrationFragment_to_navHomeFragment)
                            } else {
                                Log.w(TAG, "createUserWithEmail: failure", task.exception)

                                when (task.exception) {
                                    is FirebaseAuthUserCollisionException -> {
                                        etEmail.setErrorWithFocus(getText(R.string.email_is_taken))
                                    }
                                    is FirebaseAuthWeakPasswordException -> {
                                        etPassword.setErrorWithFocus(getText(R.string.password_is_invalid))
                                    }
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        when ((task.exception as FirebaseAuthInvalidCredentialsException).errorCode) {
                                            "ERROR_INVALID_EMAIL" -> {
                                                etEmail.setErrorWithFocus(getText(R.string.email_is_invalid))
                                            }
                                        }
                                    }
                                }

                                showProgressDisableButton(progressBar, btnSignUp, false)
                            }
                        }
                }
            }
        }

        return view
    }
}