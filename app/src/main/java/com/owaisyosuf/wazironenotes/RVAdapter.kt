package com.owaisyosuf.wazironenotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class RVAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = ArrayList<Notes>()
    private var keys = ArrayList<String>()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Notes")

    fun post(listItems: ArrayList<Notes>, noteKeys: ArrayList<String>) {
        list.addAll(listItems)
        keys.addAll(noteKeys)
    }

    fun clear() {
        list.clear()
        keys.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false)
        return NotesVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as NotesVH
        val note = list[position]
        val key = keys[position]
        vh.titleText.text = note.title
        vh.taglineText.text = note.tagline
        vh.bodyText.text = note.body
        if (note.isPinned) {
            vh.pinned.setImageResource(R.drawable.pinned)
        } else {
            vh.pinned.setImageResource(R.drawable.un_pinned)
        }
        vh.pinned.setOnClickListener {
            note.isPinned = !note.isPinned
            databaseReference.child(key).setValue(note)
            if (note.isPinned) {
                vh.pinned.setImageResource(R.drawable.pinned)
            } else {
                vh.pinned.setImageResource(R.drawable.un_pinned)
            }
        }
        vh.cardLayout.setOnClickListener {
            val intent = Intent(context, AddNoteActivity::class.java)
            val bundle = Bundle()
            bundle.putString("key", key)
            bundle.putString("title", note.title)
            bundle.putString("tagline", note.tagline)
            bundle.putString("body", note.body)
            bundle.putBoolean("isPinned", note.isPinned)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
