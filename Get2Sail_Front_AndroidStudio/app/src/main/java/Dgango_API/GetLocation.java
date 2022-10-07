package Dgango_API;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


//public class GetLocation {
//
//    public static final String URL = "http://192.168.1.83:8000/api/location/";
//    private RequestQueue mRequestQueue;
//    private StringRequest mStringRequest;
//    private String locationResponse;
//    private Context ctx;
//
//
//
//    public String executeGet(Context context, String user) {
//        ctx = context;
//
//        //Strict mode for HTTPS
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        //Volley library for API extraction
//        mRequestQueue = Volley.newRequestQueue(ctx);
//
//        //Get Location API
//        mStringRequest = new StringRequest(Request.Method.GET, URL, response -> {
//
//            Log.d("Response: ", response.toString());
//            try {
//                JSONObject obj = new JSONObject(response);
//                JSONObject request = obj.getJSONObject("request");
//                Log.d("Request:", String.valueOf(request));
//                //City Search in API
//                if (!(request.getString("query").contains("request_failed"))) {
//                     locationResponse = request.getString("query");
//                     Log.d("locationResponse", locationResponse);
//
//
//
////        //Temperature Search in API
////        JSONObject current = obj.getJSONObject("current");
////        String temperature = current.getString("temperature");
////        Log.d("temperature:", temperature);
//
////    JSONArray weather_icons = currentWI.getJSONArray("weather_icons");
//
//
//                } else {
//                    Log.d("WRONG_Location_Request","wrong location request" );
//                    Toast.makeText(ctx, "WRONG_Location_Request", Toast.LENGTH_LONG).show();
//                }
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//                Log.d("Error = ", e.getMessage());
//                Toast.makeText(ctx, "Wrong URL For Weather Provided", Toast.LENGTH_LONG).show();
//            }
//
//        }, error -> Log.d("Error = ", error.getMessage()));
//        mRequestQueue.add(mStringRequest);
//
//        return locationResponse;
//    }
//}

//interface CallBack {
//
//    String methodToCallBack();
//}


public class GetLocation {
    private Context ctx;
    public String locationsResponse;
    public static final String URL = "http://192.168.1.247:8000/api/location/";

    public void executeGet(Context context, String user, final CallBack callBack) {
        ctx = context;
        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest getRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        callBack.onSuccess(response);

//                        locationsResponse = response;
                        Log.d("response : ", response);
//                        Log.d("response type: ", response.getClass().getName());
//                        Log.d("locationsResponse", locationsResponse);
//                        Log.d("locationsResponse type", locationsResponse.getClass().getName());
                        Toast.makeText(ctx, "Got New Locations Update", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callBack.onError();
                        Log.d("ERROR", "error => " + error.toString());
                        Toast.makeText(ctx, "Locations Update ERROR: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Android User");
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        queue.add(getRequest);
    }

    public interface CallBack {
        void onSuccess(String response);
        void onError();
    }

}