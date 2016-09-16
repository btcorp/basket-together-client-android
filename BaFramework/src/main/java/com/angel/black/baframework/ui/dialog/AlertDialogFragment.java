package com.angel.black.baframework.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by KimJeongHun on 2016-09-16.
 */
public class AlertDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POSITIVE_CLICK = "positiveClick";
    private static final String ARG_NEGATIVE_CLICK = "negativeClick";

    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        if(title != null) args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public static AlertDialogFragment newInstance(String title, String message, DialogClickListener positiveClickListener) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        if(title != null) args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putSerializable(ARG_POSITIVE_CLICK, positiveClickListener);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }


    public static AlertDialogFragment newInstance(String title, String message,
                                                  DialogClickListener positiveClickListener,
                                                  DialogClickListener negativeClickListener) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        if(title != null) args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putSerializable(ARG_POSITIVE_CLICK, positiveClickListener);
        args.putSerializable(ARG_NEGATIVE_CLICK, negativeClickListener);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        DialogClickListener positiveClick = (DialogClickListener) getArguments().getSerializable(ARG_POSITIVE_CLICK);
        DialogClickListener negativeClick = (DialogClickListener) getArguments().getSerializable(ARG_NEGATIVE_CLICK);

        if(title != null)
            builder.setTitle(title);

        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, positiveClick);

        if(negativeClick != null) {
            builder.setNegativeButton(android.R.string.cancel, negativeClick);
        }

        return builder.create();
    }
}
