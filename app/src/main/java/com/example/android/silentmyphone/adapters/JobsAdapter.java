package com.example.android.silentmyphone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobsViewHolder> {

    private ArrayList<MuteJob> mJobsList;
    private Context mContext;
    private OnJobClickListener mJobClickListener;

    public JobsAdapter(Context context, ArrayList<MuteJob> muteJobs, OnJobClickListener listener) {
        this.mContext = context;
        this.mJobsList = muteJobs;
        this.mJobClickListener = listener;
    }

    public interface OnJobClickListener{
        void onJobClick();
    }

    @NonNull
    @Override
    public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_job_item,parent,false);

        if(view != null){
            view.setFocusable(true);
        }

        return new JobsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobsViewHolder holder, int position) {

        MuteJob job = mJobsList.get(holder.getAdapterPosition());

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTimeInMillis(job.getStartTime());
        endCalendar.setTimeInMillis(job.getEndTime());

        holder.startDateTV.setText(formatMillis(startCalendar));
        holder.endDateTV.setText(formatMillis(endCalendar));
        holder.silentType.setText("Personal");
        holder.repeatModeTV.setText(job.getRepeatMode()+"");

        holder.repeatModeTV.setOnClickListener(view -> mJobClickListener.onJobClick());

    }

    public static String formatMillis(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get((Calendar.MINUTE));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if(mJobsList == null) {
            return 0;
        } else {
            return mJobsList.size();
        }
    }

    public ArrayList<MuteJob> getmJobsList(){
        return mJobsList;
    }

    public void setJobs(List<MuteJob> jobs) {
        mJobsList = new ArrayList<>(jobs);
        notifyDataSetChanged();
    }

    public class JobsViewHolder extends RecyclerView.ViewHolder {

        TextView startDateTV;
        TextView endDateTV;
        TextView repeatModeTV;
        TextView silentType;

        public JobsViewHolder(View itemView) {
            super(itemView);
            startDateTV = itemView.findViewById(R.id.adapter_start_date);
            endDateTV = itemView.findViewById(R.id.adapter_end_date);
            repeatModeTV = itemView.findViewById(R.id.adapter_repeat_mode);
            silentType = itemView.findViewById(R.id.adapter_silent_type);
        }
    }
}
