package com.eazy.firda.eazy.utils;

/**
 * Created by firda on 2/15/2018.
 */
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JsonObjectPostRequest extends Request<JSONObject> {

    private Map<String, String> mParam;
    private Response.Listener<JSONObject> mListener;
    public JsonObjectPostRequest(String url, Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener, Map param){
        super(Method.POST, url, errorListener);
        mListener = listener;
        mParam = param;
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParam;
    }
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e){
            return Response.error(new ParseError(e));
        } catch (JSONException je){
            return Response.error(new ParseError(je));
        }
    }
    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }
}
