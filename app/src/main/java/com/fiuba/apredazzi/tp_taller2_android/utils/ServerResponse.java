package com.fiuba.apredazzi.tp_taller2_android.utils;

import com.fiuba.apredazzi.tp_taller2_android.model.Album;
import com.fiuba.apredazzi.tp_taller2_android.model.Artist;
import com.fiuba.apredazzi.tp_taller2_android.model.Playlist;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by apredazzi on 4/29/17.
 */

public class ServerResponse {

    @SerializedName("user")
    private User user;

    @SerializedName("users")
    private List<User> users;

    @SerializedName("contacts")
    private List<User> contacts;

    @SerializedName("artists")
    private List<Artist> artists;

    @SerializedName("albums")
    private List<Album> albums;

    @SerializedName("album")
    private Album album;

    @SerializedName("tracks")
    private List<Song> songs;

    @SerializedName("track")
    private Song song;

    @SerializedName("playlists")
    private List<Playlist> playlists;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(final List<User> users) {
        this.users = users;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(final List<Artist> artists) {
        this.artists = artists;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(final List<Album> albums) {
        this.albums = albums;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(final Album album) {
        this.album = album;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(final List<Song> songs) {
        this.songs = songs;
    }

    public List<User> getContacts() {
        return contacts;
    }

    public void setContacts(final List<User> contacts) {
        this.contacts = contacts;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(final List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(final Song song) {
        this.song = song;
    }
}
