package dev.abgeo.cupid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.abgeo.cupid.R
import dev.abgeo.cupid.helper.PersonCard

class PersonCardArrayAdapter(
        context: Context,
        resourceId: Int,
        items: List<PersonCard>
) : ArrayAdapter<PersonCard>(context, resourceId, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val personCard = getItem(position)!!

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.person_card, parent, false)
        }

        val tvTitle = view!!.findViewById<TextView>(R.id.tvTitle)
        val ivAvatar = view.findViewById<ImageView>(R.id.ivAvatar)

        tvTitle.text = personCard.title
        Firebase.storage.reference.child("avatars/${personCard.id}.png").downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                    .load(uri)
                    .error(R.drawable.ic_account_64)
                    .into(ivAvatar)
        }

        return view
    }
}