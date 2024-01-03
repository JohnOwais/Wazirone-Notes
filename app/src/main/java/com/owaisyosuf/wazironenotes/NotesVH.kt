package com.owaisyosuf.wazironenotes

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleText: TextView = itemView.findViewById(R.id.title)
    val taglineText: TextView = itemView.findViewById(R.id.tagline)
    val bodyText: TextView = itemView.findViewById(R.id.body)
    val pinned: ImageView = itemView.findViewById(R.id.pin)
}