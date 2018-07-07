package com.example.randeepsingh.firelogin;

import java.util.Date;

class Comments extends CommentID {
    private String comment_value,user_id;
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
}
