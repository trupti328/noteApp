package com.trupti.app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnAddNote: Button
    private lateinit var btnShowNotes: Button
    private lateinit var tvNotes: TextView

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")

    fun addNote(title: String, description: String) {
        val id = notesCollection.document().id // Generate unique ID
        val note = hashMapOf(
            "id" to id,
            "title" to title,
            "description" to description
        )
        Log.d("Firestore", "Adding Note: Title=$title, Description=$description")

        notesCollection.document(id).set(note)
            .addOnSuccessListener {
                Log.d("Firestore", "Note added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding note", e)
            }
    }

    fun getNotes() {
        notesCollection.get()
            .addOnSuccessListener { documents ->
                val notesList = StringBuilder()
                for (document in documents) {
                    val title = document.getString("title") ?: "No Title"
                    val description = document.getString("description") ?: "No Description"
                    Log.d("Firestore", "Fetched Note: Title=$title, Description=$description")
                    notesList.append("Title: $title\nDescription: $description\n\n")
                }
                tvNotes.text = notesList.toString()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving notes", e)
            }
    }


    fun deleteNote(noteId: String) {
        notesCollection.document(noteId).delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Note deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting note", e)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        btnAddNote = findViewById(R.id.btnAddNote)
        btnShowNotes = findViewById(R.id.btnShowNotes)
        tvNotes = findViewById(R.id.tvNotes)

        btnAddNote.setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                addNote(title, description)
            }
        }


        btnShowNotes.setOnClickListener {
            getNotes() // Fetch and display notes when button is clicked
        }


    }
}