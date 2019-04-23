package com.randeepsingh.blogfeed;

import java.util.Date;

public class Comments extends CommentID {
    private String comment_value, user_id, post_id;
    private Date time_stamp;

    public Comments() {
    }

    public Comments(String comment_value, String user_id, Date time_stamp) {
        this.comment_value = comment_value;
        this.user_id = user_id;
        this.time_stamp = time_stamp;
    }

    public String getComment_value() {
        return comment_value;
    }

    public void setComment_value(String comment_value) {
        this.comment_value = comment_value;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}

