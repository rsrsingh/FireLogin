package com.example.randeepsingh.firelogin;

import android.support.annotation.NonNull;

public class UserID {
    public String UserID;
    public <T extends UserID> T withID(@NonNull final String id) {
        this.UserID = id;
        return (T) this;
    }
}
