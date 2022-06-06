package com.example.softwareproject;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender  {

    // Declarations of variables
    String userFcmToken;
            // The user's device token
    String title;
            // The title of the notification
    String body;
            // The body of the notification
    Context mContext;

    //Assignmment of the url and server
            // The activity's context
    private RequestQueue requestQueue;
            // Request queue to make a html request

    //Notifications function
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
            // The URL for sending a message
    private final String fcmServerKey
            ="AAAAsyl8G_k:APA91bEgX74jSiZ9ItbwZid0JXW9k5gkJRi1WBnw8mZQMTvtQTc1jSkk9GZvPFKm7onjpEPtvVU04GFDCTdnggOxPmeYWDUEVDXgdma18bDUYgmSenV8mlGc0GgIqVjQLvC3TMhcG5vs";
            // The server key for the firebase cloud messaging

    // Constructor of the class
    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext) {
        //Details on the notification
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
    }

    // Method to send notifications using json objects
    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mContext);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", R.drawable.ic_notification);
                // Icon message
            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                // Requesting to the URl
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                // Inputting the headers in the json object
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
