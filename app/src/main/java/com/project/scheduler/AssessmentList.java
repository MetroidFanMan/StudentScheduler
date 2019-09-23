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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class AssessmentList extends AppCompatActivity implements AssessmentViewAdapter.OnAssessmentListener {

    private SQLiteDatabase db;
    private AssessmentViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessment_list);

        db = new DBConnect(this).getReadableDatabase();

        RecyclerView recyclerView = findViewById(R.id.assessRecView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssessmentViewAdapter(this, assessmentCursor(), this);
        recyclerView.setAdapter(adapter);
    }

    private Cursor assessmentCursor(){
        return db.rawQuery("SELECT * FROM assessments ORDER BY assessmentTitle ASC", null);
    }

    @Override
    public void onAssessmentClick(final String id) {
        Intent showIntent = new Intent(AssessmentList.this, ShowAssessment.class);
        showIntent.putExtra("id", id);
        startActivity(showIntent);
    }

    @Override
    public void onAssessmentLongClick(final String id, View view) {
        PopupMenu popupMenu = new PopupMenu(AssessmentList.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String title = menuItem.getTitle().toString();
                switch (title){
                    case "Edit":
                        Intent editIntent = new Intent(AssessmentList.this, CreateAssessment.class);
                        editIntent.putExtra("id", id);
                        startActivity(editIntent);
                        finish();
                        break;
                    case "Delete":
                        try (SQLiteDatabase db = new DBConnect(AssessmentList.this).getWritableDatabase()){
                            int result = db.delete("assessments", "assessmentId = ?", new String[]{id});
                            if (result != 0){
                                Toast.makeText(AssessmentList.this, "Assessment successfully deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AssessmentList.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (SQLException e){
                            e.getMessage();
                        }
                        finish();
                        startActivity(new Intent(AssessmentList.this, AssessmentList.class));
                }
                return true;
            }
        });
        popupMenu.show();
    }
}