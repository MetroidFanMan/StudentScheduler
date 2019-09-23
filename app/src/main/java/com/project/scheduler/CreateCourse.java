package com.project.scheduler;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreateCourse extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String id, title, sDate, eDate, termId, mentorIds, term, status, notes;
    private ArrayList<String> mentorList = new ArrayList<>();
    private ArrayList<String> selectMentor = new ArrayList<>();
    private ArrayList<String> assessList = new ArrayList<>();
    private ArrayList<String> selectAssess = new ArrayList<>();
    private String[] assessArrayList, mentorArrayList;
    private boolean[] checkedAssess, checkedMentor;
    private AlertDialog assessDialog, mentorDialog;

    private TextView startDate;
    private TextView endDate;
    private EditText titleInput, notesInput;
    private Spinner termSpinner, statusSpinner;

    private Calendar startCal;

    private ArrayAdapter<String> assessAdapter, mentorAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_course);

        //Initialize views
        startDate = findViewById(R.id.createCourseSDate);
        endDate = findViewById(R.id.createCourseEDate);
        TextView assessment = findViewById(R.id.createCourseAssessment);
        titleInput = findViewById(R.id.createCourseTitle);
        notesInput = findViewById(R.id.createCourseNotes);
        TextView mentors = findViewById(R.id.createCourseMentor);

        //Initialize spinners
        termSpinner = findViewById(R.id.createCourseTerm);
        statusSpinner = findViewById(R.id.createCourseStatus);

        //Populate lists for adapters
        List<String> termList = getTermList();
        List<String> statusList = getStatusList();

        //Initialize spinner adapters
        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, termList);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termSpinner.setOnItemSelectedListener(this);
        statusSpinner.setOnItemSelectedListener(this);
        termSpinner.setAdapter(termAdapter);
        statusSpinner.setAdapter(statusAdapter);

        //For editing existing courses
        //Set views with course info
        if (getIntent().hasExtra("id")){
            Intent intent = getIntent();
            id = intent.getStringExtra("id");

            getCourse();
            getTerm();
            getMentors();
            getAssessments();

            titleInput.setText(title);
            startDate.setText(sDate);
            endDate.setText(eDate);
            termSpinner.setSelection(termAdapter.getPosition(term));
            statusSpinner.setSelection(statusAdapter.getPosition(status));
            notesInput.setText(notes);
        }

        //Initialize list views
        final ListView assessListView = findViewById(R.id.createCourseAssessListView);
        final ListView mentorListView = findViewById(R.id.createCourseMentorListView);

        //Initialize adapters for list views
        assessAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assessList);
        mentorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mentorList);
        assessListView.setAdapter(assessAdapter);
        mentorListView.setAdapter(mentorAdapter);

//        //Populate lists for adapters
//        selectAssess = getAssessments();
//        selectMentor = getMentorList();

        //Initialize date pickers
        final DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                startCal = Calendar.getInstance();
                startCal.set(year, month, dayOfMonth);
                //Makes sure selected date is not in the past
                if (startCal.after(calendar)) {
                    month = month + 1;
                    String date = month + "/" + dayOfMonth + "/" + year;
                    startDate.setText(date);
                } else {
                    Toast.makeText(CreateCourse.this, "Please select a future date", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                //Makes sure selected date is not in the past
                if (selectedCalendar.after(calendar) && selectedCalendar.after(startCal)) {
                    month = month + 1;
                    String date = month + "/" + dayOfMonth + "/" + year;
                    endDate.setText(date);
                } else {
                    Toast.makeText(CreateCourse.this, "Please select a future date", Toast.LENGTH_SHORT).show();
                }
            }
        };


        //Set onClick listeners for date picker
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateCourse.this, android.R.style.Theme_InputMethod, startDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCal.add(Calendar.DAY_OF_MONTH, 1);
                int year = startCal.get(Calendar.YEAR);
                int month = startCal.get(Calendar.MONTH);
                int day = startCal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateCourse.this, android.R.style.Theme_InputMethod, endDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });


        //On click listener for assessment text view
        //Creates a multi choice dialog for selecting assessments
        assessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                selectAssess.clear();

                try {
                    selectAssess.add("Add a new assessment");
                    SQLiteDatabase db = new DBConnect(CreateCourse.this).getReadableDatabase();
                    Cursor cursor = db.rawQuery("SELECT * FROM assessments WHERE courseId = 0 OR courseId = " + id, null);
                    while (cursor.moveToNext()){
                        selectAssess.add(cursor.getString(cursor.getColumnIndex("assessmentTitle")));
                    }
                    cursor.close();
                    db.close();
                    assessArrayList = new String[selectAssess.size()];
                    checkedAssess = new boolean[selectAssess.size()];
                    for (int i = 0; i < selectAssess.size(); i++){
                        assessArrayList[i] = selectAssess.get(i);
                    }
                }
                catch (SQLException e) {
                    e.getMessage();
                }

                if (assessArrayList.length > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateCourse.this);
                    builder.setTitle("Select Assessments");
                    builder.setMultiChoiceItems(assessArrayList, checkedAssess, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    if (which == 0){
                                        dialog.dismiss();
                                        checkedAssess[which] = false;
                                        ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                                        startActivity(new Intent(CreateCourse.this, CreateAssessment.class));
                                    }
                                    checkedAssess[which] = isChecked;
                                }
                            });

                            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i = 0; i < assessArrayList.length; i++) {
                                        if (!assessList.contains(assessArrayList[i])) {
                                            if (checkedAssess[i]) {
                                                assessList.add(assessArrayList[i]);
                                                assessAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            if (!checkedAssess[i]) {
                                                assessList.remove(assessArrayList[i]);
                                                assessAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(CreateCourse.this, "Assessments added: " + assessList.size(), Toast.LENGTH_LONG).show();
                                }
                            });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.setNeutralButton("Clear All", null);

                    assessDialog = builder.create();
                    assessDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            Button neutralBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                            neutralBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        for (int i = 0; i < assessArrayList.length; i++) {
                                            checkedAssess[i] = false;
                                            ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                                            assessAdapter.notifyDataSetChanged();
                                        }
                                        assessList.clear();
                                        Toast.makeText(CreateCourse.this, "Assessments added: " + assessList.size(), Toast.LENGTH_LONG).show();
                                    } catch(IndexOutOfBoundsException ignored){

                                    }
                                }
                            });
                        }
                    });
                    assessDialog.show();

                    for (int i = 0; i < selectAssess.size(); i++){
                        if (assessList.contains(selectAssess.get(i))){
                            checkedAssess[i] = true;
                            assessDialog.getListView().setItemChecked(i, true);
                        }
                    }

                } else {
                    startActivity(new Intent(CreateCourse.this, CreateAssessment.class));
                }
            }
        });

        //On click listener for mentor text view
        //Creates a multi choice dialog for selecting mentors
        mentors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                selectMentor.clear();

                try {
                    selectMentor.add("Add a new mentor");
                    SQLiteDatabase db = new DBConnect(CreateCourse.this).getReadableDatabase();
                    Cursor cursor = db.query("mentors", null, null, null, null, null, "name ASC");
                    while (cursor.moveToNext()){
                        selectMentor.add(cursor.getString(cursor.getColumnIndex("name")));
                    }
                    cursor.close();
                    db.close();
                    mentorArrayList = new String[selectMentor.size()];
                    checkedMentor = new boolean[selectMentor.size()];
                    for (int i = 0; i < selectMentor.size(); i++){
                        mentorArrayList[i] = selectMentor.get(i);
                    }
                }
                catch (SQLException e) {
                    e.getMessage();
                }

                if (mentorArrayList.length > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateCourse.this);
                    builder.setTitle("Select Mentors");
                    builder.setMultiChoiceItems(mentorArrayList, checkedMentor, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (which == 0){
                                dialog.dismiss();
                                checkedMentor[which] = false;
                                ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                                startActivity(new Intent(CreateCourse.this, CreateMentor.class));
                            }
                            checkedMentor[which] = isChecked;
                        }
                    });

                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < mentorArrayList.length; i++) {
                                if (!mentorList.contains(mentorArrayList[i])) {
                                    if (checkedMentor[i]) {
                                        mentorList.add(mentorArrayList[i]);
                                        mentorAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    if (!checkedMentor[i]) {
                                        mentorList.remove(mentorArrayList[i]);
                                        mentorAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            dialog.dismiss();
                            Toast.makeText(CreateCourse.this, "Mentors added: " + mentorList.size(), Toast.LENGTH_LONG).show();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.setNeutralButton("Clear All", null);

                    mentorDialog = builder.create();
                    mentorDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            Button neutralBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                            neutralBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        for (int i = 0; i < mentorArrayList.length; i++) {
                                            checkedMentor[i] = false;
                                            ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                                            mentorAdapter.notifyDataSetChanged();
                                        }
                                        mentorList.clear();
                                        Toast.makeText(CreateCourse.this, "Mentors added: " + mentorList.size(), Toast.LENGTH_LONG).show();
                                    } catch(IndexOutOfBoundsException ignored){

                                    }
                                }
                            });
                        }
                    });
                    mentorDialog.show();

                    for (int i = 0; i < selectMentor.size(); i++){
                        if (mentorList.contains(selectMentor.get(i))){
                            checkedMentor[i] = true;
                            mentorDialog.getListView().setItemChecked(i, true);
                        }
                    }

                } else {
                    startActivity(new Intent(CreateCourse.this, CreateMentor.class));
                }
            }
        });

        assessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                assessList.remove(position);
                assessAdapter.notifyDataSetChanged();
            }
        });

        mentorListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(CreateCourse.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.mentor_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String title = menuItem.getTitle().toString();
                        switch (title) {
                            case "Remove":
                                mentorList.remove(position);
                                mentorAdapter.notifyDataSetChanged();
                                break;
                            case "Edit":
                                Intent editIntent = new Intent(CreateCourse.this, CreateMentor.class);
                                editIntent.putExtra("name", mentorAdapter.getItem(position));
                                startActivity(editIntent);
                                mentorList.remove(position);
                                mentorAdapter.notifyDataSetChanged();
                                break;
                            case "Delete":
                                try (SQLiteDatabase db = new DBConnect(CreateCourse.this).getWritableDatabase()) {
                                    int result = db.delete("mentors", "name = '" + mentorAdapter.getItem(position) + "'", null);
                                    if (result != 0) {
                                        Toast.makeText(CreateCourse.this, "Mentor successfully deleted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CreateCourse.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                    }
                                    mentorList.remove(position);
                                    mentorAdapter.notifyDataSetChanged();
                                } catch (SQLException e) {
                                    e.getMessage();
                                }
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return false;
            }
        });

        //Initialize buttons and set onClick methods
        Button save = findViewById(R.id.createCourseSaveBtn);
        Button cancel = findViewById(R.id.createCourseCancelBtn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getIntent().hasExtra("id")) {
                    writeToDatabase();
                } else {
                    updateCourse();
                    startActivity(new Intent(CreateCourse.this, CourseList.class));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!getIntent().hasExtra("id")){
                    finish();
                } else {
                    startActivity(new Intent(CreateCourse.this, CourseList.class));
                    finish();
                }
            }
        });
    }

    private List<String> getTermList() {
        List<String> termList = new ArrayList<>();
        try {
            termList.add("Select a term...");
            SQLiteDatabase db = new DBConnect(this).getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM terms", null);
            while (cursor.moveToNext()){
                termList.add(cursor.getString(1));
            }
            cursor.close();
            db.close();
        }
        catch (SQLException e){
            e.getMessage();
        }
        return termList;
    }

    private List<String> getStatusList() {
        List<String> statusList = new ArrayList<>();
        statusList.add("Select a status...");
        statusList.add("In Progress");
        statusList.add("Completed");
        statusList.add("Dropped");
        statusList.add("Planned");
        return statusList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final String string = (String) parent.getContentDescription();
        switch (string) {
            case "term":
                term = parent.getSelectedItem().toString();
                break;
            case "status":
                status = parent.getSelectedItem().toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String convertArrayToString(ArrayList<String> mentors) {
        String separator = ",";
        String stringList = "";
        for (int i = 0; i < mentors.size(); i++){
            stringList += mentors.get(i);
            if (i < mentors.size() - 1){
                stringList += separator;
            }
        }
        return stringList;
    }

    private ArrayList<String> convertStringToArray(String mentors){
        String separator = ",";
        String[] list = mentors.split(separator);
        return new ArrayList<>(Arrays.asList(list));
    }

    private void getCourse(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            Cursor cursor = db.query(
                    "courses",
                    null,
                    "courseId = ?",
                    new String[]{id},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                title = cursor.getString(cursor.getColumnIndex("courseTitle"));
                sDate = cursor.getString(cursor.getColumnIndex("startDate"));
                eDate = cursor.getString(cursor.getColumnIndex("endDate"));
                mentorIds = cursor.getString(cursor.getColumnIndex("mentorId"));
                termId = cursor.getString(cursor.getColumnIndex("termId"));
                status = cursor.getString(cursor.getColumnIndex("status"));
                notes = cursor.getString(cursor.getColumnIndex("notes"));
            }
            cursor.close();
        } catch (SQLException | IllegalArgumentException e) {
            Toast.makeText(this, "Something went wrong. getCourse():CreateCourse", Toast.LENGTH_SHORT).show();
            e.getMessage();
        }
    }

    private void getMentors(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            ArrayList<String> mentors = new ArrayList<>();
            String list = "";
            Cursor cursor = db.query(
                    "courses",
                    null,
                    "courseId = ?",
                    new String[]{id},
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                list = cursor.getString(cursor.getColumnIndex("mentorId"));
            }
            cursor.close();
            mentors = convertStringToArray(list);

            for (int i = 0; i < mentors.size(); i++){
                cursor = db.query("mentors", null, "mentorId = ?", new String[]{mentors.get(i)}, null, null, null);
                while (cursor.moveToNext()){
                    mentorList.add(cursor.getString(cursor.getColumnIndex("name")));
                }
                cursor.moveToFirst();
            }
            cursor.close();
        } catch (SQLException e){
            Toast.makeText(this, "Something went wrong. getMentor():CreateCourse", Toast.LENGTH_SHORT).show();
            e.getMessage();
        }
    }

    private void getTerm(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
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
                term = cursor.getString(cursor.getColumnIndex("termTitle"));
            }
            cursor.close();
        }
        catch (SQLException e){
            Toast.makeText(this, "Something went wrong. getTerm():CreateCourse", Toast.LENGTH_SHORT).show();
            e.getMessage();
        }
    }

    private void getAssessments(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            Cursor cursor = db.query(
                    "assessments",
                    null,
                    "courseId = ?",
                    new String[]{id},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                assessList.add(cursor.getString(cursor.getColumnIndex("assessmentTitle")));
            }
            cursor.close();
        } catch (SQLException | IllegalArgumentException e) {
            Toast.makeText(this, "Something went wrong. getAssessments():CreateCourse", Toast.LENGTH_SHORT).show();
            e.getMessage();
        }
    }

    private void writeToDatabase() {
        if (titleInput.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter a course name", Toast.LENGTH_SHORT).show();
        } else if (startDate.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
        } else if (endDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
        } else if (statusSpinner.getSelectedItem().toString().equals("Select a status...")) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
        } else if (mentorList.isEmpty()) {
            Toast.makeText(this, "Please select a mentor", Toast.LENGTH_SHORT).show();
        } else {
            title = titleInput.getText().toString();
            notes = notesInput.getText().toString();
            sDate = startDate.getText().toString();
            eDate = endDate.getText().toString();
            int termId = 0;
            ArrayList<String> mentors = new ArrayList<>();
            int courseId = 0;

            try (SQLiteDatabase db = new DBConnect(this).getWritableDatabase()){

                //Query to get the id of the matching termSpinner
                if (!term.equals("Select a term...")) {
                    Cursor termCursor = db.rawQuery("SELECT termId FROM terms WHERE termTitle = '" + term + "'", null);
                    while (termCursor.moveToNext()) {
                        termId = termCursor.getInt(0);
                    }
                    termCursor.close();
                }

                //Query to get the id of the matching mentor
                for (int i = 0; i < mentorList.size(); i++) {
                    Cursor mentorCursor = db.rawQuery("SELECT mentorId FROM mentors WHERE name = '" + mentorList.get(i) + "'", null);
                    while (mentorCursor.moveToNext()) {
                        mentors.add(mentorCursor.getString(mentorCursor.getColumnIndex("mentorId")));
                    }
                    mentorCursor.close();
                }

                mentorIds = convertArrayToString(mentors);

                ContentValues ct = new ContentValues();
                ct.put("courseTitle", title);
                ct.put("startDate", sDate);
                ct.put("endDate", eDate);
                ct.put("termId", termId);
                ct.put("mentorId", mentorIds);
                ct.put("status", status);
                ct.put("notes", notes);

                long result = db.insert("courses", null, ct);

                if (result != -1) {
                    Toast.makeText(this, "Course successfully added", Toast.LENGTH_SHORT).show();
                }

                //Query to get the id of the last inserted course
                //Could use the variable above (result) also, according to javadoc.
                Cursor courseCursor = db.rawQuery("SELECT last_insert_rowid()", null);
                while (courseCursor.moveToNext()) {
                    courseId = courseCursor.getInt(0);
                }
                courseCursor.close();

                ContentValues values = new ContentValues();
                values.put("courseId", courseId);

                for (int i = 0; i < assessList.size(); i++) {
                    int secondResult = db.update("assessments", values, "assessmentTitle = '" + assessList.get(i) + "'", null);
                }

                Calendar startCal = Calendar.getInstance();
                Calendar endCal = Calendar.getInstance();

                startCal.set(Calendar.YEAR, Integer.parseInt(sDate.substring(sDate.lastIndexOf("/") + 1)));
                startCal.set(Calendar.MONTH, Integer.parseInt(sDate.substring(0, sDate.indexOf("/"))));
                startCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sDate.substring(sDate.indexOf("/") + 1, sDate.lastIndexOf("/"))));

                endCal.set(Calendar.YEAR, Integer.parseInt(eDate.substring(eDate.lastIndexOf("/") + 1)));
                endCal.set(Calendar.MONTH, Integer.parseInt(eDate.substring(0, eDate.indexOf("/"))));
                endCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(eDate.substring(eDate.indexOf("/") + 1, eDate.lastIndexOf("/"))));

                Intent sIntent = new Intent(CreateCourse.this, NotificationReceiver.class);
                sIntent.putExtra("title", title);
                sIntent.putExtra("ending", " starts today!");

                Intent eIntent = new Intent(CreateCourse.this, NotificationReceiver.class);
                eIntent.putExtra("title", title);
                eIntent.putExtra("ending", " ends today!");

                PendingIntent sPendingIntent = PendingIntent.getBroadcast(CreateCourse.this, 0, sIntent, 0);
                PendingIntent ePendingIntent = PendingIntent.getBroadcast(CreateCourse.this, 1, eIntent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, startCal.getTimeInMillis(), sPendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, endCal.getTimeInMillis(), ePendingIntent);

                finish();
            } catch (SQLException e) {
                e.getMessage();
            }
        }
    }

    private void updateCourse(){
        if (titleInput.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter a course name", Toast.LENGTH_SHORT).show();
        } else if (startDate.getText().toString().equals("Select a Date")){
            Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
        } else if (endDate.getText().toString().equals("Select a Date")) {
            Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
        } else if (statusSpinner.getSelectedItem().toString().equals("Select a status...")) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
        } else if (mentorList.isEmpty()) {
            Toast.makeText(this, "Please select a mentor", Toast.LENGTH_SHORT).show();
        } else {
            title = titleInput.getText().toString();
            notes = notesInput.getText().toString();
            sDate = startDate.getText().toString();
            eDate = endDate.getText().toString();
            int termId = 0;
            ArrayList<String> list = new ArrayList<>();

            try (SQLiteDatabase db = new DBConnect(this).getWritableDatabase()) {

                //Query to get the id of the matching termSpinner
                if (!term.equals("Select a term...")) {
                    Cursor termCursor = db.rawQuery("SELECT termId FROM terms WHERE termTitle = '" + term + "'", null);
                    while (termCursor.moveToNext()) {
                        termId = termCursor.getInt(0);
                    }
                    termCursor.close();
                }

                for (int i = 0; i < mentorList.size(); i++){
                    Cursor mentorCursor = db.query("mentors", null, "name = ?", new String[]{mentorList.get(i)}, null, null, null);
                    while (mentorCursor.moveToNext()){
                        list.add(mentorCursor.getString(mentorCursor.getColumnIndex("mentorId")));
                    }
                    mentorCursor.moveToFirst();
                }

                mentorIds = convertArrayToString(list);

                ContentValues ct = new ContentValues();
                ct.put("courseTitle", title);
                ct.put("startDate", sDate);
                ct.put("endDate", eDate);
                ct.put("termId", termId);
                ct.put("mentorId", mentorIds);
                ct.put("status", status);
                ct.put("notes", notes);

                int result = db.update("courses", ct, "courseId = ?", new String[]{id});

                if (result > 0) {
                    Toast.makeText(CreateCourse.this, "Course successfully updated", Toast.LENGTH_SHORT).show();
                }

                ContentValues setValues = new ContentValues();
                setValues.put("courseId", Integer.valueOf(id));

                ContentValues undoValues = new ContentValues();
                undoValues.put("courseId", 0);

                int result1 = db.update("assessments", undoValues, "courseId = " + id, null);

                for (int i = 0; i < assessList.size(); i++) {
                    int result2 = db.update("assessments", setValues, "assessmentTitle = '" + assessList.get(i) + "'", null);
                }

                Calendar startCal = Calendar.getInstance();
                Calendar endCal = Calendar.getInstance();

                startCal.set(Calendar.YEAR, Integer.parseInt(sDate.substring(sDate.lastIndexOf("/") + 1)));
                startCal.set(Calendar.MONTH, Integer.parseInt(sDate.substring(0, sDate.indexOf("/"))));
                startCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sDate.substring(sDate.indexOf("/") + 1, sDate.lastIndexOf("/"))));

                endCal.set(Calendar.YEAR, Integer.parseInt(eDate.substring(eDate.lastIndexOf("/") + 1)));
                endCal.set(Calendar.MONTH, Integer.parseInt(eDate.substring(0, eDate.indexOf("/"))));
                endCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(eDate.substring(eDate.indexOf("/") + 1, eDate.lastIndexOf("/"))));

                Intent sIntent = new Intent(CreateCourse.this, NotificationReceiver.class);
                sIntent.putExtra("title", title);
                sIntent.putExtra("ending", " starts today!");

                Intent eIntent = new Intent(CreateCourse.this, NotificationReceiver.class);
                eIntent.putExtra("title", title);
                eIntent.putExtra("ending", " ends today!");

                PendingIntent sPendingIntent = PendingIntent.getBroadcast(CreateCourse.this, 0, sIntent, 0);
                PendingIntent ePendingIntent = PendingIntent.getBroadcast(CreateCourse.this, 1, eIntent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, startCal.getTimeInMillis(), sPendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, endCal.getTimeInMillis(), ePendingIntent);

                finish();
            } catch (SQLException e) {
                e.getMessage();
            }
        }
    }
}

