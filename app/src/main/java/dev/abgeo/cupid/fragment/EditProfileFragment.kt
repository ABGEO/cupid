package dev.abgeo.cupid.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
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
import dev.abgeo.cupid.helper.setErrorWithFocus
import dev.abgeo.cupid.viewmodel.UserViewModel

class EditProfileFragment : Fragment() {
    private val TAG = this::class.qualifiedName
    private lateinit var db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var user: User
    private lateinit var avatar: ImageView
    private var selectedImageUri: Uri? = null

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseDatabase.getInstance()
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        avatar = view.findViewById(R.id.avatar)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etAbout = view.findViewById<EditText>(R.id.etAbout)
        val etAge = view.findViewById<EditText>(R.id.etAge)
        val sSchool = view.findViewById<Spinner>(R.id.sSchool)
        val rbMale = view.findViewById<RadioButton>(R.id.rbMale)
        val rbFemale = view.findViewById<RadioButton>(R.id.rbFemale)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val rbMapping = mapOf(
                1 to rbMale,
                2 to rbFemale,
        )

        ArrayAdapter.createFromResource(
                context as Context,
                R.array.schools,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sSchool.adapter = adapter
        }

        sSchool.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d(TAG, "onItemSelected: $position")
                user.school = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: -1")
                user.school = -1
            }
        }

        avatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        userViewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            user = it

            etName.setText(user.name)
            etAbout.setText(user.about)

            if (user.age != null) {
                etAge.setText(user.age.toString())
            }

            if (user.school != -1) {
                sSchool.setSelection(user.school)
            }

            rbMapping[user.gender]?.let { k ->
                k.isChecked = true
            }

            storage.reference.child("avatars/${user.id}.png").downloadUrl.addOnSuccessListener { uri ->
                Glide.with(activity as Activity)
                        .load(uri)
                        .error(R.drawable.ic_account_64)
                        .into(avatar)
            }
        })

        rbMale.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Log.d(TAG, "onCheckedChangeListener: Male gender checked")
                user.gender = 1
            }
        }

        rbFemale.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Log.d(TAG, "onCheckedChangeListener: Female gender checked")
                user.gender = 2
            }
        }

        btnSave.setOnClickListener {
            // TODO: Implement loader.

            when {
                etName.text.isEmpty() -> {
                    etName.setErrorWithFocus(getText(R.string.name_is_invalid))
                }
                etAge.text.isEmpty() || etAge.text.toString().toInt() <= 0 -> {
                    etAge.setErrorWithFocus(getText(R.string.age_is_invalid))
                }
                user.gender == 0 -> {
                    rbMale.setErrorWithFocus(getText(R.string.gender_is_empty))
                }
                else -> {
                    user.name = etName.text.toString()
                    user.about = etAbout.text.toString()
                    user.age = etAge.text.toString().toInt()

                    user.id?.let { id ->
                        db.reference.child("users").child(id).setValue(user)

                        selectedImageUri?.let { uri ->
                            storage.reference.child("avatars/$id.png").putFile(uri).addOnCompleteListener {
                                userViewModel.postCurrentUser(user)
                            }
                        }
                    }

                    userViewModel.postCurrentUser(user)
                    Snackbar.make(it, getText(R.string.profile_updated), Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            selectedImageUri = imageUri
            avatar.setImageURI(selectedImageUri)
        }
    }

}