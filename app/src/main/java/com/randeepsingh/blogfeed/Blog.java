package com.randeepsingh.blogfeed;

import java.util.Date;

public class Blog extends com.randeepsingh.blogfeed.BlogPostID {

    private String description_value, thumb_imageUrl, full_name, thumb_id, User_id;
    private Date Time_stamp;

    public Blog() {
    }


    public String getDescription_value() {
        return description_value;
    }

    public void setDescription_value(String description_value) {
        this.description_value = description_value;
    }

    public String getThumb_imageUrl() {
        return thumb_imageUrl;
    }

    public void setThumb_imageUrl(String thumb_imageUrl) {
        this.thumb_imageUrl = thumb_imageUrl;
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

    public Date getTime_stamp() {
        return Time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        Time_stamp = time_stamp;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public Blog(String description_value, String thumb_imageUrl, String full_name, String thumb_id, Date time_stamp) {


        this.description_value = description_value;
        this.thumb_imageUrl = thumb_imageUrl;
        this.full_name = full_name;
        this.thumb_id = thumb_id;
        Time_stamp = time_stamp;
    }
}
