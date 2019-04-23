package com.randeepsingh.blogfeed;

public class User extends UserID {
    private String full_name, thumb_id;

    public User() {
    }

    public User(String full_name, String thumb_id) {
        this.full_name = full_name;
        this.thumb_id = thumb_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getThumb_id() {
        return thumb_id;
    }

    public void setThumb_id(String thumb_id) {
        this.thumb_id = thumb_id;
    }
}
