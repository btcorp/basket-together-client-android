package com.angel.black.baframework.ui.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

/**
 * Created by KimJeongHun on 2016-09-16.
 */
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private OnTimePickListener mOnTimePickListener;

    public static TimePickerDialogFragment newInstance(int hour, int minute, boolean is24HourView) {
        TimePickerDialogFragment f = new TimePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        args.putBoolean("is24HourView", is24HourView);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof OnTimePickListener) {
            mOnTimePickListener = (OnTimePickListener) getActivity();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = getArguments().getInt("hour");
        int minute = getArguments().getInt("minute");
        boolean is24HourView = getArguments().getBoolean("is24HourView");

        return new TimePickerDialog(getActivity(), this, hour, minute, is24HourView);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(mOnTimePickListener != null) {
            mOnTimePickListener.onTimePick(view, hourOfDay, minute);
        }
    }

    public interface OnTimePickListener {
        void onTimePick(TimePicker view, int hourOfDay, int minute);
    }
}
