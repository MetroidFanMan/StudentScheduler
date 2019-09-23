package com.project.scheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DBConnect connect;
    private FloatingActionButton fab_main, fab_term, fab_course, fab_asmt;
    private Animation fabClose, fabOpen, fabRotate, fabAntiRotate;
    boolean open = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab_main = findViewById(R.id.fab_main);
        fab_term = findViewById(R.id.fab_term);
        fab_course = findViewById(R.id.fab_course);
        fab_asmt = findViewById(R.id.fab_asmt);

        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        fabAntiRotate = AnimationUtils.loadAnimation(this, R.anim.anti_rotate);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (open) {
                    fab_term.startAnimation(fabClose);
                    fab_course.startAnimation(fabClose);
                    fab_asmt.startAnimation(fabClose);
                    fab_main.startAnimation(fabAntiRotate);
                    fab_term.setClickable(false);
                    fab_course.setClickable(false);
                    fab_asmt.setClickable(false);
                    open = false;
                } else {
                    fab_term.startAnimation(fabOpen);
                    fab_course.startAnimation(fabOpen);
                    fab_asmt.startAnimation(fabOpen);
                    fab_main.startAnimation(fabRotate);
                    fab_term.setClickable(true);
                    fab_course.setClickable(true);
                    fab_asmt.setClickable(true);
                    open = true;
                }
            }
        });

        fab_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateTerm.class));
                fab_term.startAnimation(fabClose);
                fab_course.startAnimation(fabClose);
                fab_asmt.startAnimation(fabClose);
                fab_main.startAnimation(fabAntiRotate);
                fab_term.setClickable(false);
                fab_course.setClickable(false);
                fab_asmt.setClickable(false);
                open = false;
            }
        });

        fab_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateCourse.class));
                fab_term.startAnimation(fabClose);
                fab_course.startAnimation(fabClose);
                fab_asmt.startAnimation(fabClose);
                fab_main.startAnimation(fabAntiRotate);
                fab_term.setClickable(false);
                fab_course.setClickable(false);
                fab_asmt.setClickable(false);
                open = false;
            }
        });

        fab_asmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAssessment.class));
                fab_term.startAnimation(fabClose);
                fab_course.startAnimation(fabClose);
                fab_asmt.startAnimation(fabClose);
                fab_main.startAnimation(fabAntiRotate);
                fab_term.setClickable(false);
                fab_course.setClickable(false);
                fab_asmt.setClickable(false);
                open = false;
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        connect = new DBConnect(this);
        connect.getReadableDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();

        connect.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_terms) {
            startActivity(new Intent(this, TermList.class));
        } else if (id == R.id.nav_courses) {
            startActivity(new Intent(this, CourseList.class));
        } else if (id == R.id.nav_assessments) {
            startActivity(new Intent(this, AssessmentList.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}