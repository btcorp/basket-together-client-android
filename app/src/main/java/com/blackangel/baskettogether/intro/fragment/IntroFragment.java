package com.blackangel.baskettogether.intro.fragment;

/**
 * Created by Finger-kjh on 2017-12-01.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackangel.baframework.core.base.BaseFragment;
import com.blackangel.baskettogether.R;

public class IntroFragment extends BaseFragment {

    public static IntroFragment newInstance() {
        IntroFragment fragment = new IntroFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro, null);

        return view;
    }
}