package com.example.swetha.addnotesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.HashSet;

public class NoteEditorActivity extends AppCompatActivity {

    EditText notesEditText;
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        notesEditText = findViewById(R.id.NoteEditText);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteid",-1);

        if(noteId != -1){
            notesEditText.setText(MainActivity.notesArray.get(noteId));
        }
        else{

            MainActivity.notesArray.add("");
            noteId = MainActivity.notesArray.size()-1;
            MainActivity.arrayAdapter.notifyDataSetChanged();

        }

        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                MainActivity.notesArray.set(noteId,String.valueOf(charSequence));
                MainActivity.arrayAdapter.notifyDataSetChanged();

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.swetha.addnotesapp", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet<String>(MainActivity.notesArray);
                sharedPreferences.edit().putStringSet("notesarray",set);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}
