package com.example.android.silentmyphone.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.example.android.silentmyphone.utils.CalendarUtils;
import com.example.android.silentmyphone.R;

public class NewJobDialog extends DialogFragment {

    OnSetJobBtnClicked listener;
    CheckBox mIsRepeatCheckbox;
    CheckBox[] mDaysCheckBoxes;

    public NewJobDialog(){}

    @SuppressLint("ValidFragment")
    public NewJobDialog(OnSetJobBtnClicked listener){
        super();
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Make the dialog expand in screen
        Dialog dialog = getDialog();
        if(dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT ;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width,height);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_set_job, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the Time Pickers
        TimePicker timePickerStart = view.findViewById(R.id.time_picker_start);
        TimePicker timePickerEnd = view.findViewById(R.id.time_picker_end);
        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);

        //Set up the Days pickers
        NumberPicker dayPickerStart = view.findViewById(R.id.day_picker_start);
        NumberPicker dayPickerEnd = view.findViewById(R.id.day_picker_end);
        dayPickerStart.setDisplayedValues(CalendarUtils.getWeekFromTodayOn());
        dayPickerEnd.setDisplayedValues(CalendarUtils.getWeekFromTodayOn());
        dayPickerStart.setMinValue(0);
        dayPickerStart.setMaxValue(6);
        dayPickerEnd.setMinValue(0);
        dayPickerEnd.setMaxValue(6);

        //If user check the repeat mode
        mIsRepeatCheckbox = view.findViewById(R.id.checkbox_is_daily);
        mIsRepeatCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                dayPickerStart.setValue(0);
                dayPickerStart.setEnabled(false);
                dayPickerEnd.setEnabled(false);
                view.findViewById(R.id.days_layout).setVisibility(View.VISIBLE);
            } else {
                dayPickerStart.setEnabled(true);
                dayPickerEnd.setEnabled(true);
                view.findViewById(R.id.days_layout).setVisibility(View.GONE);
            }
        });

        //set up the days repeat checboxes.
        mDaysCheckBoxes = new CheckBox[7];
        mDaysCheckBoxes[0] = view.findViewById(R.id.checkbox_monday);
        mDaysCheckBoxes[1] = view.findViewById(R.id.checkbox_tuesday);
        mDaysCheckBoxes[2] = view.findViewById(R.id.checkbox_wednesday);
        mDaysCheckBoxes[3] = view.findViewById(R.id.checkbox_thursday);
        mDaysCheckBoxes[4] = view.findViewById(R.id.checkbox_friday);
        mDaysCheckBoxes[5] = view.findViewById(R.id.checkbox_saturday);
        mDaysCheckBoxes[6] = view.findViewById(R.id.checkbox_sunday);

        Button setBtn = view.findViewById(R.id.set_job_button);

        setBtn.setOnClickListener(view1 -> listener.onSetJobBtnClicked
                (timePickerStart,timePickerEnd,mIsRepeatCheckbox.isChecked(),mDaysCheckBoxes,this));
    }

    public interface OnSetJobBtnClicked{
        void onSetJobBtnClicked(TimePicker start, TimePicker end, boolean isRepeat,
                                CheckBox[] checkBoxes, NewJobDialog fragment);
    }

}
