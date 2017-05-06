package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
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
 * Created by apredazzi on 4/30/17.
 */

public interface ArtistService {

    @POST("artists")
    Call<ResponseBody> addArtist(@Body Artist Artist);

    @GET("artists")
    Call<ServerResponse> getArtists();

    @GET("artists/{id}")
    Call<Artist> getArtist(@Path("id") int id);

    @GET("artists/{id}/tracks")
    Call<List<Song>> getSongsFromArtist(@Path("id") int id);

    @PUT("artists/{id}")
    Call<Artist> updateArtist(@Path("id") int id, @Body Artist Artist);

    @DELETE("artists/{id}")
    Call<ResponseBody> deleteArtist(@Path("id") int id);

    @DELETE("artists/{id}/follow")
    Call<ResponseBody> unfollowArtist(@Path("id") int id);

    @POST("artists/{id}/follow")
    Call<ResponseBody> followArtist(@Path("id") int id, @Body boolean follow);

}
