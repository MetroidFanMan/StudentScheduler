package com.project.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CourseViewAdapter extends RecyclerView.Adapter<CourseViewAdapter.CourseViewHolder> {

    private Cursor courseCursor;
    private ArrayMap<Integer, String> termMap;
    private Context courseContext;
    private OnCourseListener onCourseListener;

    public CourseViewAdapter(Context courseContext, Cursor courseCursor, ArrayMap<Integer, String> mentorMap, OnCourseListener onCourseListener) {
        this.courseContext = courseContext;
        this.courseCursor = courseCursor;
        this.termMap = mentorMap;
        this.onCourseListener = onCourseListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_list_layout, viewGroup, false);
        return new CourseViewHolder(view, onCourseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CourseViewHolder courseViewHolder, final int i) {
        if (!courseCursor.moveToPosition(i)){
            return;
        }

        courseViewHolder.courseId.setText(courseCursor.getString(courseCursor.getColumnIndex("courseId")));
        courseViewHolder.courseTitle.setText(courseCursor.getString(courseCursor.getColumnIndex("courseTitle")));
        courseViewHolder.courseStart.setText(courseCursor.getString(courseCursor.getColumnIndex("startDate")));
        courseViewHolder.courseEnd.setText(courseCursor.getString(courseCursor.getColumnIndex("endDate")));
        courseViewHolder.status.setText(courseCursor.getString(courseCursor.getColumnIndex("status")));
        if (courseCursor.getInt(courseCursor.getColumnIndex("termId")) != 0) {
            courseViewHolder.term.setText(termMap.get(courseCursor.getInt(courseCursor.getColumnIndex("termId"))));
        } else {
            courseViewHolder.term.setText("Term not set");
        }

    }

    @Override
    public int getItemCount() {
       return courseCursor.getCount();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView courseId, courseTitle, courseStart, courseEnd, term, status;
        RelativeLayout courseLayout;
        OnCourseListener onCourseListener;

        public CourseViewHolder(View itemView, OnCourseListener onCourseListener) {
            super(itemView);
            courseId = itemView.findViewById(R.id.courseId);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseStart = itemView.findViewById(R.id.courseStartDate);
            courseEnd = itemView.findViewById(R.id.courseEndDate);
            term = itemView.findViewById(R.id.courseTerm);
            status = itemView.findViewById(R.id.courseStatus);
            courseLayout = itemView.findViewById(R.id.courseList);

            this.onCourseListener = onCourseListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCourseListener.onCourseClick(courseId.getText().toString());
        }

        @Override
        public boolean onLongClick(View v) {
            onCourseListener.onCourseLongClick(courseId.getText().toString(), CourseViewHolder.this.itemView);
            return false;
        }
    }

    public interface OnCourseListener {
        void onCourseClick(String id);
        void onCourseLongClick(String id, View view);
    }
}
