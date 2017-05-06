package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by apredazzi on 4/11/17.
 */

public interface UsersService {

    String BASE_URL = "https://music-io-shared-server.herokuapp.com/";

    @GET("users/me")
    Call<ServerResponse> getUserMe();

    @PUT("users/me")
    Call<ResponseBody> modifyUserMe(@Body User user);

    @GET("users")
    Call<ServerResponse> getAllUsers();


}
