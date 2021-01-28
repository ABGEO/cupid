package dev.abgeo.cupid.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.abgeo.cupid.R

import dev.abgeo.cupid.helper.PersonCard

class MatchRecyclerViewAdapter(
    private val values: List<PersonCard>,
    private val context: Context,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<MatchRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_match_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        Firebase.storage.reference.child("avatars/${item.id}.png").downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .error(R.drawable.ic_account_64)
                .into(holder.ivAvatar)
        }

        holder.tvTitle.text = item.title

        holder.itemView.setOnClickListener {
            item.id?.let { id -> cellClickListener.onCellClickListener(id) }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    }
}

interface CellClickListener {
    fun onCellClickListener(userId: String)
}
