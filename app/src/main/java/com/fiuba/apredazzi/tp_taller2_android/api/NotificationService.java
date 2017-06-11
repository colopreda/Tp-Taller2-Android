package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.Notification;
import com.fiuba.apredazzi.tp_taller2_android.model.NotificationRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by apredazzi on 5/21/17.
 */

public interface NotificationService {

    String BASE_URL = "http://fcm.googleapis.com/";

    @Headers("Authorization: key=")// + API_KEY)
    @POST("/fcm/send")
    Call<ResponseBody> sendNotification(@Body NotificationRequest notification);

}
