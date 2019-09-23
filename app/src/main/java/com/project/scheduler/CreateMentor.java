package com.project.scheduler;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class CreateMentor extends AppCompatActivity {

    private EditText mentorName, mentorEmail, mentorPhone;
    private Button save, cancel;
    private String name, email, phone;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_mentor);

        mentorName = findViewById(R.id.createMentorName);
        mentorEmail = findViewById(R.id.createMentorEmail);
        mentorPhone = findViewById(R.id.createMentorPhone);

        if (getIntent().hasExtra("name")){
            try (SQLiteDatabase db = new DBConnect(this).getReadableDatabase()){
                Cursor cursor = db.query("mentors", null, "name = ?", new String[]{getIntent().getStringExtra("name")}, null, null, null);
                if (cursor.moveToFirst()){
                    id = cursor.getInt(cursor.getColumnIndex("mentorId"));
                    mentorName.setText(cursor.getString(cursor.getColumnIndex("name")));
                    mentorEmail.setText(cursor.getString(cursor.getColumnIndex("email")));
                    mentorPhone.setText(cursor.getString(cursor.getColumnIndex("phone")));
                }
                cursor.close();
            } catch (SQLException e){
                e.getMessage();
            }
        }

        save = findViewById(R.id.createMentorSaveBtn);
        cancel = findViewById(R.id.createMentorCancelBtn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getIntent().hasExtra("name")){
                    writeToDatabase();
                } else {
                    updateDatabase();
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

    private void writeToDatabase() {
        try (SQLiteDatabase db = new DBConnect(this).getWritableDatabase()){
            if (mentorName.getText().toString().trim().isEmpty()){
                Toast.makeText(CreateMentor.this, "Please enter a mentor name", Toast.LENGTH_SHORT).show();
            } else if (mentorEmail.getText().toString().trim().isEmpty()){
                Toast.makeText(CreateMentor.this, "Please enter a mentor email address", Toast.LENGTH_SHORT).show();
            } else if (mentorPhone.getText().toString().trim().isEmpty()){
                Toast.makeText(CreateMentor.this, "Please enter a mentor phone number", Toast.LENGTH_SHORT).show();
            } else {
                name = mentorName.getText().toString();
                email = mentorEmail.getText().toString();
                phone = PhoneNumberUtils.formatNumber(mentorPhone.getText().toString(), Locale.getDefault().getCountry());

                ContentValues insertValues = new ContentValues();
                insertValues.put("name", name);
                insertValues.put("email", email);
                insertValues.put("phone", phone);

                long result = db.insert("mentors",
                        null,
                        insertValues);

                if (result != -1) {
                    Toast.makeText(this, "Mentor added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    private void updateDatabase() {
        try (SQLiteDatabase db = new DBConnect(this).getWritableDatabase()){
            if (mentorName.getText().toString().trim().isEmpty()){
                Toast.makeText(CreateMentor.this, "Please enter a mentor name", Toast.LENGTH_SHORT).show();
            } else if (mentorEmail.getText().toString().trim().isEmpty()){
                Toast.makeText(CreateMentor.this, "Please enter a mentor email address", Toast.LENGTH_SHORT).show();
            } else if (mentorPhone.getText().toString().trim().isEmpty()){
                Toast.makeText(CreateMentor.this, "Please enter a mentor phone number", Toast.LENGTH_SHORT).show();
            } else {
                name = mentorName.getText().toString();
                email = mentorEmail.getText().toString();
                phone = PhoneNumberUtils.formatNumber(mentorPhone.getText().toString(), Locale.getDefault().getCountry());

                ContentValues updateValues = new ContentValues();
                updateValues.put("name", name);
                updateValues.put("email", email);
                updateValues.put("phone", phone);

                long result = db.update("mentors",
                        updateValues,
                        "mentorId = ?",
                        new String[]{String.valueOf(id)});

                if (result != -1) {
                    Toast.makeText(this, "Mentor added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }
}
