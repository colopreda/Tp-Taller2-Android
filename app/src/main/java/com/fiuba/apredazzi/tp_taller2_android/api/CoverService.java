package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.AlbumArt;
import com.fiuba.apredazzi.tp_taller2_android.model.CoverArt;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by apredazzi on 5/22/17.
 */

public interface CoverService {

    String BASE_URL = "http://ws.audioscrobbler.com/";
    String last_fm_api = "c5fbe7ad130c12408957b79e19e50d38";
    String format = "json";

    @GET("/2.0/")
    Call<AlbumArt> getCoverArt(@Query("method") String method, @Query("api_key") String api_key, @Query("artist") String artist, @Query("album") String album, @Query("format") String format);
}
