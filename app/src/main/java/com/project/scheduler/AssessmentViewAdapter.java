package com.project.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class AssessmentViewAdapter extends RecyclerView.Adapter<AssessmentViewAdapter.AssessmentViewHolder> {

    private Cursor assessmentCursor;
    private Context assessmentContext;
    private OnAssessmentListener onAssessmentListener;

    public AssessmentViewAdapter(Context assessmentContext, Cursor assessmentCursor, OnAssessmentListener onAssessmentListener) {
        this.assessmentContext = assessmentContext;
        this.assessmentCursor = assessmentCursor;
        this.onAssessmentListener = onAssessmentListener;
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assessment_list_layout, viewGroup, false);
        return new AssessmentViewHolder(view, onAssessmentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final AssessmentViewHolder assessmentViewHolder, final int i) {
        if (!assessmentCursor.moveToPosition(i)){
            return;
        }

        assessmentViewHolder.assessmentId.setText(assessmentCursor.getString(assessmentCursor.getColumnIndex("assessmentId")));
        assessmentViewHolder.assessmentTitle.setText(assessmentCursor.getString(assessmentCursor.getColumnIndex("assessmentTitle")));
        assessmentViewHolder.assessmentDueDate.setText(assessmentCursor.getString(assessmentCursor.getColumnIndex("dueDate")));

    }

    @Override
    public int getItemCount() {
        return assessmentCursor.getCount();
    }

    public class AssessmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView assessmentId, assessmentTitle, assessmentDueDate;
        RelativeLayout assessmentLayout;
        OnAssessmentListener onAssessmentListener;

        public AssessmentViewHolder(View itemView, OnAssessmentListener onAssessmentListener) {
            super(itemView);
            assessmentId = itemView.findViewById(R.id.assessId);
            assessmentTitle = itemView.findViewById(R.id.assessTitle);
            assessmentDueDate = itemView.findViewById(R.id.dueDate);
            assessmentLayout = itemView.findViewById(R.id.assessmentList);

            this.onAssessmentListener = onAssessmentListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAssessmentListener.onAssessmentClick(assessmentId.getText().toString());
        }

        @Override
        public boolean onLongClick(View v) {
            onAssessmentListener.onAssessmentLongClick(assessmentId.getText().toString(), AssessmentViewHolder.this.itemView);
            return false;
        }
    }

    public interface OnAssessmentListener {
        void onAssessmentClick(String id);
        void onAssessmentLongClick(String id, View view);
    }
}