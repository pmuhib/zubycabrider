package com.zuby.user.zubbyrider.view.ridermanagement.util;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by citymapper-pc5 on 17/4/18.
 */

public class HttpConnectorSendJsonDataLatest
{
    private static String url_param;
    private static HttpURLConnection conn;

    public HttpConnectorSendJsonDataLatest(String url_param) {
        this.url_param = url_param;
    }

    public String postData(String postData)
    {
        InputStream is = null;
        InputStreamReader isr = null;
        StringBuffer sb = null; // To read data
        Log.e("EM", "The url is: " + url_param);

        try
        {
            URL url = new URL(url_param);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestProperty("content-type", "application/json");
            conn.setRequestMethod("POST");

            Log.e("EM", "The url is222222222: " + url_param);
            // conn.setDoInput(true);
            // Starts the query


            conn.connect();
            Log.e("EM", "The url is3333333332: " + url_param);
            // Send request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            int response_code = conn.getResponseCode();
            Log.e("EM", "Response code: " + response_code);
            // Read data from network
            if (response_code == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();

                // Add is to Reader for buffered data reading from server
                isr = new InputStreamReader(is);
                sb = new StringBuffer();
                char[] readBuffer = new char[1024];
                int count = 0;
                while ((count = isr.read(readBuffer)) != -1) {
                    sb.append(readBuffer, 0, count);
                }
                return sb.toString();
            } else {
                Log.e("Em","exception"+" "+new Exception());
                throw new Exception();

            }

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }

         catch (Exception e) {
            Log.e("EM", "Exception : " + e);
            return null;
        } finally {
            // Close inner stream.
            if (is != null) {
                try {
                    conn.disconnect();
                    is.close();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {

                }
            }
            sb = null;
        }
    }

}