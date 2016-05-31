package com.angel.black.baskettogether.user;

import com.angel.black.baskettogether.core.network.ServerInfo;
import com.angel.black.baskettogether.util.MyLog;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KimJeongHun on 2016-05-24.
 */
public class UserHelper {

    public static void authToDjangoServer() {
        try {
            HttpClientConnection httpClientConnection = new DefaultBHttpClientConnection(4096);
            HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("post", ServerInfo.DEV_SERVER_URL + "api-token-auth/");

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("username", "KimeJeongHun"));
            nameValuePairs.add(new BasicNameValuePair("password", "1234"));

            request.setEntity(new StringEntity(nameValuePairs.toString()));

            // Execute HTTP Post Request
            httpClientConnection.sendRequestEntity(request);
            httpClientConnection.flush();

            HttpResponse response = httpClientConnection.receiveResponseHeader();
            httpClientConnection.receiveResponseEntity(response);

            MyLog.d("response >> " + response.toString());
        } catch(HttpException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String POST(final String url,final JSONObject obj) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream inputStream = null;
                String result = "";
                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);

                    String json = obj.toString();
                    StringEntity se = new StringEntity(json);
                    httpPost.setEntity(se);

                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type","application/json");

                    HttpResponse httpResponse = httpClient.execute((HttpUriRequest)httpPost);
                    inputStream = httpResponse.getEntity().getContent();

                    if(inputStream != null){
                        result = iStreamToString(inputStream);
                    }else{
                        result = "Did not work!";
                    }
                    MyLog.d(result);
                }catch(Exception e){

                }
            }
        }).start();

        return "";
    }

    public static String postToLogin(final String url, final String authToken, final JSONObject obj) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream inputStream = null;
                String result = "";
                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);

                    String json = obj.toString();
                    StringEntity se = new StringEntity(json);
                    httpPost.setEntity(se);

                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type","application/json");
                    httpPost.setHeader("Authorization", "Token " + authToken);

                    HttpResponse httpResponse = httpClient.execute((HttpUriRequest)httpPost);
                    inputStream = httpResponse.getEntity().getContent();

                    if(inputStream != null){
                        result = iStreamToString(inputStream);
                    }else{
                        result = "Did not work!";
                    }
                    MyLog.d(result);
                }catch(Exception e){

                }
            }
        }).start();


        return "";
    }

    private static String convertInputStreamToString(InputStream inputStream) {
        byte[] buf = new byte[4096];

        try {
            while (inputStream.read(buf) != -1) {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        return null;
    }

    public static String iStreamToString(InputStream is1)
    {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is1), 4096);
        String line;
        StringBuilder sb =  new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String contentOfMyInputStream = sb.toString();
        return contentOfMyInputStream;
    }

}
