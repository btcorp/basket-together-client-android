package com.angel.black.baskettogether.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.angel.black.baskettogether.util.MyLog;
import com.angel.black.baskettogether.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity {
    private EditText mEditId;
    private EditText mEditPw;
    private EditText mEditPwRe;
    private EditText mEditNickName;
    private EditText mEditMobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        initToolbar();

        mEditId = (EditText) findViewById(R.id.user_id);
        mEditPw = (EditText) findViewById(R.id.password);
        mEditPwRe = (EditText) findViewById(R.id.password_re);
        mEditNickName = (EditText) findViewById(R.id.nickname);
        mEditMobileNo = (EditText) findViewById(R.id.mobile_no);

//        UserHelper.authToDjangoServer();

    }

    private void authToDjangoServer(String username, String password1, String password2, String email) throws JSONException{
//        JSONObject requestData = buildRequestAuthData();
//        UserHelper.POST(MyApplication.serverUrl + "rest-auth/registration/", requestData);
        JSONObject requestData = buildRequestAuthData();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, MyApplication.serverUrl + "rest-auth/registration/", requestData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SignUpActivity.this, "response >> " + response.toString(), Toast.LENGTH_LONG).show();
                        MyLog.d("response >> " + response.toString());
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpActivity.this, "error >> " + error.toString(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(SignUpActivity.this, "response >> " + response.toString(), Toast.LENGTH_LONG).show();
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
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_sign_up) {
            String id = mEditId.getText().toString().trim();
            String pwd = mEditPw.getText().toString().trim();
            String pwdRe = mEditPwRe.getText().toString().trim();
            String nickname = mEditNickName.getText().toString().trim();
            String mobileNo = mEditMobileNo.getText().toString().trim();

            if(isValidateForm(id, pwd, pwdRe, nickname, mobileNo)) {
                try {
                    authToDjangoServer(id, pwd, pwdRe, "");
                } catch(JSONException e) {
                    e.printStackTrace();
                }

//                try {
//                    requestSignUp(id, pwd, nickname, mobileNo);
//                } catch(JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }
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
                Toast.makeText(SignUpActivity.this, "error >> " + error.toString(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                MyLog.e("response >> " + error.toString());
            }
        }

        );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, MyApplication.serverUrl + "user/add/", requestData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SignUpActivity.this, "response >> " + response.toString(), Toast.LENGTH_LONG).show();
                        MyLog.d("response >> " + response.toString());
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpActivity.this, "error >> " + error.toString(), Toast.LENGTH_LONG).show();
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
            mEditId.setError(getString(R.string.error_not_input_id));
            return false;
        } else if(StringUtil.isEmptyString(pwd)) {
            mEditPw.setError(getString(R.string.error_not_input_pwd));
            return false;
        } else if(StringUtil.isEmptyString(pwdRe)) {
            mEditPwRe.setError(getString(R.string.error_not_input_pwd_re));
            return false;
        } else if(StringUtil.isEmptyString(nickname)) {
            mEditNickName.setError(getString(R.string.error_not_input_nickname));
            return false;
        } else if(StringUtil.isEmptyString(mobileNo)) {
            mEditMobileNo.setError(getString(R.string.error_not_input_mobile_no));
            return false;
        } else if(!pwd.equals(pwdRe)) {
            showOkDialog(R.string.error_not_equal_pw_re);
            return false;
        }

        return true;
    }
}
