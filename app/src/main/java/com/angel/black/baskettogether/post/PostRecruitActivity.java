package com.angel.black.baskettogether.post;

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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.MyApplication;
import com.angel.black.baskettogether.util.CalendarUtil;
import com.angel.black.baskettogether.util.MyLog;
import com.angel.black.baskettogether.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostRecruitActivity extends BaseActivity {
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

        //TODO 날짜 초기화
    }

    private void authToDjangoServer(String username, String password1, String password2, String email) throws JSONException{
//        JSONObject requestData = buildRequestAuthData();
//        UserHelper.POST(MyApplication.serverUrl + "rest-auth/registration/", requestData);
        JSONObject requestData = buildRequestAuthData();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, MyApplication.serverUrl + "rest-auth/registration/", requestData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(PostRecruitActivity.this, "response >> " + response.toString(), Toast.LENGTH_LONG).show();
                        MyLog.d("response >> " + response.toString());
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostRecruitActivity.this, "error >> " + error.toString(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        MyLog.e("response >> " + error.toString());
                    }
                }
        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                Toast.makeText(PostRecruitActivity.this, "response >> " + response.toString(), Toast.LENGTH_LONG).show();
                MyLog.d("response >> " + response.toString());
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("username","KJH123");
//                params.put("password1", "123456");
//                params.put("password2", "123456");
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/json");
//                return params;
//            }
//        };
        MyApplication.getInstance().getRequestQueue().add(request);
// {
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("username", "KJH");
//                params.put("password1", "1234");
//                params.put("password2", "1234");
//                params.put("email", "black2602@gmail.com");
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/json");
//                return params;
//            }
//        };
//        MyApplication.getInstance().getRequestQueue().add(request);
    }

    private JSONObject buildRequestAuthData() throws JSONException {
        JSONObject json = new JSONObject();
//        json.put("user_id", id);
        json.put("username", "KJH123");
        json.put("password1", "123456");
        json.put("password2", "123456");
//        json.put("email", "black2602@gmail.com");
//        json.put("join_type", 0);
//        json.put("device_type", "a");

        return json;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();

//        mToolbar.inflateMenu(R.menu.post_recruit);
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
            return true;
        }
        return false;
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_pick_date) {
            MyLog.d("글등록 메뉴 버튼 클릭");
            showDatePickerDialog();
        }
    }

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(recruitDateYear, recruitDateMonth, recruitDateDay);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void requestSignUp(String id, String pwd, String nickname, String mobileNo) throws JSONException{
        JSONObject requestData = buildRequestJoinData(id, pwd, nickname, mobileNo);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MyApplication.serverUrl + "user/add/", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                MyLog.d("onResponse(response=" + s +")");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostRecruitActivity.this, "error >> " + error.toString(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                MyLog.e("response >> " + error.toString());
            }
        }

        );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, MyApplication.serverUrl + "user/add/", requestData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(PostRecruitActivity.this, "response >> " + response.toString(), Toast.LENGTH_LONG).show();
                        MyLog.d("response >> " + response.toString());
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostRecruitActivity.this, "error >> " + error.toString(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        MyLog.e("response >> " + error.toString());
                    }
                }
        );
        MyApplication.getInstance().getRequestQueue().add(request);
    }

    private JSONObject buildRequestJoinData(String id, String pwd, String nickname, String mobileNo) throws JSONException{
        JSONObject json = new JSONObject();
//        json.put("user_id", id);
        json.put("password", pwd);
        json.put("username", nickname);
//        json.put("mobile_no", mobileNo);
//        json.put("join_type", 0);
//        json.put("device_type", "a");

        return json;
    }

    private boolean isValidateForm(String id, String pwd, String pwdRe, String nickname, String mobileNo) {
        if(StringUtil.isEmptyString(id)) {
            mEditTitle.setError(getString(R.string.error_not_input_id));
            return false;
        } else if(StringUtil.isEmptyString(pwd)) {
            mEditContent.setError(getString(R.string.error_not_input_pwd));
            return false;
        }
//          else if(StringUtil.isEmptyString(pwdRe)) {
//            mEditPwRe.setError(getString(R.string.error_not_input_pwd_re));
//            return false;
//        } else if(StringUtil.isEmptyString(nickname)) {
//            mEditNickName.setError(getString(R.string.error_not_input_nickname));
//            return false;
//        } else if(StringUtil.isEmptyString(mobileNo)) {
//            mEditMobileNo.setError(getString(R.string.error_not_input_mobile_no));
//            return false;
//        } else if(!pwd.equals(pwdRe)) {
//            showOkDialog(R.string.error_not_equal_pw_re);
//            return false;
//        }

        return true;
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

            c.set(savedInstanceState.getInt("year"), savedInstanceState.getInt("month"), savedInstanceState.getInt("day"));

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((PostRecruitActivity)getActivity()).mTxtRecruitDate.setText(CalendarUtil.getDateString(year, month, day));
        }
    }
}
