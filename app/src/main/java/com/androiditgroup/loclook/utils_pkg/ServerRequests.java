package com.androiditgroup.loclook.utils_pkg;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androiditgroup.loclook.R;

import org.json.JSONObject;
import java.util.Map;

/**
 * Created by OS1 on 10.02.2016.
 */
public class ServerRequests {

    private Context context;

    private String      requestUrlTail;
    private String[]    requestParamsArr;
    private String      requestTailSeparator;

    private Map<String, String>     requestBody;
    private OnResponseReturnListener responseReturnListener;

    private ProgressDialog progressDialog;

    // private String requestUrlHead = "http://192.168.1.229:8000/v2/";
    // private String requestUrlHead = "http://192.168.1.230:8000/v2/";
    // private String requestUrlHead = "http://192.168.1.231:8000/v2/";
    private String requestUrlHead = "http://192.168.1.232:8000/v2/";

    final String LOG_TAG = "myLogs";

    //////////////////////////////////////////////

    // интерфейс для работы с вызывающими классами
    public interface OnResponseReturnListener {
        void onResponseReturn(JSONObject serverResponse);
    }

    //////////////////////////////////////////////

    //
    public ServerRequests(Context context) {

        this.context = context;

        // если вызывающий класс реализует интерфейс
        if (this.context instanceof OnResponseReturnListener)
            // получаем ссылку на него
            responseReturnListener = (OnResponseReturnListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnResponseReturnListener");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void setRequestUrlTail(String requestUrlTail) {
        this.requestUrlTail = requestUrlTail;
    }

    //
    public void setRequestParams(String[] requestParamsArr) {
        this.requestParamsArr = requestParamsArr;
    }

    //
    public void setRequestTailSeparator(String requestTailSeparator) {
        this.requestTailSeparator = requestTailSeparator;
    }

    //
    public void setRequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    //
    public void sendGetRequest() {

        showPD();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringBuilder request = new StringBuilder("" +requestUrlHead + requestUrlTail);

        if((requestParamsArr != null) && (requestParamsArr.length > 0)) {

            // request.append("/?");
            request.append(requestTailSeparator);

            for(int i=0; i<requestParamsArr.length; i++) {

                if((i > 0) && (requestParamsArr.length > 1))
                    request.append("&");

                request.append("" +requestParamsArr[i]);

//                if((i > 0) && (requestParamsArr.length > 1))
//                    request.append("&");
            }
        }

        // Log.d(LOG_TAG, "ServerRequest: sendGetRequest(): request= " +request.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, request.toString(), null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                // Log.d(LOG_TAG, "ServerRequest: sendGetRequest(): onResponse(): response= " + response.toString());
                responseReturnListener.onResponseReturn(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidePD();
                // hidePD("1");

                //
                responseReturnListener.onResponseReturn(null);
            }
        });

        queue.add(jsonObjectRequest);

        hidePD();
        // hidePD("2");
    }

    //
    public void sendPostRequest() {

        showPD();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringBuilder request = new StringBuilder("" +requestUrlHead + requestUrlTail);

        if((requestParamsArr != null) && (requestParamsArr.length > 0)) {

            // request.append("/");
            request.append(requestTailSeparator);

            for(int i=0; i<requestParamsArr.length; i++) {
                request.append("" +requestParamsArr[i]);

                if((i > 0) && (requestParamsArr.length > 1))
                    request.append("&");
            }

            requestParamsArr = null;
        }

        // Log.d(LOG_TAG, "ServerRequest: sendPostRequest(): request= " +request.toString());

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, request.toString(), requestBody, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                // Log.d(LOG_TAG, "==========================================================");
                // Log.d(LOG_TAG, "ServerRequest: sendPostRequest(): onResponse(): response= " + response.toString());

                try {
                    responseReturnListener.onResponseReturn(response);
                } catch (Exception e) {

                    hidePD();
                    // hidePD("3");

                    responseReturnListener.onResponseReturn(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidePD();
                // hidePD("4");

                //
                responseReturnListener.onResponseReturn(null);
            }
        });

        queue.add(jsonObjectRequest);

        hidePD();
        // hidePD("5");
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void showPD() {
        // Log.d(LOG_TAG, "========================================================");
        // Log.d(LOG_TAG, "showPD");

        if(progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.load_text));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    private void hidePD() {
    // private void hidePD(String msg) {

        // Log.d(LOG_TAG, "" +msg+ "_ServerRequest: hidePD()");

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}