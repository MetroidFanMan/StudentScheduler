package com.project.scheduler;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ShowCourse extends AppCompatActivity {

    final static int SEND_SMS_PERMISSION_REQUEST = 0;

    private ArrayList<String> assessList = new ArrayList<>();
    private ArrayList<String> mentorList = new ArrayList<>();

    private TextView title, startDate, endDate, term, status, notes;
    private ListView assessListView, mentorListView;
    private ImageButton share;
    private EditText phone;
    private AlertDialog smsDialog;

    private int termId;
    private String idInsert, titleInsert, startDateInsert, endDateInsert, termInsert, statusInsert, mentorIds, notesInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ext_course_layout);

        if (getIntent().hasExtra("id")) {
            Intent intent = getIntent();
            idInsert = intent.getStringExtra("id");
        }

        getCourse();
        getTerm();
        getMentors();
        getAssessments();

        title = findViewById(R.id.showCourseTitle);
        startDate = findViewById(R.id.showCourseStartDate);
        endDate = findViewById(R.id.showCourseEndDate);
        term = findViewById(R.id.showCourseTerm);
        status = findViewById(R.id.showCourseStatus);
        notes = findViewById(R.id.showCourseNotes);
        share = findViewById(R.id.shareBtn);
        share.setEnabled(false);

        if (checkPermission()) {
            share.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST);
        }

        LayoutInflater layoutInflater = LayoutInflater.from(ShowCourse.this);
        final View smsView = layoutInflater.inflate(R.layout.share_layout, null, false);
        phone = smsView.findViewById(R.id.phoneNumber);

        assessListView = findViewById(R.id.showCourseAssessListView);
        mentorListView = findViewById(R.id.showCourseMentorListView);

        ArrayAdapter<String> assessAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assessList);
        ArrayAdapter<String> mentorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mentorList);

        assessAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mentorAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        assessListView.setAdapter(assessAdapter);
        mentorListView.setAdapter(mentorAdapter);

        title.setText(titleInsert);
        startDate.setText(startDateInsert);
        endDate.setText(endDateInsert);
        term.setText(termInsert);
        status.setText(statusInsert);
        notes.setText(notesInsert);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!notes.getText().toString().trim().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowCourse.this);
                    builder.setView(smsView);
                    builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String number = PhoneNumberUtils.formatNumber(phone.getText().toString(), Locale.getDefault().getCountry());
                            sendMsg(number);
                            Toast.makeText(ShowCourse.this, "Message sent!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            ((ViewGroup)smsView.getParent()).removeView(smsView);
                            phone.setText("");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ((ViewGroup)smsView.getParent()).removeView(smsView);
                            phone.setText("");
                        }
                    });
                    smsDialog = builder.create();
                    smsDialog.setCanceledOnTouchOutside(false);
                    smsDialog.show();
                } else {
                    Toast.makeText(ShowCourse.this, "No notes to send", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMsg(String number) {
        if (checkPermission()){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, "Notes from " + title.getText().toString() + ":\n" + notes.getText().toString(), null, null);
        }
    }

    private boolean checkPermission(){
        int granted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        return granted == PackageManager.PERMISSION_GRANTED;
    }

    private void getCourse(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            Cursor cursor = db.query(
                    "courses",
                    null,
                    "courseId = ?",
                    new String[]{idInsert},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                titleInsert = cursor.getString(cursor.getColumnIndex("courseTitle"));
                startDateInsert = cursor.getString(cursor.getColumnIndex("startDate"));
                endDateInsert = cursor.getString(cursor.getColumnIndex("endDate"));
                mentorIds = cursor.getString(cursor.getColumnIndex("mentorId"));
                termId = cursor.getInt(cursor.getColumnIndex("termId"));
                statusInsert = cursor.getString(cursor.getColumnIndex("status"));
                notesInsert = cursor.getString(cursor.getColumnIndex("notes"));
            }
            cursor.close();
        } catch (SQLException e) {
            Toast.makeText(this, "Something went wrong. getCourse():ShowCourse", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<String> convertStringToArray(String mentors){
        String separator = ",";
        String[] list = mentors.split(separator);
        return new ArrayList<>(Arrays.asList(list));
    }

    private void getMentors(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            if (!mentorIds.isEmpty()) {
                ArrayList<String> list = convertStringToArray(mentorIds);
                Cursor cursor = db.rawQuery("SELECT * FROM mentors", null);
                for (int i = 0; i < list.size(); i++){
                    while(cursor.moveToNext()){
                        if (cursor.getString(cursor.getColumnIndex("mentorId")).equals(list.get(i))){
                            mentorList.add(cursor.getString(cursor.getColumnIndex("name")) + "\n" +
                                    cursor.getString(cursor.getColumnIndex("email")) + "\n" +
                                    cursor.getString(cursor.getColumnIndex("phone")));
                        }
                    }
                    cursor.moveToFirst();
                }
                cursor.close();
            }
        } catch (SQLException e) {
            Toast.makeText(this, "Something went wrong. getMentor():ShowCourse", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTerm(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            if (termId != 0) {
                Cursor cursor = db.query(
                        "terms",
                        null,
                        "termId = ?",
                        new String[]{String.valueOf(termId)},
                        null,
                        null,
                        null
                );
                if (cursor.moveToFirst()) {
                    termInsert = cursor.getString(cursor.getColumnIndex("termTitle"));
                }
                cursor.close();
            } else {
                termInsert = "Term not set";
            }
        } catch (SQLException e) {
            Toast.makeText(this, "Something went wrong. getTerm():ShowCourse", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAssessments(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            Cursor cursor = db.query(
                    "assessments",
                    null,
                    "courseId = ?",
                    new String[]{idInsert},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                assessList.add(cursor.getString(cursor.getColumnIndex("assessmentTitle")) + "\nDue: " + cursor.getString(cursor.getColumnIndex("dueDate")));
            }
            cursor.close();
        } catch (SQLException e) {
            Toast.makeText(this, "Something went wrong. getAssessments():ShowCourse", Toast.LENGTH_SHORT).show();
        }
    }
}
