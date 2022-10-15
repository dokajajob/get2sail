package Dgango_API;


import android.os.StrictMode;

import com.google.gson.Gson;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import Utils.GPS;

import android.content.Context;
import android.util.Log;


public class PostLocation {

    public static final String targetURL = "http://192.168.1.232:8000/api/location/";
    private Context mContext;



    //New Login
    public Serializable executePost(String lat, String lng, String uid, String user, String date) throws JSONException {

        //Strict mode for HTTPS
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StringBuffer response = new StringBuffer();

        GPS gps = new GPS();
        gps.setLat(lat);
        gps.setLng(lng);
        gps.setUid(uid);
        gps.setUser_is(user);
        gps.setDate(date);
        Gson gson = new Gson();
        String user_pass_json_user = gson.toJson(gps);
        Log.d("user_pass_json_user", user_pass_json_user);


        HttpURLConnection httpConnection = null;
        try {
            //Criando a conexão
            URL target = new URL(targetURL);
            httpConnection = (HttpURLConnection) target.openConnection();

            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.connect();


            //Enviando Request
            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(user_pass_json_user.getBytes());
            outputStream.flush();

            if (httpConnection.getResponseCode() != 200) {
                return ("Failed : HTTP error code : " + httpConnection.getResponseCode());
            }

            //Recebendo Response
            InputStream is = httpConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            return response;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "MalformedURLException";

        } catch (IOException e) {
            e.printStackTrace();
            return "" + httpConnection.getErrorStream();
        } finally {

            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

    }
}


