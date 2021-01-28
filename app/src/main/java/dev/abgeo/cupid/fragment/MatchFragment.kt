package dev.abgeo.cupid.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import dev.abgeo.cupid.R
import dev.abgeo.cupid.adapter.PersonCardArrayAdapter
import dev.abgeo.cupid.entity.User
import dev.abgeo.cupid.helper.PersonCard
import dev.abgeo.cupid.viewmodel.UserViewModel
import java.util.*

class MatchFragment : Fragment() {
    private val TAG = this::class.qualifiedName
    private lateinit var db: FirebaseDatabase
    private lateinit var cardsAdapter: PersonCardArrayAdapter
    private lateinit var currentUser: User
    private var personCards: MutableList<PersonCard> = ArrayList()

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_match, container, false)
        val flingContainer = view.findViewById<SwipeFlingAdapterView>(R.id.flingContainer)
        val ibReject = view.findViewById<ImageButton>(R.id.ibReject)
        val ibMatch = view.findViewById<ImageButton>(R.id.ibMatch)

        userViewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            currentUser = it

            getUsersToMatch()
        })

        cardsAdapter = PersonCardArrayAdapter(context as Context, R.layout.person_card, personCards)

        flingContainer.adapter = cardsAdapter
        flingContainer.setMinStackInAdapter(2)
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                personCards.removeAt(0)
                cardsAdapter.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                (dataObject as PersonCard).id?.let { writeMatchResult(it, false) }
            }

            override fun onRightCardExit(dataObject: Any) {
                (dataObject as PersonCard).id?.let { it ->
                    writeMatchResult(it, true)

                    currentUser.id?.let { id ->
                        db.reference.child("matches").child(it).child(id)
                            .addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        dataSnapshot.getValue<Boolean>()?.let { matched ->

                                            Log.d(TAG, "onDataChange: $matched")

                                            if (matched) {
                                                val snackBar = Snackbar.make(
                                                    view,
                                                    R.string.you_matched,
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snackBar.setAction(
                                                    R.string.go_to_profile,
                                                    VisitProfileListener(it)
                                                )
                                                snackBar.show()
                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.w(TAG, "onCancelled", databaseError.toException())
                                    }
                                })
                    }
                }
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                if (::currentUser.isInitialized) {
                    getUsersToMatch()
                }
            }

            override fun onScroll(scrollProgressPercent: Float) {}
        })

        flingContainer.setOnItemClickListener { _: Int, item: Any ->
            (item as PersonCard).id?.let { navigateToProfile(it) }
        }

        ibReject.setOnClickListener {
            flingContainer.topCardListener.selectLeft()
        }

        ibMatch.setOnClickListener {
            flingContainer.topCardListener.selectRight()
        }

        return view
    }

    private fun getUsersToMatch() {
        db.reference.child("users").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (
                        snapshot.exists()
                        && snapshot.key != currentUser.id
                        && (3 == currentUser.interestedIn || currentUser.interestedIn == snapshot.child("gender").value.toString().toInt())
                ) {
                    snapshot.getValue<User>()?.let { u ->
                        personCards.add(PersonCard(
                                u.id,
                                u.name + (if (null != u.age) ", ${u.age}" else "")
                        ))
                        cardsAdapter.notifyDataSetChanged()

                        Log.d(TAG, "getUsersToMatch: onChildAdded")
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun writeMatchResult(userId : String, result : Boolean) {
        Log.d(TAG, "writeMatchResult: $userId: $result")

        currentUser.id?.let {
            db.reference.child("matches").child(it).child(userId).setValue(result)
        }
    }

    private fun navigateToProfile(userId: String) {
        db.reference.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue<User>()?.let { u -> userViewModel.postPerson(u) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException())
            }
        })

        findNavController().navigate(R.id.action_navHomeFragment_to_navPersonProfileFragment)
    }

    inner class VisitProfileListener(private val userId: String) : View.OnClickListener {
        override fun onClick(v: View) {
            navigateToProfile(userId)
        }
    }
}