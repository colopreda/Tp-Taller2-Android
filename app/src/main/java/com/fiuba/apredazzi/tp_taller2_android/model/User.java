package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 4/2/17.
 */

public class User {

    String id;
    String email;
    String first_name;
    String last_name;
    String password;

    public User(String email, String first_name, String last_name, String password) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public User(String email, String first_name, String last_name) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(final String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(final String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

}
