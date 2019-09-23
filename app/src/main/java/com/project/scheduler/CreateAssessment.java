package com.project.scheduler;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class CreateAssessment extends AppCompatActivity {

    private String title, dueDate;
    private EditText titleInput;
    private TextView dateInput;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_assessment);

        //Initialize views
        titleInput = findViewById(R.id.createAssessTitle);
        dateInput = findViewById(R.id.createAssessDueDate);
        Button save = findViewById(R.id.createAssessSaveBtn);
        Button cancel = findViewById(R.id.createAssessCancelBtn);

        //If assessment is passed from another activity
        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");

            getAssessment();

            titleInput.setText(title);
            dateInput.setText(dueDate);
        }


        //Date picker for assessment due date
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);

                if (selectedCalendar.after(calendar)) {
                    month = month + 1;
                    String date = month + "/" + dayOfMonth + "/" + year;
                    dateInput.setText(date);
                } else {
                    Toast.makeText(CreateAssessment.this, "Please select a future date", Toast.LENGTH_SHORT).show();
                }
            }
        };

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAssessment.this, android.R.style.Theme_InputMethod, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });


        //On click functions for buttons
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getIntent().hasExtra("id")) {
                    writeToDatabase();
                } else {
                    updateDatabase();
                    startActivity(new Intent(CreateAssessment.this, AssessmentList.class));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getAssessment() {
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()){
            Cursor cursor = db.query("assessments", null, "assessmentId = ?", new String[]{id}, null, null, null);
            while(cursor.moveToNext()){
                title = cursor.getString(cursor.getColumnIndex("assessmentTitle"));
                dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));
            }
        } catch (SQLException e){
            e.getMessage();
        }
    }

    private void writeToDatabase() {
            try (SQLiteDatabase db = new DBConnect(this).getWritableDatabase()){
                if (titleInput.getText().toString().trim().isEmpty()){
                    Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                } else if (dateInput.getText().toString().trim().isEmpty()){
                    Toast.makeText(this, "Please enter a due date", Toast.LENGTH_SHORT).show();
                } else {
                    title = titleInput.getText().toString();
                    dueDate = dateInput.getText().toString();

                    ContentValues ct = new ContentValues();
                    ct.put("courseId", 0);
                    ct.put("assessmentTitle", title);
                    ct.put("dueDate", dueDate);

                    long result = db.insert("assessments", null, ct);

                    if (result != -1) {
                        Toast.makeText(this, "Assessment was added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    Calendar dueCal = Calendar.getInstance();

                    dueCal.set(Calendar.YEAR, Integer.parseInt(dueDate.substring(dueDate.lastIndexOf("/") + 1)));
                    dueCal.set(Calendar.MONTH, Integer.parseInt(dueDate.substring(0, dueDate.indexOf("/"))));
                    dueCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dueDate.substring(dueDate.indexOf("/") + 1, dueDate.lastIndexOf("/"))));

                    Intent sIntent = new Intent(CreateAssessment.this, NotificationReceiver.class);
                    sIntent.putExtra("title", title);
                    sIntent.putExtra("ending", " is due today!");

                    PendingIntent sPendingIntent = PendingIntent.getBroadcast(CreateAssessment.this, 2, sIntent, 0);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, dueCal.getTimeInMillis(), sPendingIntent);

                    finish();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    private void updateDatabase() {
        try (SQLiteDatabase db = new DBConnect(this).getWritableDatabase()){
            if (titleInput.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            } else if (dateInput.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter a due date", Toast.LENGTH_SHORT).show();
            } else {
                title = titleInput.getText().toString();
                dueDate = dateInput.getText().toString();

                ContentValues ct = new ContentValues();
                ct.put("assessmentTitle", title);
                ct.put("dueDate", dueDate);

                int result = db.update("assessments", ct, "assessmentId = " + id, null);

                if (result > 0) {
                    Toast.makeText(this, "Assessment updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }

                Calendar dueCal = Calendar.getInstance();

                dueCal.set(Calendar.YEAR, Integer.parseInt(dueDate.substring(dueDate.lastIndexOf("/") + 1)));
                dueCal.set(Calendar.MONTH, Integer.parseInt(dueDate.substring(0, dueDate.indexOf("/"))));
                dueCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dueDate.substring(dueDate.indexOf("/") + 1, dueDate.lastIndexOf("/"))));

                Intent sIntent = new Intent(CreateAssessment.this, NotificationReceiver.class);
                sIntent.putExtra("title", title);
                sIntent.putExtra("ending", " is due today!");

                PendingIntent sPendingIntent = PendingIntent.getBroadcast(CreateAssessment.this, 2, sIntent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, dueCal.getTimeInMillis(), sPendingIntent);

                finish();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
