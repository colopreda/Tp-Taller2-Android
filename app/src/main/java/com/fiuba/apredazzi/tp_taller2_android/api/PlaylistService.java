package com.fiuba.apredazzi.tp_taller2_android.api;

import com.fiuba.apredazzi.tp_taller2_android.model.Playlist;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by apredazzi on 6/6/17.
 */

public interface PlaylistService {

    @POST("playlists")
    Call<ResponseBody> addPlaylists(@Body Playlist playlist);

    @GET("playlists")
    Call<ServerResponse> getPlaylists();

    @GET("playlists/{id}")
    Call<ServerResponse> getPlaylist(@Path("id") int id);

    @PUT("playlists/{id}")
    Call<Playlist> updatePlaylist(@Path("id") int id, @Body Playlist playlist);

    @DELETE("playlists/{id}")
    Call<ResponseBody> deletePlaylist(@Path("id") int id);

    @DELETE("playlists/{playlist_id}/tracks/{track_id}")
    Call<ResponseBody> deleteSongFromPlaylist(@Path("playlist_id") int playlist_id, @Path("track_id") int track_id);

    @PUT("playlists/{playlist_id}/tracks/{track_id}")
    Call<ResponseBody> addSongToPlaylist(@Path("playlist_id") int playlist_id, @Path("track_id") int track_id);

    @PUT("playlists/{playlist_id}/albums/{album_id}")
    Call<ResponseBody> addAlbumToPlaylist(@Path("playlist_id") int playlist_id, @Path("album_id") int album_id);

    @DELETE("playlists/{playlist_id}/albums/{album_id}")
    Call<ResponseBody> deleteAlbumFromPlaylist(@Path("playlist_id") int playlist_id, @Path("album_id") int album_id);

    @GET("playlists/{id}/tracks")
    Call<ServerResponse> getSongsFromPlaylist(@Path("id") int id);

    @GET("playlists/{id}/albums")
    Call<ServerResponse> getAlbumsFromPlaylist(@Path("id") int id);
}
