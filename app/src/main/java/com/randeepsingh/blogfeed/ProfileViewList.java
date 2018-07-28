package com.randeepsingh.blogfeed;

public class ProfileViewList extends BlogPostID {

    private String blogPostrID;
    private String thumb_imageUrl;

    public ProfileViewList() {
    }

    public ProfileViewList(String blogPostrID, String thumb_imageUrl) {
        this.blogPostrID = blogPostrID;
        this.thumb_imageUrl = thumb_imageUrl;
    }

    public ProfileViewList(String blogPostrID) {

        this.blogPostrID = blogPostrID;
    }

    public String getBlogPostrID() {

        return blogPostrID;
    }

    public void setBlogPostrID(String blogPostrID) {
        this.blogPostrID = blogPostrID;
    }

    public String getThumb_imageUrl() {
        return thumb_imageUrl;
    }

    public void setThumb_imageUrl(String thumb_imageUrl) {
        this.thumb_imageUrl = thumb_imageUrl;
    }
}
