package com.angel.black.baframework.network;

import android.os.AsyncTask;

import com.angel.black.baframework.BaApplication;
import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.core.base.BaseFragment;
import com.angel.black.baframework.core.base.BaseListActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.network.util.NetworkUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by KimJeongHun on 2016-06-06.
 */
public class HttpAPIRequester extends AsyncTask<JSONObject, Void, HttpAPIRequester.APICallResult> {
    private final String NOT_CONNECTED_NETWORK = "NotConnectNetwork";

    // 커스텀 에러코드
    public static final int ERROR_CODE_JSON_PARSING = 80000;
    public static final int ERROR_CODE_NULL_RESULT = 80001;

    public static final String NO_HTTP_RESPONSE = "NoHttpResponse";

    public HttpRequestStrategy mHttpRequestStrategy;

    private BaseActivity activity;
    private boolean showCenterLoading;
    private String APIUrl;
    private String method;
    private OnAPIResponseListener onAPIResponseListener;

    public HttpAPIRequester(BaseActivity activity, boolean showCenterLoading, String APIUrl, String method, OnAPIResponseListener onAPIResponseListener) {
        this.activity = activity;
        this.mHttpRequestStrategy = ((BaApplication) activity.getApplication()).getHttpRequestStrategy();
        this.showCenterLoading = showCenterLoading;
        this.APIUrl = APIUrl;
        this.method = method;
        this.onAPIResponseListener = onAPIResponseListener;
    }

    public HttpAPIRequester(BaseFragment fragment, boolean showCenterLoading, String APIUrl, String method, OnAPIResponseListener onAPIResponseListener) {
        this((BaseActivity) fragment.getActivity(), showCenterLoading, APIUrl, method, onAPIResponseListener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!NetworkUtil.isOnline(activity)) {
            activity.showOkDialog(R.string.error_not_connected_network);
            this.cancel(true);
        } else {
            if(activity instanceof BaseListActivity && !showCenterLoading) {
                ((BaseListActivity) activity).showLoadingFooter();
            } else if(activity instanceof BaseActivity && showCenterLoading) {
                activity.showProgress();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        BaLog.w("request " + APIUrl + " is Cancelled");
        activity.showToast("요청 취소됨");
        activity.hideProgress();
    }

    @Override
    public APICallResult doInBackground(JSONObject... params) {
        HttpURLConnection conn = null;
        int retCode = 0;

        String serverUrl = mHttpRequestStrategy.getServerUrl();
        try {
            URL connectURL = new URL(serverUrl + APIUrl);
            conn = (HttpURLConnection) connectURL.openConnection();

            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(method.equals("POST") || method.equals("PUT"));
            conn.setUseCaches(false);

            BaLog.d("setRequestMethod=" + method);

            mHttpRequestStrategy.setHeader(conn, method);

            conn.connect();

            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
                String json = params[0].toString();
                List<BasicNameValuePair> postParams = buildPostParams(json);

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                outputStreamWriter.write(getURLQuery(postParams));
//                outputStreamWriter.write(json);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            }

            BaLog.w("request " + serverUrl + APIUrl + " " + method);

            if(params[0] != null) {
                BaLog.w("jsonParams >> " + params[0].toString());
            }

            String response = getResponse(conn);
            int responseCode = conn.getResponseCode();
            String responseMsg = conn.getResponseMessage();
            BaLog.i("responseCode=" + responseCode + ", responseMsg=" + responseMsg);
            BaLog.w("API response=" + response);

            return new APICallResult(responseCode, responseMsg, response);


//            HttpClient httpClient = new DefaultHttpClient();
//            HttpRequestBase httpRequest = null;
//            HttpResponse httpResponse;
//
//            if (method.equalsIgnoreCase("POST")) {
//                httpRequest = new HttpPost(serverUrl + APIUrl);
//
//                // HttpPost 에 Post 데이터(JSON) input
//                String json = params[0].toString();
//                StringEntity se = new StringEntity(json, HTTP.UTF_8);
//                List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>(1);
//                postParams.add(new BasicNameValuePair("json_value", json));
//
//                ((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(postParams));
//
//            } else if (method.equalsIgnoreCase("GET")) {
//                httpRequest = makeHttpGet(serverUrl + APIUrl);
//
//            } else if (method.equalsIgnoreCase("DELETE")) {
//                httpRequest = makeHttpDelete(serverUrl + APIUrl);
//            }
//
//            BaLog.w("request " + serverUrl + APIUrl + " " + method);
//
//            if(params[0] != null) {
//                BaLog.w("jsonParams >> " + params[0].toString());
//            }
//
//            setHeader(httpRequest);
//            httpResponse = httpClient.execute(httpRequest);
//            BaLog.i("retCode >> " + httpResponse.getStatusLine().getStatusCode());
//
//            if (httpResponse != null) {
//                retCode = httpResponse.getStatusLine().getStatusCode();
//                HttpEntity httpEntity = httpResponse.getEntity();
//
//                if(httpEntity != null) {
//                    inputStream = httpEntity.getContent();
//                }
//
//                if (inputStream != null) {
//                    result = convertStreamToString(inputStream);
//                } else {
//                    result = NO_HTTP_RESPONSE;
//                }
//                BaLog.d("API(" + APIUrl + " " + method + ") result >> " + result);
//
//                return new APICallResult(retCode, result);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            onAPIResponseListener.onErrorResponse(APIUrl, retCode, e.getMessage(), e.getCause());
            return new APICallResult(retCode, e.getMessage(), NO_HTTP_RESPONSE);
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

    private List<BasicNameValuePair> buildPostParams(String json) throws JSONException {
        List<BasicNameValuePair> list = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(json);

        Iterator<String> keyIter = jsonObject.keys();
        while(keyIter.hasNext()) {
            String key = keyIter.next();

            list.add(new BasicNameValuePair(key, jsonObject.optString(key)));
        }

        return list;
    }

    private String getURLQuery(List<BasicNameValuePair> params){
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (BasicNameValuePair pair : params) {
            if (first)
                first = false;
            else
                stringBuilder.append("&");

            try {
                stringBuilder.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    /**
     * @param conn
     * @return
     */
    private String getResponse(HttpURLConnection conn) {
        StringBuilder sb = new StringBuilder();

        try {
            InputStreamReader inputStream = new InputStreamReader(conn.getInputStream(), "UTF-8");
            BufferedReader bufferReader = new BufferedReader(inputStream);

            String str;
            while((str = bufferReader.readLine()) != null){
                sb.append(str + "\n");
            }

            if(bufferReader != null) {
                bufferReader.close();
            }

            return sb.toString();
        } catch (IOException e) {
            BaLog.e("getResponse error >> " + e.getMessage());
            return NO_HTTP_RESPONSE;
        }

//
//        try {
//            DataInputStream dis = new DataInputStream(conn.getInputStream());
//            byte[] data = new byte[1024];
//            int len = dis.read(data, 0, 1024);
//
//            dis.close();
//
//            if (len > 0)
//                return new String(data, 0, len);
//            else
//                return NO_HTTP_RESPONSE;
//        }
//        catch(Exception e) {
//            BaLog.e("getResponse error >> " + e.getMessage());
//            return NO_HTTP_RESPONSE;
//        }
    }

    @Override
    protected void onPostExecute(APICallResult result) {
        if(activity instanceof BaseListActivity && !showCenterLoading) {
            ((BaseListActivity) activity).hideLoadingFooter();
        } else if(activity instanceof BaseActivity && showCenterLoading) {
            activity.hideProgress();
        }

        if (HttpHostConnectException.class.getSimpleName().equals(result.resultString)) {
            if(!activity.isFinishing()) {
                activity.showOkDialog(R.string.error_not_responsed_server);
            }
            return;
        }

        try {
            if(!(result.responseCode == HttpURLConnection.HTTP_OK
                    || result.responseCode == HttpURLConnection.HTTP_CREATED
                    || result.responseCode == HttpURLConnection.HTTP_NO_CONTENT)) {
                // 200, 201, 204 응답외의 것들은 모두 에러 처리!!
                onAPIResponseListener.onErrorResponse(APIUrl, result.responseCode, result.resultString, null);
                return;
            }

            String resultString = result.resultString;

            if (resultString.startsWith("{")) {
                JSONObject jsonResult = new JSONObject(resultString);
                onAPIResponseListener.onResponse(APIUrl, result.responseCode, jsonResult);
            } else if (resultString.startsWith("[")) {
//                resultString = "{\"json_array\":[{\"comments_count\": 0, \"title\": \"hello\", \"author_id\": 1, \"comments\":["
//                + "{\"list\":[{\"test\": \"a\"}, {\"test\": \"a\"}, {\"test\": \"a\"}]}], \"recruit_status\": \"\", \"author_name\": \"test\", \"content\": \"alskdf\", \"registered_date\": \"2016-06-19T10:31:04.724Z\", \"id\": 1, \"recruit_count\": 6, \"attend_count\": 0}, {\"comments_count\": 0, \"title\": \"hh\", \"author_id\": 1, \"comments\": [], \"recruit_status\": \"\", \"author_name\": \"test\", \"content\": \"ff\", \"registered_date\": \"2016-06-19T11:26:22.978Z\", \"id\": 2, \"recruit_count\": 4, \"attend_count\": 0}, {\"comments_count\": 0, \"title\": \"jj\", \"author_id\": 1, \"comments\": [], \"recruit_status\": \"\", \"author_name\": \"test\", \"content\": \"gg\", \"registered_date\": \"2016-06-19T11:27:00.959Z\", \"id\": 3, \"recruit_count\": 4, \"attend_count\": 0}]}";

                resultString = "{\"json_array\":" + resultString + "}";
                BaLog.d("appended Result=" + resultString);
                JSONObject jsonResult = new JSONObject(resultString);

                // "json_array" 라는 키값으로 묶어서 jsonObject 로 전달
                onAPIResponseListener.onResponse(APIUrl, result.responseCode, jsonResult);
            } else if (resultString.equals(NO_HTTP_RESPONSE)) {
                onAPIResponseListener.onResponse(APIUrl, result.responseCode, null);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            onAPIResponseListener.onErrorResponse(APIUrl, ERROR_CODE_JSON_PARSING, "result is not valid JSON", e.getCause());
        } catch (NullPointerException e) {
            e.printStackTrace();
            onAPIResponseListener.onErrorResponse(APIUrl, ERROR_CODE_NULL_RESULT, "result is null", e.getCause());
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


    private HttpDelete makeHttpDelete(String url) {
        HttpDelete request = new HttpDelete(url);

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

    public interface OnAPIResponseListener {
        void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException;
        void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause);
    }

    class APICallResult {
        int responseCode;       // HTTP 요청에 대한 응답 코드
        String responseMsg;     // HTTP 요청에 대한 응답 메시지 (OK, Bad Gateway 등)
        String resultString;    // API 응답으로 오는 JSON 문자열

        public APICallResult(int responseCode, String responseMsg, String resultString) {
            this.responseCode = responseCode;
            this.responseMsg = responseMsg;
            this.resultString = resultString;
        }
    }

    public interface HttpRequestStrategy {
        String getServerUrl();
        void setHeader(HttpURLConnection conn, String method);
    }
}
