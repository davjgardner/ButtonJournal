package com.davjgardner.buttonjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {

    public static final String TAG = "AddEventActivity";

    public static final String REPLY_DATE = "com.davjgardner.buttonjournal.REPLY_DATE";
    public static final String REPLY_TIME = "com.davjgardner.buttonjournal.REPLY_TIME";
    public static final String REPLY_TIMESTAMP = "com.davjgardner.buttonjournal.REPLY_TIMESTAMP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        EditText dateBox = findViewById(R.id.editTextDate);
        EditText timeBox = findViewById(R.id.editTextTime);
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        // TODO make these buttons
        dateBox.setOnClickListener(dl -> {
            DatePickerDialog datePicker = new DatePickerDialog(AddEventActivity.this, 0);
            datePicker.getDatePicker().init(y, m, d, (dp, year, month, day) -> {
                cal.set(year, month, day);
            });
            datePicker.setTitle(R.string.title_select_date);
            datePicker.show();
        });

        timeBox.setOnClickListener(l -> {
            TimePickerDialog timePicker = new TimePickerDialog(AddEventActivity.this, 0, (tp, hr, min) -> {
                cal.set(Calendar.HOUR, hr);
                cal.set(Calendar.MINUTE, min);
            }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), false);
            timePicker.show();
        });

        final Button saveButton = findViewById(R.id.add_event_save);
        saveButton.setOnClickListener(l -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(dateBox.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String date = dateBox.getText().toString();
                String time = timeBox.getText().toString();
                // TODO if time is empty, use some sane default
                replyIntent.putExtra(REPLY_DATE, date);
                replyIntent.putExtra(REPLY_TIME, time);
                Log.d(TAG, "Picked time " + cal.toString());
            }
            finish();
        });

    }
}