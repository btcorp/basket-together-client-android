package com.angel.black.baframework.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by KimJeongHun on 2016-09-16.
 */
public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private OnDatePickListener mOnDatePickListener;

    public static DatePickerDialogFragment newInstance(int year, int month, int day) {
        DatePickerDialogFragment f = new DatePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof OnDatePickListener) {
            mOnDatePickListener = (OnDatePickListener) getActivity();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        c.set(getArguments().getInt("year"), getArguments().getInt("month"), getArguments().getInt("day"));

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(mOnDatePickListener != null) {
            mOnDatePickListener.onDatePick(view, year, month, day);
        }
    }

    public interface OnDatePickListener {
        void onDatePick(DatePicker view, int year, int month, int day);
    }
}
