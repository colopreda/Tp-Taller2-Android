package com.fiuba.apredazzi.tp_taller2_android.model;

/**
 * Created by apredazzi on 4/2/17.
 */

public class User {

    String id;
    String email;
    String firstName;
    String lastName;
    String password;

    public User(String email, String first_name, String last_name, String password) {
        this.email = email;
        this.firstName = first_name;
        this.lastName = last_name;
        this.password = password;
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
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
        return firstName;
    }

    public void setFirst_name(final String first_name) {
        this.firstName = first_name;
    }

    public String getLast_name() {
        return lastName;
    }

    public void setLast_name(final String last_name) {
        this.lastName = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

}
