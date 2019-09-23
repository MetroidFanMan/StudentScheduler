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


public class TermList extends AppCompatActivity implements TermViewAdapter.OnTermListener {

    private SQLiteDatabase db;
    private TermViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_list);

        db = new DBConnect(this).getReadableDatabase();

        RecyclerView recyclerView = findViewById(R.id.termRecView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TermViewAdapter(this, termCursor(), this);
        recyclerView.setAdapter(adapter);
    }

    private Cursor termCursor(){
        return db.rawQuery("SELECT * FROM terms ORDER BY termTitle ASC", null);
    }

    @Override
    public void onTermClick(String id) {
        Intent intent = new Intent(this, ShowTerm.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onTermLongClick(final String id, View view) {
        PopupMenu popupMenu = new PopupMenu(TermList.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String title = menuItem.getTitle().toString();
                switch (title){
                    case "Edit":
                        Intent editIntent = new Intent(TermList.this, CreateTerm.class);
                        editIntent.putExtra("id", id);
                        startActivity(editIntent);
                        finish();
                        break;
                    case "Delete":
                        try (SQLiteDatabase db = new DBConnect(TermList.this).getWritableDatabase()) {
                            Cursor cursor = db.query("courses", null, "termId = ?", new String[]{id}, null, null, null);
                            if (cursor.getCount() != 0) {
                                Toast.makeText(TermList.this, "Terms with courses can not be deleted.\nPlease remove courses prior to deletion.", Toast.LENGTH_LONG).show();
                            } else {
                                int result = db.delete("terms", "termId = ?", new String[]{id});
                                if (result != 0) {
                                    Toast.makeText(TermList.this, "Term successfully deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TermList.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                                startActivity(new Intent(TermList.this, TermList.class));
                            }
                            cursor.close();
                        } catch(SQLException e){
                            e.getMessage();
                        }
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
