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


public class TermViewAdapter extends RecyclerView.Adapter<TermViewAdapter.TermViewHolder> {

    private Cursor termCursor;
    private Context termContext;
    private OnTermListener onTermListener;

    public TermViewAdapter(Context termContext, Cursor termCursor, OnTermListener onTermListener) {
        this.termContext = termContext;
        this.termCursor = termCursor;
        this.onTermListener = onTermListener;
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.term_list_layout, viewGroup, false);
        return new TermViewHolder(view, onTermListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final TermViewHolder termViewHolder, final int i) {

        if (!termCursor.moveToPosition(i)){
            return;
        }

        termViewHolder.termId.setText(termCursor.getString(termCursor.getColumnIndex("termId")));
        termViewHolder.termTitle.setText(termCursor.getString(termCursor.getColumnIndex("termTitle")));
        termViewHolder.termStart.setText(termCursor.getString(termCursor.getColumnIndex("startDate")));
        termViewHolder.termEnd.setText(termCursor.getString(termCursor.getColumnIndex("endDate")));

    }

    @Override
    public int getItemCount() {
        return termCursor.getCount();
    }

    public class TermViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView termId, termTitle, termStart, termEnd;
        RelativeLayout termLayout;
        OnTermListener onTermListener;

        public TermViewHolder(View itemView, OnTermListener onTermListener) {
            super(itemView);
            termId = itemView.findViewById(R.id.termId);
            termTitle = itemView.findViewById(R.id.termTitle);
            termStart = itemView.findViewById(R.id.termStartDate);
            termEnd = itemView.findViewById(R.id.termEndDate);
            termLayout = itemView.findViewById(R.id.termList);

            this.onTermListener = onTermListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTermListener.onTermClick(termId.getText().toString());
        }

        @Override
        public boolean onLongClick(View v) {
            onTermListener.onTermLongClick(termId.getText().toString(), TermViewHolder.this.itemView);
            return false;
        }
    }

    public interface OnTermListener {
        void onTermClick(String id);
        void onTermLongClick(String id, View view);
    }
}
