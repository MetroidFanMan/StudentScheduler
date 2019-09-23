package com.project.scheduler;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShowTerm extends AppCompatActivity {

    private ArrayAdapter<String> courseAdapter;
    private ArrayList<String> courses = new ArrayList<>();

    private TextView title, startDate, endDate;
    private ListView listView;

    private String idInsert, titleInsert, startDateInsert, endDateInsert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ext_term_layout);

        title = findViewById(R.id.showTermTitle);
        startDate = findViewById(R.id.showTermStartDate);
        endDate = findViewById(R.id.showTermEndDate);

        if (getIntent().hasExtra("id")) {
            idInsert = getIntent().getStringExtra("id");

            getTerm();
            getCourses();

            title.setText(titleInsert);
            startDate.setText(startDateInsert);
            endDate.setText(endDateInsert);
        }

        listView = findViewById(R.id.showCourseListView);
        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        listView.setAdapter(courseAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = courseAdapter.getItem(position);
                try (SQLiteDatabase db = new DBConnect(ShowTerm.this).getReadableDatabase()){
                    Cursor cursor = db.rawQuery("SELECT courseId FROM courses WHERE courseTitle = '" + selected + "'", null);
                    if (cursor.moveToFirst()){
                        Intent intent = new Intent(ShowTerm.this, ShowCourse.class);
                        intent.putExtra("id", cursor.getString(0));
                        startActivity(intent);
                    }
                    cursor.close();
                } catch (SQLException e){
                    e.getMessage();
                }
            }
        });

        title.setText(titleInsert);
        startDate.setText(startDateInsert);
        endDate.setText(endDateInsert);
    }

    private void getTerm() {
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()){
            Cursor cursor = db.query(
                    "terms",
                    null,
                    "termId = ?",
                    new String[]{idInsert},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                titleInsert = cursor.getString(cursor.getColumnIndex("termTitle"));
                startDateInsert = cursor.getString(cursor.getColumnIndex("startDate"));
                endDateInsert = cursor.getString(cursor.getColumnIndex("endDate"));
            }
            cursor.close();
        }
        catch (SQLException e){
            Toast.makeText(this, "Something went wrong. getTerm(): ShowTerm.class", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCourses(){
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()){
            Cursor cursor = db.query(
                    "courses",
                    null,
                    "termId = ?",
                    new String[]{idInsert},
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                courses.add(cursor.getString(cursor.getColumnIndex("courseTitle")));
            }
            cursor.close();
        }
        catch (SQLException e){
            Toast.makeText(this, "Something went wrong. getCourse(): ShowTerm.class", Toast.LENGTH_SHORT).show();
        }
    }
}