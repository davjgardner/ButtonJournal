package com.davjgardner.buttonjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    public static final String TAG = "AddEventActivity";

    public static final String REPLY_DATE = "com.davjgardner.buttonjournal.REPLY_DATE";
    public static final String REPLY_TIME = "com.davjgardner.buttonjournal.REPLY_TIME";
    public static final String REPLY_TIMESTAMP = "com.davjgardner.buttonjournal.REPLY_TIMESTAMP";

    Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        TextView title = findViewById(R.id.tv_add_event_title);
        title.setText(getString(R.string.title_new_event, getIntent().getStringExtra(ViewEventActivity.EVENT_TYPE_NAME)));

        date = Calendar.getInstance();

        Button dateButton = findViewById(R.id.btn_pick_date);
        Button timeButton = findViewById(R.id.btn_pick_time);

        TextView dateField = findViewById(R.id.tv_date_text);
        TextView timeField = findViewById(R.id.tv_time_text);
        dateField.setText(DateFormat.getDateFormat(this).format(date.getTime()));
        timeField.setText(DateFormat.getTimeFormat(this).format(date.getTime()));

        dateButton.setOnClickListener(l -> {
            DatePickerDialog datePicker = new DatePickerDialog(AddEventActivity.this, 0);
            datePicker.getDatePicker().init(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH), (dp, year, month, day) -> {
                date.set(year, month, day);
                dateField.setText(DateFormat.getDateFormat(this).format(date.getTime()));
            });
            datePicker.setTitle(R.string.title_select_date);
            datePicker.show();
        });

        timeButton.setOnClickListener(l -> {
            TimePickerDialog timePicker = new TimePickerDialog(AddEventActivity.this, 0, (tp, hour, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hour);
                date.set(Calendar.MINUTE, minute);
                timeField.setText(DateFormat.getTimeFormat(this).format(date.getTime()));
                Log.d(TAG, "new hr: " + hour);
            }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false);
            timePicker.show();
        });

        final Button saveButton = findViewById(R.id.add_event_save);
        saveButton.setOnClickListener(l -> {
            Intent replyIntent = new Intent();
            replyIntent.putExtra(REPLY_TIMESTAMP, this.date.getTimeInMillis());
            Log.d(TAG, "Picked time " + this.date.toString());
            setResult(RESULT_OK, replyIntent);
            finish();
        });

    }
}