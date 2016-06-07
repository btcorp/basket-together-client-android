package com.angel.black.baskettogether.core.network;

import android.os.AsyncTask;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.MyApplication;
import com.angel.black.baskettogether.core.network.util.NetworkUtil;
import com.angel.black.baskettogether.user.UserHelper;
import com.angel.black.baskettogether.util.MyLog;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Vector;

/**
 * Created by KimJeongHun on 2016-06-06.
 */
public class HttpAPIRequester extends AsyncTask<JSONObject, Void, String> {
    private BaseActivity activity;
    private String APIUrl;
    private String method;
    private OnAPIResponseListener onAPIResponseListener;

    public HttpAPIRequester(BaseActivity activity, String APIUrl, String method, OnAPIResponseListener onAPIResponseListener) {
        this.activity = activity;
        this.APIUrl = APIUrl;
        this.method = method;
        this.onAPIResponseListener = onAPIResponseListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!NetworkUtil.isOnline(activity)) {
            activity.showOkDialog(R.string.not_connected_network);
            this.cancel(true);
        } else {
            activity.showProgress();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        MyLog.w("request " + APIUrl + " is Cancelled");
        activity.showToast("요청 취소됨");
        activity.hideProgress();
    }

    @Override
    public String doInBackground(JSONObject... params) {
        InputStream inputStream;
        String result;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = null;

            if (method.equalsIgnoreCase("POST")) {
                HttpPost httpPost = new HttpPost(MyApplication.serverUrl + APIUrl);
                setHeader(httpPost);

                String json = params[0].toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);

                MyLog.w("request " + APIUrl + " POST, jsonParams >> " + json);

                httpResponse = httpClient.execute((HttpUriRequest) httpPost);
                MyLog.i("retCode >> " + httpResponse.getStatusLine().getStatusCode());

            } else if (method.equalsIgnoreCase("GET")) {
                HttpGet httpGet = makeHttpGet(MyApplication.serverUrl + APIUrl);
                setHeader(httpGet);

                MyLog.w("request " + APIUrl + " GET");

                httpResponse = httpClient.execute(httpGet);
                MyLog.i("retCode >> " + httpResponse.getStatusLine().getStatusCode());
            }

            if (httpResponse != null) {
                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    result = convertStreamToString(inputStream);
                    MyLog.d("API(" + APIUrl + ") result >> " + result);
                } else {
                    result = "Did not work!";
                    MyLog.d("API(" + APIUrl + ") result >> " + result);
                }

                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            onAPIResponseListener.onErrorResponse(APIUrl, e.getMessage(), e.getCause());
        }

        return null;
    }

    private void setHeader(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("Accept", "application/json");
        httpRequestBase.setHeader("Content-type", "application/json");
        if (isNeedUserAuthToken()) {
            httpRequestBase.setHeader("Authorization", "Token " + UserHelper.userAccessToken);
        }

    }

    @Override
    protected void onPostExecute(String result) {
        activity.hideProgress();

        if (result == null) {
            onAPIResponseListener.onErrorResponse(APIUrl, "result is null", null);
            return;
        }

        try {
            if (result.startsWith("{")) {
                JSONObject jsonResult = new JSONObject(result);
                onAPIResponseListener.onResponse(APIUrl, HttpURLConnection.HTTP_OK, jsonResult);
            } else if (result.startsWith("[")) {
                JSONArray jsonArrayResult = new JSONArray(result);
                onAPIResponseListener.onResponse(APIUrl, HttpURLConnection.HTTP_OK, jsonArrayResult);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            onAPIResponseListener.onErrorResponse(APIUrl, "result is not valid JSON", null);
        }
    }

    private HttpGet makeHttpGet(String url) {
        Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
//        nameValue.add( new BasicNameValuePair( "user_id", user_id ) ) ;
//        nameValue.add( new BasicNameValuePair( "user_pwd", user_pwd ) ) ;

//        String my_url = url + "?" + URLEncodedUtils.format( nameValue, null) ;
        HttpGet request = new HttpGet(url);
        return request;
    }

    public static String convertStreamToString(InputStream is1) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is1), 4096);
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String contentOfMyInputStream = sb.toString();
        return contentOfMyInputStream;
    }

    private boolean isNeedUserAuthToken() {
        return !(APIUrl.contains(ServerURLInfo.API_USER_REGIST) || APIUrl.contains(ServerURLInfo.API_USER_LOGIN));
    }

    public interface OnAPIResponseListener {
        void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException;

        void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException;

        void onErrorResponse(String APIUrl, String message, Throwable cause);

    }
}
