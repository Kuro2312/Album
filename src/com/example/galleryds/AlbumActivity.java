package com.example.galleryds;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class AlbumActivity extends Activity {

    private Button btnOK;
    private EditText etName;
    private EditText etEvent;
    private EditText etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        etName = (EditText) findViewById(R.id.etName);
        etEvent = (EditText) findViewById(R.id.etEvent);
        etNote = (EditText) findViewById(R.id.etNote);

        btnOK = (Button) findViewById(R.id.button);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = etName.getText().toString();
                if (s.equals(""))
                    Toast.makeText(AlbumActivity.this, "Name must not be empty", Toast.LENGTH_SHORT).show();
                else if (checkAlbumExistence(s))
                    Toast.makeText(AlbumActivity.this, "This name has already existed", Toast.LENGTH_SHORT).show();
                else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("Name", etName.getText().toString());
                    resultIntent.putExtra("Event", etEvent.getText().toString());
                    resultIntent.putExtra("Note", etNote.getText().toString());
                    setResult(Activity.RESULT_OK, resultIntent);

                    finish();
                }
            }
        });


        String name = getIntent().getStringExtra("Name");
        if (name != null) {
            String event = getIntent().getStringExtra("Event");
            String note = getIntent().getStringExtra("Note");

            etName.setText(name);
            etEvent.setText(event);
            etNote.setText(note);
        }
    }

    private boolean checkAlbumExistence(String name) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File folder = new File(path.getAbsolutePath() + File.separator + name);
        return folder.exists();
    }
}
