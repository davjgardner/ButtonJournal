package com.davjgardner.buttonjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

public class AddEventTypeActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.davjgardner.buttonjournal.REPLY";

    private EditText textBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_type);
        textBox = findViewById(R.id.addEventTypeName);

        final Button saveButton = findViewById(R.id.addEventTypeCreate);
        saveButton.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(textBox.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String name = textBox.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, name);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}