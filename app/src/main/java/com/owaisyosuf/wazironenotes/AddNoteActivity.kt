package com.owaisyosuf.wazironenotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AddNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        val bundle = intent.extras
        if (bundle != null) {
            // here will be the code to use bundle contents
        }
    }
}