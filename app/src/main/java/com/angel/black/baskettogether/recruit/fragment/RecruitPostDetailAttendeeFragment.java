package com.angel.black.baskettogether.recruit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.base.BaseFragment;
import com.angel.black.baskettogether.recruit.view.RecruitAttendeeView;

/**
 * Created by KimJeongHun on 2016-09-04.
 */
public class RecruitPostDetailAttendeeFragment extends BaseFragment {
    private LinearLayout mContainerAttendeeView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendee_list, container, false);

        mContainerAttendeeView = (LinearLayout) view;

        //TODO 테스트로 추가
        addAttendee("1", "test1");
        addAttendee("2", "스테판커리");

        return view;
    }

    public void addAttendee(String attendeeId, String attendeeName) {
        RecruitAttendeeView recruitAttendeeView = new RecruitAttendeeView(getContext());

        Attendee attendee = new Attendee(attendeeId, attendeeName);
        recruitAttendeeView.setTag(attendee);
        recruitAttendeeView.setName(attendeeName);

        mContainerAttendeeView.addView(recruitAttendeeView);
    }

    public static RecruitPostDetailAttendeeFragment newInstance() {
        RecruitPostDetailAttendeeFragment fragment = new RecruitPostDetailAttendeeFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public class Attendee {
        String id;
        String name;

        public Attendee(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
