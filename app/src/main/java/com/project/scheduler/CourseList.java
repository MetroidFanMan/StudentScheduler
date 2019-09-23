package com.project.scheduler;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class CourseList extends AppCompatActivity implements CourseViewAdapter.OnCourseListener {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list);

        db = new DBConnect(this).getReadableDatabase();

        Cursor cursor = courseCursor();
        ArrayMap<Integer, String> termMap = termMap();

        RecyclerView recyclerView = findViewById(R.id.courseRecView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CourseViewAdapter adapter = new CourseViewAdapter(this, cursor, termMap, this);
        recyclerView.setAdapter(adapter);
    }

    private Cursor courseCursor(){
        return db.rawQuery("SELECT * FROM courses ORDER By courseTitle ASC", null);
    }

    private ArrayMap<Integer, String> termMap(){
        ArrayMap<Integer, String> termMap = new ArrayMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM terms", null);
        while(cursor.moveToNext()){
            termMap.put(cursor.getInt(0), cursor.getString(1));
        }
        cursor.close();
        return termMap;
    }

    @Override
    public void onCourseClick(final String id) {
        Intent showIntent = new Intent(CourseList.this, ShowCourse.class);
        showIntent.putExtra("id", id);
        startActivity(showIntent);
    }

    @Override
    public void onCourseLongClick(final String id, View view) {
        PopupMenu popupMenu = new PopupMenu(CourseList.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String title = menuItem.getTitle().toString();
                switch (title){
                    case "Edit":
                        Intent editIntent = new Intent(CourseList.this, CreateCourse.class);
                        editIntent.putExtra("id", id);
                        startActivity(editIntent);
                        finish();
                        break;
                    case "Delete":
                        try (SQLiteDatabase db = new DBConnect(CourseList.this).getWritableDatabase()){
                            int result = db.delete("courses", "courseId = ?", new String[]{id});
                            if (result != 0){
                                Toast.makeText(CourseList.this, "Course successfully deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CourseList.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (SQLException e){
                            e.getMessage();
                        }
                        finish();
                        startActivity(new Intent(CourseList.this, CourseList.class));
                }
                return true;
            }
        });
        popupMenu.show();

    }
}
