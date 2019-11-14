package com.example.team10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView tempText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempText = findViewById(R.id.tempText);
        RequestQueue queue = Volley.newRequestQueue(this);

        new Timer().scheduleAtFixedRate(new TimerTask(queue) {
            @Override
            public void run() {
                getLastTemp(queue);
            }
        }, 0, 1000);
    }

    /**
     * Update UI with last recorded temperature
     *
     * @param queue: Queue of requests
     */
    public void getLastTemp(RequestQueue queue) {
        String apiUrl = "http://tp-api.zsluder.com/api/temperature";

        StringRequest request = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject apiResponse = new JSONObject(response);
                    tempText.setText((apiResponse.get("F").toString().substring(0, 2) + "°"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        queue.add(request);
    }
}
