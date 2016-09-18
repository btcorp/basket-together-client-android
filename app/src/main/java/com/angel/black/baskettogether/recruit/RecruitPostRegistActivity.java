package com.angel.black.baskettogether.recruit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.ui.dialog.DatePickerDialogFragment;
import com.angel.black.baframework.ui.dialog.TimePickerDialogFragment;
import com.angel.black.baframework.util.CalendarUtil;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.api.APICallSuccessNotifier;
import com.angel.black.baskettogether.api.RecruitAPI;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.recruit.googlemap.LocationInfo;
import com.angel.black.baskettogether.recruit.googlemap.RecruitPostLocationMapActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RecruitPostRegistActivity extends BaseActivity implements DatePickerDialogFragment.OnDatePickListener, TimePickerDialogFragment.OnTimePickListener{
    private EditText mEditTitle;
    private EditText mEditContent;
    private ImageButton mBtnPickDate;
    private ImageButton mBtnPickPlace;
    private TextView mTxtRecruitDate;
    private TextView mTxtRecruitTime;
    private TextView mTxtAddress1;
    private EditText mEditAddress2;
    private Spinner mSpinPeopleNum;

    private int recruitDateYear;
    private int recruitDateMonth;
    private int recruitDateDay;
    private int recruitTimeHour;
    private int recruitTimeMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_post_regist);

        mEditTitle = (EditText) findViewById(R.id.post_recruit_title);
        mEditContent = (EditText) findViewById(R.id.post_recruit_content);
        mTxtRecruitDate = (TextView) findViewById(R.id.txt_recruit_date);
        mTxtRecruitTime = (TextView) findViewById(R.id.txt_recruit_time);
        mTxtAddress1 = (TextView) findViewById(R.id.txt_address1);
        mEditAddress2 = (EditText) findViewById(R.id.edit_address2);

        mSpinPeopleNum = (Spinner) findViewById(R.id.spin_post_recruit_people_num);
        mSpinPeopleNum.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.recruit_people_nums)));

        mBtnPickDate = (ImageButton) findViewById(R.id.btn_pick_date);
        mBtnPickPlace = (ImageButton) findViewById(R.id.btn_pick_place);

        Calendar c = Calendar.getInstance();

        recruitDateYear = c.get(Calendar.YEAR);
        recruitDateMonth = c.get(Calendar.MONTH);
        recruitDateDay = c.get(Calendar.DAY_OF_MONTH);
        recruitTimeHour = (c.get(Calendar.HOUR_OF_DAY) + 1) % 24;       // 디폴트로 현재 시간보다 1시간뒤
        recruitTimeMinute = 0;                                          // 디폴트로 0분

        mTxtRecruitTime.setText(CalendarUtil.getTimeString(recruitTimeHour, 0));
        mTxtRecruitDate.setText(CalendarUtil.getDateString(c));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recruit_post_regist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.menu_post_regist) {
            BaLog.d("글 등록 완료버튼 클릭!");

            String title = mEditTitle.getText().toString().trim();
            String content = mEditContent.getText().toString().trim();

            if(isValidateForm(title, content)) {
                requestRegistPost();
                return true;
            }
        }
        return false;
    }

    private boolean isValidateForm(String title, String content) {
        if(StringUtil.isEmptyString(title)) {
            mEditTitle.setError(getString(R.string.error_not_input_post_title));
            return false;
        } else if(StringUtil.isEmptyString(content)) {
            mEditContent.setError(getString(R.string.error_not_input_post_content));
            return false;
        } else if(mBtnPickPlace.getTag() == null || StringUtil.isEmptyInputString(mTxtAddress1.getText().toString().trim())) {
            mTxtAddress1.setError(getString(R.string.error_not_input_court_place));
            return false;
        }

        return true;
    }

    private String getPlaceLatLng() {
        LocationInfo locationInfo = (LocationInfo) mBtnPickPlace.getTag();
        return locationInfo.latitude + "," + locationInfo.longitude;
    }

    private void requestRegistPost() {
        String recruitDateTime = getRecruitDateTime();

        try {
            RecruitAPI.registRecruitPost(this, mEditTitle.getText().toString().trim(),
                    mEditContent.getText().toString().trim(), mSpinPeopleNum.getSelectedItemPosition() + 1,
                    recruitDateTime, getPlaceLatLng(), mTxtAddress1.getText().toString().trim(),
                    mEditAddress2.getText().toString().trim(), new APICallSuccessNotifier() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            showToast("글등록 성공");
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
        } catch(JSONException e) {
            e.printStackTrace();
            BaLog.e("글등록 요청중 오류 발생!!");
        }
    }

    private String getRecruitDateTime() {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, recruitDateYear);
        c.set(Calendar.MONTH, recruitDateMonth);
        c.set(Calendar.DAY_OF_MONTH, recruitDateDay);
        c.set(Calendar.HOUR_OF_DAY, recruitTimeHour);
        c.set(Calendar.MINUTE, recruitTimeMinute);

        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(c.getTimeInMillis()));
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_pick_date) {
            BaLog.d("날짜 픽 버튼 클릭");
            showDatePickerDialog();

        } else if(v.getId() == R.id.btn_pick_time) {
            BaLog.d("시간 픽 버튼 클릭");
            showTimePickerDialog();
        } else if(v.getId() == R.id.btn_pick_place) {
            Intent intent = new Intent(this, RecruitPostLocationMapActivity.class);

            if(mBtnPickPlace.getTag() != null) {
                LocationInfo locationInfo = (LocationInfo) mBtnPickPlace.getTag();

                intent.putExtra(IntentConst.KEY_EXTRA_MAP_LATITUDE, locationInfo.latitude);
                intent.putExtra(IntentConst.KEY_EXTRA_MAP_LONGITUDE, locationInfo.longitude);
                intent.putExtra(IntentConst.KEY_EXTRA_MAP_ADDRESS, locationInfo.address);
            }

            intent.putExtra(IntentConst.KEY_EXTRA_MAP_MODE, RecruitPostLocationMapActivity.MapMode.SELECT_LOCATION.toString());
            startActivityForResult(intent, IntentConst.REQUEST_MAP_LOCATION_SELECT);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(recruitDateYear, recruitDateMonth, recruitDateDay);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        TimePickerDialogFragment newFragment = TimePickerDialogFragment.newInstance(recruitTimeHour, recruitTimeMinute, false);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDatePick(DatePicker view, int year, int month, int day) {
        BaLog.i("year=" + year + ", month=" + month + ", day=" + day);

        if(CalendarUtil.isEalierThanToday(year, month, day)) {
            showToast(R.string.error_ealier_than_today);
            showDatePickerDialog();
            return;
        }

        recruitDateYear = year;
        recruitDateMonth = month;
        recruitDateDay = day;

        mTxtRecruitDate.setText(CalendarUtil.getDateString(year, month, day));
    }

    @Override
    public void onTimePick(TimePicker view, int hourOfDay, int minute) {
        BaLog.i("hourOfDay=" + hourOfDay + ", minute=" + minute);
        recruitTimeHour = hourOfDay;
        recruitTimeMinute = minute;

        mTxtRecruitTime.setText(CalendarUtil.getTimeString(hourOfDay, minute));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentConst.REQUEST_MAP_LOCATION_SELECT) {
            if (resultCode == RESULT_OK) {
                double latitude = data.getDoubleExtra(IntentConst.KEY_EXTRA_MAP_LATITUDE, 0);
                double longitude = data.getDoubleExtra(IntentConst.KEY_EXTRA_MAP_LONGITUDE, 0);
                String address = data.getStringExtra(IntentConst.KEY_EXTRA_MAP_ADDRESS);

                BaLog.d("select map latitude=" + latitude + ", longitude=" + longitude);
                BaLog.d("select map address=" + address);

                // 지도 설정 버튼에 태그로 위치정보 셋팅
                mBtnPickPlace.setTag(new LocationInfo(latitude, longitude, address));

                mEditAddress2.setVisibility(View.VISIBLE);

                mTxtAddress1.setError(null);
                mTxtAddress1.setText(address);
                mEditAddress2.requestFocus();
            }
        }
    }
}
