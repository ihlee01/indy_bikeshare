package com.misabelleeli.pacers_bikeshare;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by M.Isabel on 8/23/2014.
 */
public class JSONParser {

    static String response = null;
    static JSONArray jArray = null;

    String apiKey = "CDF97241-9E01-4C18-AEA9-1DBA42651EA8";

    public JSONParser(){}

    //returns a JSONArray with all the stations information from the b-cycle API
    public JSONArray getJSONFromUrl(String url) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("apiKey",apiKey);
            httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();

            String result = "";

            //Setting it up into the jArray
            InputStream is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            is.close();
            result = sb.toString();
            jArray = new JSONArray(result);
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return jArray;
    }

}
