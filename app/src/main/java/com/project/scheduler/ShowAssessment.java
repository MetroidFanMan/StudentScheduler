package com.project.scheduler;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ShowAssessment extends AppCompatActivity {

    TextView title, dueDate, course, term;

    String idInsert, titleInsert, dueDateInsert, courseInsert, termInsert;
    int termId, courseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ext_assessment_layout);

        Intent intent = getIntent();
        idInsert = intent.getStringExtra("id");

        title = findViewById(R.id.showAssessmentTitle);
        dueDate = findViewById(R.id.showAssessmentDueDate);
        course = findViewById(R.id.showAssessmentCourse);
        term = findViewById(R.id.showAssessmentTerm);

        getAssessment();
        getCourse();
        getTerm();

        title.setText(titleInsert);
        dueDate.setText(dueDateInsert);
        if (courseId == 0){
            String fill = "No course";
            course.setText(fill);
        } else {
            course.setText(courseInsert);
        }
        if (termId == 0){
            String fill = "No term";
            term.setText(fill);
        } else {
            term.setText(termInsert);
        }
    }

    private void getTerm() {
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            Cursor cursor = db.query("terms", null, "termId = ?", new String[]{"" + termId}, null, null, null);
            while (cursor.moveToNext()) {
                termInsert = cursor.getString(cursor.getColumnIndex("termTitle"));
            }
            cursor.close();
        }
        catch (SQLException e){
            Toast.makeText(ShowAssessment.this, "Something went wrong. getTerm(): ShowAssessment.class", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCourse() {
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            Cursor cursor = db.query("courses", null, "courseId = ?", new String[]{"" + courseId}, null, null, null);
            while (cursor.moveToNext()) {
                courseInsert = cursor.getString(cursor.getColumnIndex("courseTitle"));
                termId = cursor.getInt(cursor.getColumnIndex("termId"));
            }
            cursor.close();
        }
        catch (SQLException e){
            Toast.makeText(ShowAssessment.this, "Something went wrong. getCourse(): ShowAssessment.class", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAssessment() {
        try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()) {
            Cursor cursor = db.query("assessments", null, "assessmentId = ?", new String[]{idInsert}, null, null, null);
            while (cursor.moveToNext()) {
                titleInsert = cursor.getString(cursor.getColumnIndex("assessmentTitle"));
                dueDateInsert = cursor.getString(cursor.getColumnIndex("dueDate"));
                courseId = cursor.getInt(cursor.getColumnIndex("courseId"));
            }
            cursor.close();
        }
        catch (SQLException e){
            Toast.makeText(ShowAssessment.this, "Something went wrong. getAssessment(): ShowAssessment.class", Toast.LENGTH_SHORT).show();
        }
    }
}