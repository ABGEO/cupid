package dev.abgeo.cupid.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dev.abgeo.cupid.R
import dev.abgeo.cupid.adapter.CellClickListener
import dev.abgeo.cupid.adapter.MatchRecyclerViewAdapter
import dev.abgeo.cupid.entity.User
import dev.abgeo.cupid.helper.PersonCard
import dev.abgeo.cupid.viewmodel.UserViewModel
import java.util.ArrayList

class MatchesFragment : Fragment(), CellClickListener {
    private val TAG = this::class.qualifiedName
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var tvIsEmptyMessage: TextView

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
        val view = inflater.inflate(R.layout.fragment_matches_list, container, false)
        tvIsEmptyMessage = view.findViewById(R.id.tvIsEmptyMessage)
        val rvMatches = view.findViewById<RecyclerView>(R.id.rvMatches)

        val matches: MutableList<PersonCard> = ArrayList()

        with(rvMatches) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MatchRecyclerViewAdapter(matches, context, this@MatchesFragment)
        }

        auth.currentUser?.uid?.let { uid ->
            db.reference.child("matches").child(uid).addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (
                        snapshot.exists()
                        && snapshot.getValue<Boolean>()!!
                    ) {
                        db.reference.child("matches").child(snapshot.key!!)
                            .addChildEventListener(
                                object :
                                    ChildEventListener {
                                    override fun onChildAdded(
                                        snapshot2: DataSnapshot,
                                        previousChildName: String?
                                    ) {
                                        if (
                                            snapshot2.exists()
                                            && uid == snapshot2.key
                                            && snapshot2.getValue<Boolean>()!!
                                        ) {

                                            Log.d(TAG, "2: ${snapshot.key}")

                                            db.reference.child("users").child(snapshot.key!!)
                                                .addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                        dataSnapshot.getValue<User>()?.let { u ->
                                                            matches.add(
                                                                PersonCard(
                                                                    u.id,
                                                                    u.name + (if (null != u.age) ", ${u.age}" else "")
                                                                )
                                                            )
                                                            tvIsEmptyMessage.visibility = View.INVISIBLE
                                                            rvMatches.adapter?.notifyDataSetChanged()
                                                        }
                                                    }

                                                    override fun onCancelled(databaseError: DatabaseError) {
                                                        Log.w(
                                                            TAG,
                                                            "onCancelled",
                                                            databaseError.toException()
                                                        )
                                                    }
                                                })
                                        }
                                    }

                                    override fun onChildChanged(snapshot2: DataSnapshot, previousChildName: String?) { }
                                    override fun onChildRemoved(snapshot2: DataSnapshot) {}
                                    override fun onChildMoved(snapshot2: DataSnapshot, previousChildName: String?) {}
                                    override fun onCancelled(error2: DatabaseError) {}
                                })


                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        return view
    }

    override fun onCellClickListener(userId: String) {
        db.reference.child("users").child(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<User>()?.let { u -> userViewModel.postPerson(u) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException())
            }
        })

        findNavController().navigate(R.id.action_navHomeFragment_to_navPersonProfileFragment)
    }

}