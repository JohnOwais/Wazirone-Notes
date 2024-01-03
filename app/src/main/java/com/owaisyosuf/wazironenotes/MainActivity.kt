package com.owaisyosuf.wazironenotes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class MainActivity : AppCompatActivity() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Notes")
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: RVAdapter
    private lateinit var emptyNotepad: TextView
    private var key: String = "null"
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeRefreshLayout = findViewById(R.id.swipe)
        emptyNotepad = findViewById(R.id.empty_notepad)
        val recyclerView: RecyclerView = findViewById(R.id.rv)
        val addNote: ExtendedFloatingActionButton = findViewById(R.id.add_note)
        recyclerView.setHasFixedSize(true)
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = manager
        adapter = RVAdapter(this)
        recyclerView.adapter = adapter
        loadNotes()
        addNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    val staggeredGridLayoutManager =
                        recyclerView.layoutManager as StaggeredGridLayoutManager
                    val visibleItemCount = staggeredGridLayoutManager.childCount
                    val totalItemCount = staggeredGridLayoutManager.itemCount
                    val firstVisibleItems = IntArray(staggeredGridLayoutManager.spanCount)
                    staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems)
                    val firstVisibleItem = firstVisibleItems.min()
                    val threshold = 6
                    if (visibleItemCount + firstVisibleItem >= totalItemCount - threshold && !isLoading) {
                        isLoading = true
                        loadNotes()
                    }
                }
            }
        })
        swipeRefreshLayout.setOnRefreshListener {
            key = "null"
            adapter.clear()
            loadNotes()
        }
    }

    private fun loadNotes() {
        var isEmpty = false
        swipeRefreshLayout.isRefreshing = true
        val database: Query
        if (key == "null") {
            isEmpty = true
            database = databaseReference.orderByKey().limitToFirst(6)
        } else {
            database = databaseReference.orderByKey().startAfter(key)
                .limitToFirst(6)
        }
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = ArrayList<Notes>()
                val noteKeys = ArrayList<String>()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val note = dataSnapshot.getValue<Notes>()
                    if (note != null) {
                        notes.add(note)
                        noteKeys.add(dataSnapshot.key.toString())
                        isEmpty = false
                    }
                    key = dataSnapshot.key.toString()
                }
                adapter.post(notes, noteKeys)
                adapter.notifyDataSetChanged()
                isLoading = false
                swipeRefreshLayout.isRefreshing = false
                if (isEmpty)
                    emptyNotepad.visibility = View.VISIBLE
                else
                    emptyNotepad.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}