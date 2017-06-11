package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.fiuba.apredazzi.tp_taller2_android.model.UserSong;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by apredazzi on 4/19/17.
 */

public interface SongsService {

    @POST("tracks")
    Call<ResponseBody> addSong(@Body Song song);

    @GET("tracks")
    Call<ServerResponse> getSongs();

    @GET("tracks/{id}")
    Call<ServerResponse> getSong(@Path("id") int id);

    @PUT("tracks/{id}")
    Call<Song> updateSong(@Path("id") int id);

    @DELETE("tracks/{id}")
    Call<ResponseBody> deleteSong(@Path("id") int id);

    @POST("tracks/{id}/popularity")
    Call<ResponseBody> rankSong(@Path("id") int id, @Body UserSong userSong);

    @POST("tracks/{id}/like")
    Call<ResponseBody> likeSong(@Path("id") int id, @Body UserSong userSong);

    @DELETE("tracks/{id}/like")
    Call<ResponseBody> unlikeSong(@Path("id") int id);

}
