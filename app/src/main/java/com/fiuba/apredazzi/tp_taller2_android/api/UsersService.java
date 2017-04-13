package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by apredazzi on 4/11/17.
 */

public interface UsersService {

    String BASE_URL = "https://music-io-shared-server.herokuapp.com/";

    @GET("users/me")
    Call<User> getUserMe();

    @GET("users")
    Call<List<User>> getAllUsers();


}
