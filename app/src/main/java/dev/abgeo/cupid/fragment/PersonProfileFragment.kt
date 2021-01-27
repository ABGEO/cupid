package dev.abgeo.cupid.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dev.abgeo.cupid.R
import dev.abgeo.cupid.viewmodel.UserViewModel

class PersonProfileFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_person_profile, container, false)
        val avatar = view.findViewById<ImageView>(R.id.avatar)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSchool = view.findViewById<TextView>(R.id.tvSchool)
        val tvAbout = view.findViewById<TextView>(R.id.tvAbout)
        val schoolsArr = resources.getStringArray(R.array.schools)
        val schools : MutableMap<Int, String> = mutableMapOf()
        for (i in schoolsArr.indices) {
            schools[i] = schoolsArr[i]
        }

        userViewModel.personLiveData.observe(viewLifecycleOwner, {
            tvTitle.text = it.name + (if (null != it.age) ", ${it.age}" else "")

            if (schools.containsKey(it.school)) {
                tvSchool.text = schools.get(it.school)
            } else {
                tvSchool.visibility = View.GONE
            }

            if (!it.about.isNullOrEmpty()) {
                tvAbout.text = it.about
            } else {
                tvAbout.visibility = View.GONE
            }

            storage.reference.child("avatars/${it.id}.png").downloadUrl.addOnSuccessListener { uri ->
                Glide.with(activity as Activity)
                        .load(uri)
                        .error(R.drawable.ic_account_64)
                        .into(avatar)
            }
        })

        return view
    }
}
