package com.sumang.mapsgoogle.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    public static JSONObject getJSONFromUrl(String url) {




        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            System.out.println("url " + url);
            System.out.println("http header : " + httpGet.getAllHeaders());
            HttpResponse httpResponse = httpClient.execute(httpGet);
            System.out.println("response = " + httpResponse);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            System.out.println("is = " + is);
        } catch (UnsupportedEncodingException e) {
            System.out.println("encoding ex");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            System.out.println("protocol ex");

            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("io ex");

            e.printStackTrace();
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line ="";
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            json = sb.toString();
        } catch (Exception e) {
            System.out.println("ebu = " + e);
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            System.out.println("json = " + json);
            jObj = new JSONObject(json);
            System.out.println("after parse ");
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
}