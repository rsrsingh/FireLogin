package com.randeepsingh.blogfeed;

import androidx.annotation.NonNull;

public class UserID {
    public String UserID;

    public <T extends UserID> T withID(@NonNull final String id) {
        this.UserID = id;
        return (T) this;
    }
}
