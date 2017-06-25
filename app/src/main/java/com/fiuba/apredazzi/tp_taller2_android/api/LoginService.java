package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.FBUser;
import com.fiuba.apredazzi.tp_taller2_android.model.Token;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.google.gson.JsonObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by apredazzi on 4/2/17.
 */

public interface LoginService {

    String BASE_URL = "https://music-io-shared-server.herokuapp.com/";

    @POST("users")
    Call<User> createUser(@Body User user);

    @POST("tokens")
    Call<Token> generateToken(@Body User user);

    @POST("tokens")
    Call<Token> generateToken(@Body FBUser fbuser);

    @GET("json")
    Call<JsonObject> getCountry();

}
