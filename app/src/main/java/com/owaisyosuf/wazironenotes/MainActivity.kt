package com.owaisyosuf.wazironenotes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class MainActivity : AppCompatActivity() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Notes")
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: RVAdapter
    private lateinit var emptyNotepad: TextView
    private var key = "null"
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.loadingProgressBar)
        emptyNotepad = findViewById(R.id.empty_notepad)
        val recyclerView: RecyclerView = findViewById(R.id.rv)
        val addNote: ExtendedFloatingActionButton = findViewById(R.id.add_note)
        recyclerView.setHasFixedSize(true)
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = manager
        adapter = RVAdapter(this, this)
        recyclerView.adapter = adapter
        loadNotes()
        addNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
            finish()
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
    }

    private fun loadNotes() {
        progressBar.visibility = View.VISIBLE
        var isEmpty = false
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
                        key = dataSnapshot.key.toString()
                    }
                }
                adapter.post(notes, noteKeys)
                adapter.notifyDataSetChanged()
                isLoading = false
                progressBar.visibility = View.GONE
                if (isEmpty)
                    emptyNotepad.visibility = View.VISIBLE
                else
                    emptyNotepad.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }
}