package com.example.android.silentmyphone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.silentmyphone.MuteJob;
import com.example.android.silentmyphone.R;
import com.example.android.silentmyphone.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_BUSINESS = 1;
    private final static int VIEW_TYPE_PERSONAL = 0;
    private final static String TAG = JobsAdapter.class.getSimpleName();

    private ArrayList<MuteJob> mJobsList;
    private Context mContext;
    private OnJobClickListener mJobClickListener;

    public JobsAdapter(Context context, ArrayList<MuteJob> muteJobs, OnJobClickListener listener) {
        this.mContext = context;
        this.mJobsList = muteJobs;
        this.mJobClickListener = listener;
    }

    public interface OnJobClickListener{
        void onJobClick(int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case VIEW_TYPE_PERSONAL:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.adapter_job_item, parent, false);
                return new JobsViewHolder(view);

            default:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.adapter_business_job, parent, false);
                return new BusinessJobsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MuteJob job = mJobsList.get(holder.getAdapterPosition());
        Log.i(TAG,job.toString());

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTimeInMillis(job.getStartTime());
        endCalendar.setTimeInMillis(job.getEndTime());

        if(holder.getItemViewType() == VIEW_TYPE_PERSONAL) {
            JobsViewHolder mHolder =  (JobsViewHolder)holder;

            mHolder.itemView.setOnClickListener(view -> mJobClickListener.onJobClick(mHolder.getAdapterPosition()));

            mHolder.dateTV.setText(CalendarUtils.getPrettyHour(startCalendar.getTimeInMillis()) + " - " +
                    CalendarUtils.getPrettyHour(endCalendar.getTimeInMillis()));

            if(job.getRepeatMode() == MuteJob.MODE_ONE_TIME) {
                mHolder.daysWrapper.setVisibility(View.GONE);
                mHolder.dayTV.setText(CalendarUtils.getDayRepresentation(job.getStartTime(),mContext));
            } else {
                mHolder.dayTV.setVisibility(View.GONE);
                boolean[] checkBoxes = job.getRepeatDays();
                for(int i=0; i<checkBoxes.length;i++) {
                    //Get the checkbox in the i'th place
                    ((CheckBox)((LinearLayout)mHolder.daysWrapper.getChildAt(i)).getChildAt(1))
                            .setChecked(checkBoxes[i]);
                }
            }
        } else if (holder.getItemViewType() == VIEW_TYPE_BUSINESS) {
            BusinessJobsViewHolder mHolder = (BusinessJobsViewHolder)holder;
            mHolder.dateTV.setText(CalendarUtils.getPrettyHour(startCalendar.getTimeInMillis()) + " - " +
                    CalendarUtils.getPrettyHour(endCalendar.getTimeInMillis()));

            mHolder.dayTV.setText(CalendarUtils.getDayRepresentation(job.getStartTime(),mContext));
            mHolder.title.setText(job.getEventTitle());
            mHolder.location.setText(job.getEventLocation());
        }
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

    @Override
    public int getItemViewType(int position) {
        if(mJobsList.get(position).isBusiness()) {
            return VIEW_TYPE_BUSINESS;
        } else {
            return VIEW_TYPE_PERSONAL;
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

        TextView dateTV;
        TextView dayTV;
        LinearLayout daysWrapper;

        public JobsViewHolder(View itemView) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.adapter_date);
            daysWrapper = itemView.findViewById(R.id.adapter_repeated_days_wrapper);
            dayTV = itemView.findViewById(R.id.adapter_day);
        }
    }

    public class BusinessJobsViewHolder extends RecyclerView.ViewHolder {

        TextView dateTV;
        TextView location;
        TextView title;
        TextView dayTV;
        ImageView type;

        public BusinessJobsViewHolder(View itemView) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.adapter_date);
            location = itemView.findViewById(R.id.adapter_event_location);
            title = itemView.findViewById(R.id.adapter_event_title);
            type = itemView.findViewById(R.id.adapter_silent_type);
            dayTV = itemView.findViewById(R.id.adapter_day);
        }
    }
}
