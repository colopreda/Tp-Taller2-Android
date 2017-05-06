package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.Album;
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
 * Created by apredazzi on 5/1/17.
 */

public interface AlbumService {

    @POST("albums")
    Call<ResponseBody> addAlbums(@Body Album album);

    @GET("albums")
    Call<ServerResponse> getAlbums();

    @GET("albums/{id}")
    Call<Album> getAlbum(@Path("id") int id);

    @PUT("albums/{id}")
    Call<Album> updateAlbum(@Path("id") int id, @Body Album album);

    @DELETE("albums/{id}")
    Call<ResponseBody> deteleAlbum(@Path("id") int id);

    @DELETE("albums/{album_id}/tracks/{track_id}")
    Call<ResponseBody> deteleSongFromAlbum(@Path("album_id") int album_id, @Path("track_id") int track_id);



}
