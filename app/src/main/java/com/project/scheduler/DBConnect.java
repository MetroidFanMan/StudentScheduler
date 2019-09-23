package com.project.scheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnect extends SQLiteOpenHelper {

    private static final String DB_NAME = "SchedulerDB";
    private static final int DB_VERSION = 1;

    public DBConnect(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS terms (termId INTEGER PRIMARY KEY AUTOINCREMENT, termTitle text NOT NULL, startDate DATE NOT NULL, endDate DATE NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS assessments (assessmentId INTEGER PRIMARY KEY AUTOINCREMENT, courseId int, assessmentTitle text NOT NULL, dueDate DATE NOT NULL," +
                "FOREIGN KEY (courseId) REFERENCES courses(courseId))");
        db.execSQL("CREATE TABLE IF NOT EXISTS mentors (mentorId INTEGER PRIMARY KEY AUTOINCREMENT, name text NOT NULL, phone text NOT NULL, email text NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS courses (courseId INTEGER PRIMARY KEY AUTOINCREMENT, courseTitle text NOT NULL, termId int, mentorId text NOT NULL, startDate DATE NOT NULL, endDate DATE NOT NULL, status text NOT NULL, notes text," +
                "FOREIGN KEY (termId) REFERENCES terms(termId)," +
                "FOREIGN KEY (mentorId) REFERENCES mentors(mentorId))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
