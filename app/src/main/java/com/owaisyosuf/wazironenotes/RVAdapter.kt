package com.owaisyosuf.wazironenotes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class RVAdapter(private val context: Context, private val activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = ArrayList<Notes>()
    private var keys = ArrayList<String>()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Notes")

    fun post(listItems: ArrayList<Notes>, noteKeys: ArrayList<String>) {
        list.addAll(listItems)
        keys.addAll(noteKeys)
        sort(list, keys)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sort(list: ArrayList<Notes>, keys: ArrayList<String>) {
        val combinedList = list.zip(keys)
        val sortedList = combinedList.sortedByDescending { it.first.isPinned }
        list.clear()
        keys.clear()
        for ((note, key) in sortedList) {
            list.add(note)
            keys.add(key)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false)
        return NotesVH(view)
    }

    @SuppressLint("NotifyDataSetChanged")
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
            sort(list, keys)
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
            activity.finish()
        }
        vh.cardLayout.setOnLongClickListener {
            val builder = AlertDialog.Builder(
                context,
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background
            )
            builder.setTitle("Sure to delete?")
            builder.setPositiveButton("Yes") { _, _ ->
                databaseReference.child(key).removeValue()
                val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    "Note Deleted",
                    Snackbar.LENGTH_SHORT
                )
                snackBar.setBackgroundTint(Color.parseColor("#F44336"))
                snackBar.setTextColor(Color.parseColor("#FFFFFFFF"))
                snackBar.show()
                list.remove(note)
                keys.remove(key)
                notifyDataSetChanged()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val alert: AlertDialog = builder.create()
            alert.show()
            vh.outerLayout.setBackgroundColor(Color.parseColor("#F44336"))
            vh.titleText.setTextColor(Color.parseColor("#F0F0F0"))
            vh.taglineText.setTextColor(Color.parseColor("#F0F0F0"))
            vh.bodyText.setTextColor(Color.parseColor("#F0F0F0"))
            alert.setOnDismissListener {
                vh.outerLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
                vh.titleText.setTextColor(Color.parseColor("#202020"))
                vh.taglineText.setTextColor(Color.parseColor("#646464"))
                vh.bodyText.setTextColor(Color.parseColor("#080808"))
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
