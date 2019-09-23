package com.project.scheduler;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateTerm extends AppCompatActivity {

    private String id, title, sDate, eDate;
    private ArrayList<String> courseList = new ArrayList<>();
    private ArrayList<String> selectCourses = new ArrayList<>();
    private String[] courseArrayList;
    private boolean[] checkedCourse;
    private AlertDialog courseDialog;

    private EditText titleInput;
    private TextView sDateInput;
    private TextView eDateInput;
    private ListView courseListView;

    private Calendar startCal;

    private ArrayAdapter<String> courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_term);

        //Initialize views
        titleInput = findViewById(R.id.createTermTitle);
        sDateInput = findViewById(R.id.createTermSDate);
        eDateInput = findViewById(R.id.createTermEDate);
        TextView addCourse = findViewById(R.id.createTermCourse);
        courseListView = findViewById(R.id.createTermCourseListView);
        Button saveBtn = findViewById(R.id.createTermSaveBtn);
        Button cancelBtn = findViewById(R.id.createTermCancelBtn);

        if (getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");

            getTerm();
            getCourses();

            titleInput.setText(title);
            sDateInput.setText(sDate);
            eDateInput.setText(eDate);
        }


        final DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                startCal = Calendar.getInstance();
                startCal.set(year, month, dayOfMonth);

                if (startCal.after(calendar)) {
                    month = month + 1;
                    String date = month + "/" + dayOfMonth + "/" + year;
                    sDateInput.setText(date);
                } else {
                    Toast.makeText(CreateTerm.this, "Please select a future date", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);

                if (selectedCalendar.after(calendar) && selectedCalendar.after(startCal)) {
                    month = month + 1;
                    String date = month + "/" + dayOfMonth + "/" + year;
                    eDateInput.setText(date);
                } else {
                    Toast.makeText(CreateTerm.this, "Please select a future date", Toast.LENGTH_SHORT).show();
                }
            }
        };

        sDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTerm.this, android.R.style.Theme_InputMethod, startDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        eDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCal.add(Calendar.DAY_OF_MONTH, 1);
                int year = startCal.get(Calendar.YEAR);
                int month = startCal.get(Calendar.MONTH);
                int day = startCal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTerm.this, android.R.style.Theme_InputMethod, endDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

//        try {
//            selectCourses.add("Add a new course");
//            SQLiteDatabase db = new DBConnect(this).getReadableDatabase();
//            Cursor courseCursor = db.rawQuery("SELECT courseTitle FROM courses WHERE termId = ?", new String[]{"0"});
//            while (courseCursor.moveToNext()){
//                selectCourses.add(courseCursor.getString(0));
//            }
//            courseCursor.close();
//            db.close();
//            courseArrayList = new String[selectCourses.size()];
//            checkedCourse = new boolean[selectCourses.size()];
//            for (int i = 0; i < selectCourses.size(); i++){
//                courseArrayList[i] = selectCourses.get(i);
//            }
//        }
//        catch (SQLException e){
//            e.printStackTrace();
//        }


        courseAdapter = new ArrayAdapter<>(CreateTerm.this, android.R.layout.simple_list_item_1, courseList);
        courseListView.setAdapter(courseAdapter);

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCourses.clear();

                try {
                    selectCourses.add("Add a new course");
                    SQLiteDatabase db = new DBConnect(CreateTerm.this).getReadableDatabase();
                    Cursor cursor = db.query("courses",
                            null,
                            "termId = 0 OR termId = " + id,
                            null,
                            null,
                            null,
                            "courseTitle ASC");
                    while (cursor.moveToNext()){
                        selectCourses.add(cursor.getString(cursor.getColumnIndex("courseTitle")));
                    }
                    cursor.close();
                    db.close();
                    courseArrayList = new String[selectCourses.size()];
                    checkedCourse = new boolean[selectCourses.size()];
                    for (int i = 0; i < selectCourses.size(); i++){
                        courseArrayList[i] = selectCourses.get(i);
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                if (courseArrayList.length > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateTerm.this);
                    builder.setTitle("Select Courses");
                    builder.setMultiChoiceItems(courseArrayList, checkedCourse, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (which == 0){
                                dialog.dismiss();
                                checkedCourse[which] = false;
                                ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                                startActivity(new Intent(CreateTerm.this, CreateCourse.class));
                            }
                            checkedCourse[which] = isChecked;
                        }
                    });

                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < courseArrayList.length; i++) {
                                if (!courseList.contains(courseArrayList[i])) {
                                    if (checkedCourse[i]) {
                                        courseList.add(courseArrayList[i]);
                                        courseAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    if (!checkedCourse[i]) {
                                        courseList.remove(courseArrayList[i]);
                                        courseAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.setNeutralButton("Clear All", null);

                    courseDialog = builder.create();
                    courseDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            Button neutralBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                            neutralBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        for (int i = 0; i < courseArrayList.length; i++) {
                                            checkedCourse[i] = false;
                                            ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                                            courseAdapter.notifyDataSetChanged();
                                        }
                                        courseList.clear();
                                    } catch(IndexOutOfBoundsException ignored){

                                    }
                                }
                            });
                        }
                    });
                    courseDialog.show();

                    for (int i = 0; i < selectCourses.size(); i++){
                        if (courseList.contains(selectCourses.get(i))){
                            checkedCourse[i] = true;
                            courseDialog.getListView().setItemChecked(i, true);
                        }
                    }

                } else {
                    startActivity(new Intent(CreateTerm.this, CreateCourse.class));
                }
            }
        });

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                courseList.remove(position);
                courseAdapter.notifyDataSetChanged();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getIntent().hasExtra("id")) {
                    writeToDatabase();
                } else {
                    updateTerm();
                    startActivity(new Intent(CreateTerm.this, TermList.class));
                }
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("id")){
                    startActivity(new Intent(CreateTerm.this, TermList.class));
                    finish();
                } else {
                    finish();
                }
            }
        });

    }

    private void getCourses() {
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()){
            Cursor cursor = db.query(
                    "courses",
                    null,
                    "termId = ?",
                    new String[]{id},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                courseList.add(cursor.getString(cursor.getColumnIndex("courseTitle")));
            }
            cursor.close();
        }
        catch (SQLException e){
            e.getMessage();
        }
    }

    private void getTerm() {
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()){
            Cursor cursor = db.query(
                    "terms",
                    null,
                    "termId = ?",
                    new String[]{id},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                title = cursor.getString(cursor.getColumnIndex("termTitle"));
                sDate = cursor.getString(cursor.getColumnIndex("startDate"));
                eDate = cursor.getString(cursor.getColumnIndex("endDate"));
            }
            cursor.close();
        }
        catch (SQLException e){
            e.getMessage();
        }

    }

    private void writeToDatabase(){
        try (SQLiteDatabase db = new DBConnect(this).getWritableDatabase()){
            if (titleInput.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter a term name", Toast.LENGTH_SHORT).show();
            } else if (sDateInput.getText().toString().equals("Select a Date")){
                Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
            } else if (eDateInput.getText().toString().equals("Select a Date")){
                Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
            } else {
                title = titleInput.getText().toString();
                sDate = sDateInput.getText().toString();
                eDate = eDateInput.getText().toString();

                int termId = 0;

                ContentValues ctTerm = new ContentValues();
                ctTerm.put("termTitle", title);
                ctTerm.put("startDate", sDate);
                ctTerm.put("endDate", eDate);

                long termResult = db.insert("terms", null, ctTerm);
                if (termResult != -1) {
                    Toast.makeText(CreateTerm.this, "Term added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateTerm.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                Cursor termCursor = db.rawQuery("SELECT last_insert_rowid()", null);
                while (termCursor.moveToNext()) {
                    termId = termCursor.getInt(0);
                }
                termCursor.close();

                ContentValues ctCourse = new ContentValues();
                ctCourse.put("termId", termId);

                for (int i = 0; i < courseList.size(); i++) {
                    int result = db.update("courses", ctCourse, "courseTitle = ?", new String[]{"'" + courseList.get(i) + "'"});
                }
            }
        }
        catch (SQLException e){
            e.getMessage();
        }
    }

    private void updateTerm(){
        try (SQLiteDatabase db = new DBConnect(this).getWritableDatabase()){
            if (titleInput.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter a term name", Toast.LENGTH_SHORT).show();
            } else if (sDateInput.getText().toString().equals("Select a Date")){
                Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
            } else if (eDateInput.getText().toString().equals("Select a Date")){
                Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
            } else {
                title = titleInput.getText().toString();
                sDate = sDateInput.getText().toString();
                eDate = eDateInput.getText().toString();

                ContentValues ctTerm = new ContentValues();
                ctTerm.put("termTitle", title);
                ctTerm.put("startDate", sDate);
                ctTerm.put("endDate", eDate);

                int result = db.update("terms", ctTerm, "termId = ?", new String[]{id});

                if (result > 0) {
                    Toast.makeText(this, "Course successfully updated", Toast.LENGTH_SHORT).show();
                }

                ContentValues undoCourse = new ContentValues();
                undoCourse.put("termId", 0);

                ContentValues setCourse = new ContentValues();
                setCourse.put("termId", id);

                int result1 = db.update("courses", undoCourse, "termId = " + id, null);

                for (int i = 0; i < courseList.size(); i++) {
                    int result2 = db.update("courses", setCourse, "courseTitle = '" + courseList.get(i) + "'", null);
                }
            }
        }
        catch (SQLException e){
            e.getMessage();
        }
    }
}
