package com.angel.black.baskettogether.recruit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.angel.black.baframework.core.base.BaseFragment;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.recruit.view.RecruitAttendeeView;

import java.util.ArrayList;

/**
 * Created by KimJeongHun on 2016-09-04.
 */
public class RecruitPostDetailAttendeeFragment extends BaseFragment {
    private static final String KEY_ATTENDEE = "attendee";

    private LinearLayout mContainerAttendeeView;
    private ArrayList<Attendee> mAttendees;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendee_list, container, false);

        mContainerAttendeeView = (LinearLayout) view;

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        showAttendees();
    }

    public void addAttendee(long attendeeId, String attendeeName) {
        if(mAttendees == null) {
            mAttendees = new ArrayList<>();
        }

        mAttendees.add(new Attendee(attendeeId, attendeeName));
    }

    public void showAttendees() {
        if(mAttendees == null)
            return;

        for(Attendee attendee : mAttendees) {
            RecruitAttendeeView recruitAttendeeView = new RecruitAttendeeView(getContext());

            recruitAttendeeView.setTag(attendee);
            recruitAttendeeView.setName(attendee.name);

            mContainerAttendeeView.addView(recruitAttendeeView);
        }
    }

    public static RecruitPostDetailAttendeeFragment newInstance() {
        RecruitPostDetailAttendeeFragment fragment = new RecruitPostDetailAttendeeFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public class Attendee {
        long id;
        String name;

        public Attendee(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
