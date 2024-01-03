package com.owaisyosuf.wazironenotes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RVAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = ArrayList<Notes>()

    fun post(listItems: ArrayList<Notes>) {
        list.addAll(listItems)
    }

    fun clear() {
        list.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false)
        return NotesVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as NotesVH
        val note = list[position]
        vh.titleText.text = note.title
        vh.taglineText.text = note.tagline
        vh.bodyText.text = note.body
        if (note.isPinned) {
            vh.pinned.setImageResource(R.drawable.pinned)
        } else {
            vh.pinned.setImageResource(R.drawable.un_pinned)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
