package com.example.team10;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ApiRequest {
    private Context mContext;
    private String mUrl;
    private int mMethod;
    private VolleyListener mVolleyListener;

    /**
     * Constructor
     */
    public ApiRequest(Context context) {
        mContext = context;
    }

    /**
     * Sets the request URL
     *
     * @param url: Full API url
     */
    public ApiRequest setURL(String url) {
        mUrl = url;

        return this;
    }

    /**
     * Updates current request mode
     *
     * @param method: GET or POST
     */
    public ApiRequest setMethod(int method) {
        mMethod = method;

        return this;
    }

    /**
     * Read URL returned data
     */
    public ApiRequest readFromURL() {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(mMethod, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mVolleyListener.onRecieve(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mVolleyListener.onFail(volleyError);
            }
        });

        requestQueue.add(stringRequest);

        return this;
    }

    /**
     * Listener for completed request
     */
    public ApiRequest onListener(VolleyListener volleyListener) {
        mVolleyListener = volleyListener;

        return this;
    }

    public interface VolleyListener {
        public void onRecieve(String data);

        public void onFail(VolleyError volleyError);
    }
}
