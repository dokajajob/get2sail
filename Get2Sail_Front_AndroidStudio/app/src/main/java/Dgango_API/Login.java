package Dgango_API;


import android.content.SharedPreferences;
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


public class Login {

    private Context context;
    public static final String targetURL = "http://192.168.1.232:8000/auth/";

    //New Login
    public Serializable executePost(String userName, String userPasswd) throws JSONException {

        StringBuffer response = new StringBuffer();

        GPS gps = new GPS();
        gps.setUsername(userName);
        gps.setPassword(userPasswd);
        Gson gson = new Gson();
        String user_pass_json_user = gson.toJson(gps);


                HttpURLConnection httpConnection = null;
                try {
                    //Connection
                    URL target = new URL(targetURL);
                    httpConnection = (HttpURLConnection) target.openConnection();

                    httpConnection.setDoOutput(true);
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setRequestProperty("Content-Type", "application/json");
                    httpConnection.connect();

                    //Request
                    OutputStream outputStream = httpConnection.getOutputStream();
                    outputStream.write(user_pass_json_user.getBytes());
                    outputStream.flush();

                    if (httpConnection.getResponseCode() != 200) {
                        return ("Failed : HTTP error code : " + httpConnection.getResponseCode());
                    }

                    //Response
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


