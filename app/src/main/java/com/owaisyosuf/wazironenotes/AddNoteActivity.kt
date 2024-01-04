package com.owaisyosuf.wazironenotes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class AddNoteActivity : AppCompatActivity() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Notes")
    private lateinit var titleText: EditText
    private lateinit var pinNote: ImageView
    private lateinit var taglineText: EditText
    private lateinit var bodyText: EditText
    private lateinit var addNote: TextView
    private var key = "null"
    private var isPinned = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        val bundle = intent.extras
        titleText = findViewById(R.id.title_text)
        pinNote = findViewById(R.id.pin_note)
        taglineText = findViewById(R.id.tagline_text)
        bodyText = findViewById(R.id.body_text)
        addNote = findViewById(R.id.add_note_button)
        if (bundle != null) {
            key = bundle.getString("key").toString()
            titleText.setText(bundle.getString("title"))
            taglineText.setText(bundle.getString("tagline"))
            bodyText.setText(bundle.getString("body"))
            addNote.text = "UPDATE"
            if (bundle.getBoolean("isPinned")) {
                pinNote.setImageResource(R.drawable.pinned)
                isPinned = true
            }
        }
        addNote.setOnClickListener {
            titleText.setText(titleText.text.trim())
            taglineText.setText(taglineText.text.trim())
            bodyText.setText(bodyText.text.trim())
            if (titleText.length() < 3) {
                titleText.error = "Can't be less than 3 characters"
                titleText.requestFocus()
            } else if (taglineText.length() < 3) {
                taglineText.error = "Can't be less than 3 characters"
                taglineText.requestFocus()
            } else if (bodyText.length() < 5) {
                bodyText.error = "Can't be less than 5 characters"
                bodyText.requestFocus()
            } else {
                val note = Notes()
                note.title = titleText.text.toString()
                note.tagline = taglineText.text.toString()
                note.body = bodyText.text.toString()
                note.isPinned = isPinned
                val message = if (key == "null") {
                    databaseReference.push().setValue(note)
                    "Note Added Successfully"
                } else {
                    databaseReference.child(key).setValue(note)
                    "Note Updated Successfully"
                }
                val snackBar = Snackbar.make(
                    findViewById(android.R.id.content),
                    message,
                    Snackbar.LENGTH_SHORT
                )
                snackBar.setBackgroundTint(resources.getColor(R.color.green))
                snackBar.setTextColor(resources.getColor(R.color.white))
                snackBar.show()
                Handler(mainLooper).postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 2000)
            }
        }
        pinNote.setOnClickListener {
            isPinned = if (isPinned) {
                pinNote.setImageResource(R.drawable.un_pinned)
                false
            } else {
                pinNote.setImageResource(R.drawable.pinned)
                true
            }
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@AddNoteActivity, MainActivity::class.java))
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}