package com.angel.black.baframework.network;

import com.angel.black.baframework.logger.BaLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class GeoPictureUploader {
    static String CRLF = "\r\n";
    static String twoHyphens = "--";
    static String boundary = "*****mgd*****";

    public enum ReturnCode { noPicture, unknown, succ, http201, http400, http401, http403, http404, http500};

    private ImageUploadRequestParamsStrategy imageUploadRequestParamsStrategy;

    public void setImageUploadRequestParamsStrategy(ImageUploadRequestParamsStrategy imageUploadRequestParamsStrategy) {
        this.imageUploadRequestParamsStrategy = imageUploadRequestParamsStrategy;
    }

    protected abstract String getFileUploadUrl();

    public ReturnCode uploadPicture() {
        try {
            URL connectURL = new URL(getFileUploadUrl());
            BaLog.d("connectUrl=" + connectURL.toString());
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection","Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            imageUploadRequestParamsStrategy.setExtraHeader(conn);

            conn.connect();

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());

            imageUploadRequestParamsStrategy.writeRequestParams(dataOutputStream);

            // final closing boundary line
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);
            dataOutputStream.flush();
            dataOutputStream.close();

            String response = getResponse(conn);
            int responseCode = conn.getResponseCode();
            BaLog.i("GeoPictureUploader", "responseCode=" + responseCode + ", response+" + response);

            if (responseCode == 200 || responseCode == 201) {
                return ReturnCode.succ;
            } else {
                return ReturnCode.http500;
            }
        }
        catch (MalformedURLException mue) {
            // Log.e(Tag, "error: " + mue.getMessage(), mue);
            mue.printStackTrace();
            System.out.println("GeoPictureUploader.uploadPicture: Malformed URL: " + mue.getMessage());
            return ReturnCode.http400;
        }
        catch (IOException ioe) {
            // Log.e(Tag, "error: " + ioe.getMessage(), ioe);
            ioe.printStackTrace();
            System.out.println("GeoPictureUploader.uploadPicture: IOE: " + ioe.getMessage());
            return ReturnCode.http500;
        }
        catch (Exception e) {
            // Log.e(Tag, "error: " + ioe.getMessage(), ioe);
            e.printStackTrace();
            System.out.println("GeoPictureUploader.uploadPicture: unknown: " + e.getMessage());
            return ReturnCode.unknown;
        }
    }

    /**
     * @param conn
     * @return
     */
    private String getResponse(HttpURLConnection conn) {
        try {
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            byte[] data = new byte[1024];
            int len = dis.read(data, 0, 1024);

            dis.close();
            int responseCode = conn.getResponseCode();

            if (len > 0)
                return new String(data, 0, len);
            else
                return "";
        }
        catch(Exception e) {
            BaLog.e("GeoPictureUploader: biffed it getting HTTPResponse, e.getMessage=" + e.getMessage());
            return "";
        }
    }

    /**
     *  this mode of reading response no good either
     */
    private String getResponseOrig(HttpURLConnection conn) {
        InputStream is = null;
        try {
            is = conn.getInputStream();
            // scoop up the reply from the server
            int ch;
            StringBuffer sb = new StringBuffer();
            while( ( ch = is.read() ) != -1 ) {
                sb.append( (char)ch );
            }
            return sb.toString();
        }
        catch(Exception e) {
            System.out.println("GeoPictureUploader: biffed it getting HTTPResponse");
            //Log.e(TAG, "GeoPictureUploader: biffed it getting HTTPResponse");
        }
        finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {}
        }

        return "";
    }

    /**
     * write one form field to dataSream
     * @param fieldName
     * @param fieldValue
     */
    protected void writeFormField(DataOutputStream dataStream, String fieldName, String fieldValue) {
        try {
            dataStream.writeBytes(twoHyphens + boundary + CRLF);
            dataStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"" + CRLF);
            dataStream.writeBytes(CRLF);
            dataStream.write(fieldValue.getBytes("utf-8"));
            dataStream.writeBytes(CRLF);
        }
        catch(Exception e) {
            System.out.println("GeoPictureUploader.writeFormField: got: " + e.getMessage());
            //Log.e(TAG, "GeoPictureUploader.writeFormField: got: " + e.getMessage());
        }
    }

    /**
     * write one file field to dataSream
     * @param fieldName - name of file field
     * @param fieldValue - file name
     * @param type - mime type
     * @param fis - stream of bytes that get sent up
     */
    protected void writeFileField(
            DataOutputStream dataStream,
            String fieldName,
            String fieldValue,
            String type,
            FileInputStream fis) {
        try {
            // opening boundary line
            dataStream.writeBytes(twoHyphens + boundary + CRLF);
            dataStream.writeBytes("Content-Disposition: form-data; name=\""
                    + fieldName
                    + "\";filename=\""
                    + fieldValue
                    + "\""
                    + CRLF);
            dataStream.writeBytes("Content-Type: " + type +  CRLF);
            dataStream.writeBytes(CRLF);

            // create a buffer of maximum size 
            int bytesAvailable = fis.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            // read file and write it into form... 
            int bytesRead = fis.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dataStream.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }

            // closing CRLF
            dataStream.writeBytes(CRLF);
        }
        catch(Exception e) {
            System.out.println("GeoPictureUploader.writeFormField: got: " + e.getMessage());
            //Log.e(TAG, "GeoPictureUploader.writeFormField: got: " + e.getMessage());
        }
    }

    public interface ImageUploadRequestParamsStrategy {
        void writeRequestParams(DataOutputStream outputStream) throws IOException;

        void setExtraHeader(HttpURLConnection conn);
    }
}