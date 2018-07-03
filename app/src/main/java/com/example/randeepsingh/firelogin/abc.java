/*
package com.example.randeepsingh.firelogin;

public class abc {
    //delete feature
        holder.deleteImage.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        //deleting photos from storage
        firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String post_name = task.getResult().getString("post_name");

                    if (!post_name.isEmpty()) {
                        final FirebaseStorage storageReference = FirebaseStorage.getInstance();
                        StorageReference delfile = storageReference.getReferenceFromUrl(image_url);
                        delfile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ///the post deleted from the storage
                                if (task.isSuccessful()) {

                                    StorageReference delThumb = storageReference.getReferenceFromUrl(thumb_image_url);
                                    delThumb.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (task.isSuccessful()) {
                                                //thumbnail deleted successful

                                                //deleting post from database
                                                firebaseFirestore.collection("Posts").document(blogPostID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            postList.remove(position);
                                                            user_list.remove(position);
                                                            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.e("deleting post", "error in deleting entries from database " + task.getException().getMessage());
                                                            //error in deleting entries from database
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.e("deleting post", "error deleting thumbnail from storage " + task.getException().getMessage());
                                                //error in thumbnail deletion
                                            }
                                        }
                                    });

                                } else {
                                    Log.e("deleting post", "error deleting file from storage " + task.getException().getMessage());
                                    //error deleting file from storage
                                }
                            }
                        });
                    } else {
                        Log.e("deleting post", "post not found " + task.getException().getMessage());
                        //post not found
                    }
                }
            }
        });
    }
    });
}
*/
