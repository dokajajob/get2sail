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


public class GetLocationByUserType {
    private Context ctx;
    public String locationsResponse;


    public void executeGet(Context context, String user, final CallBack callBack) {
        String URL = "http://192.168.1.232:8000/api/usertypesearch/?user=" + user;
        ctx = context;
        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest getRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        callBack.onSuccess(response);

//                        locationsResponse = response;
                        Log.d("response_by_user_type : ", response);
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