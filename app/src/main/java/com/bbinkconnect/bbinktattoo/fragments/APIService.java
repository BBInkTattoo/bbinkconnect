package com.bbinkconnect.bbinktattoo.fragments;

import com.bbinkconnect.bbinktattoo.notifications.MyResponse;
import com.bbinkconnect.bbinktattoo.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAo2NTxyg:APA91bEpz1LVhxWa9cd4bY1CHH6mPYxG11DMALE-dLO9S7WTvaR5s3FNZA3DTyZ2DHBHePh-2CtZTeeKfY1KV3iKsccSOveKIwNorAoKznNqVTwNyeFCnfjj3SKRYNRtw-OgY02RaFWO"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
