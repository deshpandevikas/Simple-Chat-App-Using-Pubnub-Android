package me.kevingleason.pubnubchat;

import java.io.Serializable;

/**
 * Created by Vikas Deshpande on 1/21/2017.
 */

public class User implements Serializable
{
    String fname, lname, password, email;

    public String getFnmae() {
        return fname;
    }

    public void setFnmae(String fnmae) {
        this.fname = fnmae;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "fnmae='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public User(String email, String password, String lname, String fname) {
        this.email = email;
        this.password = password;
        this.lname = lname;
        this.fname = fname;
    }
}
