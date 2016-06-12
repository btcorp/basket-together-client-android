package com.angel.black.baskettogether.recruit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.util.CalendarUtil;
import com.angel.black.baskettogether.util.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RecruitPostRegistActivity extends BaseActivity {
    private EditText mEditTitle;
    private EditText mEditContent;
    private AppCompatImageButton mBtnPickDate;
    private AppCompatImageButton mBtnPickPlace;
    private TextView mTxtRecruitDate;
    private TextView mTxtRecruitPlace;
    private AppCompatSpinner mSpinPeopleNum;

    private int recruitDateYear;
    private int recruitDateMonth;
    private int recruitDateDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_recruit);
        initToolbar();

        mEditTitle = (EditText) findViewById(R.id.post_recruit_title);
        mEditContent = (EditText) findViewById(R.id.post_recruit_content);
        mTxtRecruitDate = (TextView) findViewById(R.id.txt_recruit_date);
        mTxtRecruitPlace = (TextView) findViewById(R.id.txt_recruit_place);

        mSpinPeopleNum = (AppCompatSpinner) findViewById(R.id.spin_post_recruit_people_num);
        mSpinPeopleNum.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.recruit_people_nums)));

        Calendar c = Calendar.getInstance();

        recruitDateYear = c.get(Calendar.YEAR);
        recruitDateMonth = c.get(Calendar.MONTH);
        recruitDateDay = c.get(Calendar.DAY_OF_MONTH);

        mTxtRecruitDate.setText(CalendarUtil.getDateString(c));
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_recruit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.menu_post_regist) {
            MyLog.d("글 등록 완료버튼 클릭!");





            requestRegistPost();
            return true;
        }
        return false;
    }


    private void requestRegistPost() {
        try {
            JSONObject data = buildRegistPostData();

            new HttpAPIRequester(this, true, ServerURLInfo.API_RECRUIT_POST_REGIST, "POST", new HttpAPIRequester.OnAPIResponseListener() {
                @Override
                public void onResponse(String APIUrl, int retCode, JSONObject response) {
                    showToast("글등록 성공");
                }

                @Override
                public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {

                }

                @Override
                public void onErrorResponse(String APIUrl, String message, Throwable cause) {
                    showToast("글등록 실패");
                }
            }).execute(data);

        } catch(JSONException e) {
            e.printStackTrace();
            MyLog.e("글등록 요청중 오류 발생!!");
        }
    }

    private JSONObject buildRegistPostData() throws JSONException{
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("author", "http://localhost:8000/users/1/");
        jsonObject.put("title", mEditTitle.getText().toString().trim());
        jsonObject.put("content", mEditContent.getText().toString().trim());
        jsonObject.put("recruit_count", mSpinPeopleNum.getSelectedItemPosition() + 1);
        jsonObject.put("gps_x", "123.356");
        jsonObject.put("gps_y", "452.952");
        jsonObject.put("address1", "경기도 양주시 백석읍");
        jsonObject.put("address2", "백석 체육공원 농구장");
        jsonObject.put("meeting_date", new SimpleDateFormat("yyyy-mm-dd hh:mm").format(new Date(System.currentTimeMillis())));

        return jsonObject;
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_pick_date) {
            MyLog.d("날짜 픽 버튼 클릭");
            showDatePickerDialog();
        }
    }

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(recruitDateYear, recruitDateMonth, recruitDateDay);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private int year;
        private int month;
        private int day;

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        public static DatePickerFragment newInstance(int year, int month, int day) {
            DatePickerFragment f = new DatePickerFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("year", year);
            args.putInt("month", month);
            args.putInt("day", day);
            f.setArguments(args);

            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();

            if(savedInstanceState != null) {
                c.set(savedInstanceState.getInt("year"), savedInstanceState.getInt("month"), savedInstanceState.getInt("day"));
            }

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            RecruitPostRegistActivity activity = ((RecruitPostRegistActivity)getActivity());
            activity.recruitDateYear = year;
            activity.recruitDateMonth = month;
            activity.recruitDateDay = day;

            activity.mTxtRecruitDate.setText(CalendarUtil.getDateString(year, month, day));
        }
    }
}
