package com.example.team10;

import androidx.appcompat.app.AppCompatActivity;
import io.apptik.widget.MultiSlider;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView tempText;
    Switch toggleFan;

    MultiSlider low_range;
    MultiSlider medium_range;
    MultiSlider high_range;

    TextView low_text;
    TextView medium_text;
    TextView high_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI initialization & handling
        tempText = findViewById(R.id.tempText);
        toggleFan = findViewById(R.id.toggleFan);

        low_text = findViewById(R.id.low_text);
        low_range = findViewById(R.id.tempRangeLow);
        low_range.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            int low, high;

            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    low = value;
                } else {
                    high = value;
                }
                low_text.setText("Low Speed (" + low + "° - " + high + "°)");
            }
        });

        medium_text = findViewById(R.id.medium_text);
        medium_range = findViewById(R.id.tempRangeMedium);
        medium_range.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            int low, high;

            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    low = value;
                } else {
                    high = value;
                }
                medium_text.setText("Medium Speed (" + low + "° - " + high + "°)");
            }
        });

        high_text = findViewById(R.id.high_text);
        high_range = findViewById(R.id.tempRangeHigh);
        high_range.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            int low, high;

            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    low = value;
                } else {
                    high = value;
                }
                high_text.setText("High Speed (" + low + "° - " + high + "°)");
            }
        });

        // Pull data from API for UI
        getTempRanges();

        // Loop for UI & API updates
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getLastTemp();

                updateSwitch();
            }
        }, 0, 2000);
    }

    /**
     * Update UI with last recorded temperature
     */
    public void getLastTemp() {
        new ApiRequest(this)
            .setURL("http://tp-api.zsluder.com/api/temperature")
            .setMethod(Request.Method.GET)
            .readFromURL()
            .onListener(new ApiRequest.VolleyListener() {
                @Override
                public void onRecieve(String response) {
                    System.out.println("[API] Received temp. update");
                    try {
                        JSONObject apiResponse = new JSONObject(response);
                        tempText.setText((apiResponse.get("F").toString().substring(0, 2) + "°"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFail(VolleyError volleyError) {
                    System.out.println(volleyError.getMessage());
                }
            });
    }

    /**
     * Gets saved temperature ranges from API
     */
    public void getTempRanges() {
        new ApiRequest(this)
            .setURL("http://tp-api.zsluder.com/api/temperature/ranges")
            .setMethod(Request.Method.GET)
            .readFromURL()
            .onListener(new ApiRequest.VolleyListener() {
                @Override
                public void onRecieve(String response) {
                    System.out.println("[API] Received temp. ranges update");
                    try {
                        JSONObject apiResponse = new JSONObject(response);

                        low_range.getThumb(0).setValue(Integer.parseInt(apiResponse.getJSONObject("low").get("from").toString()));
                        low_range.getThumb(1).setValue(Integer.parseInt(apiResponse.getJSONObject("low").get("to").toString()));

                        medium_range.getThumb(0).setValue(Integer.parseInt(apiResponse.getJSONObject("medium").get("from").toString()));
                        medium_range.getThumb(1).setValue(Integer.parseInt(apiResponse.getJSONObject("medium").get("to").toString()));

                        high_range.getThumb(0).setValue(Integer.parseInt(apiResponse.getJSONObject("high").get("from").toString()));
                        high_range.getThumb(1).setValue(Integer.parseInt(apiResponse.getJSONObject("high").get("to").toString()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFail(VolleyError volleyError) {
                    System.out.println(volleyError.getMessage());
                }
            });
    }

    /**
     * Gets whether fan is on/off from API
     */
    public void getSwitch() {
        new ApiRequest(this)
            .setURL("http://tp-api.zsluder.com/api/switch")
            .setMethod(Request.Method.GET)
            .readFromURL()
            .onListener(new ApiRequest.VolleyListener() {
                @Override
                public void onRecieve(String response) {
                    System.out.println("[API] Received switch update");
                    try {
                        JSONObject apiResponse = new JSONObject(response);

                        toggleFan.setChecked(apiResponse.get("switch").toString().equals("on"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFail(VolleyError volleyError) {
                    System.out.println(volleyError.getMessage());
                }
            });
    }

    /**
     * Tells API to turn fan on/off from user
     */
    public void updateSwitch() {
        new ApiRequest(this)
            .setURL("http://tp-api.zsluder.com/api/switch/" + (toggleFan.isChecked() ? "on" : "off"))
            .setMethod(Request.Method.POST)
            .readFromURL()
            .onListener(new ApiRequest.VolleyListener() {
                @Override
                public void onRecieve(String data) {
                    System.out.println("[API] Updated switch");

                    getSwitch();
                }
                @Override
                public void onFail(VolleyError volleyError) {
                    System.out.println(volleyError.getMessage());
                }
            });
    }
}
