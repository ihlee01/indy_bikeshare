package com.misabelleeli.pacers_bikeshare;

/**
 * Created by Lee on 2014-07-09.
 */
public class User {

    public long userId;
    public String username;
    public String password;

    public User(Long userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
}
