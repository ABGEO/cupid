package dev.abgeo.cupid.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.abgeo.cupid.R
import dev.abgeo.cupid.helper.setErrorWithFocus
import dev.abgeo.cupid.helper.showProgressDisableButton

class ResetPasswordFragment : Fragment() {
    private val TAG = this::class.qualifiedName

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reset_password, container, false)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnResetPassword = view.findViewById<Button>(R.id.btnResetPassword)

        btnResetPassword.setOnClickListener {
            showProgressDisableButton(progressBar, btnResetPassword, true)

            when {
                etEmail.text.isEmpty() -> {
                    etEmail.setErrorWithFocus(getText(R.string.email_is_empty))
                    showProgressDisableButton(progressBar, btnResetPassword, false)
                }
                else -> {
                    Firebase.auth.sendPasswordResetEmail(etEmail.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "sendPasswordResetEmail: success")

                                Snackbar.make(
                                    view,
                                    R.string.reset_email_sent,
                                    Snackbar.LENGTH_LONG
                                ).show()

                                showProgressDisableButton(progressBar, btnResetPassword, false)
                            } else {
                                Log.w(TAG, "sendPasswordResetEmail: failure", task.exception)

                                when (task.exception) {
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        when ((task.exception as FirebaseAuthInvalidCredentialsException).errorCode) {
                                            "ERROR_INVALID_EMAIL" -> {
                                                etEmail.setErrorWithFocus(getText(R.string.email_is_invalid))
                                            }
                                        }
                                    }
                                }

                                showProgressDisableButton(progressBar, btnResetPassword, false)
                            }
                        }
                }
            }
        }

        return view
    }
}