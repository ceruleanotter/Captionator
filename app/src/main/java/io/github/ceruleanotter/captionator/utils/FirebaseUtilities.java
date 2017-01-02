package io.github.ceruleanotter.captionator.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by lyla on 1/2/17.
 */

public class FirebaseUtilities {

    private static final String CAPTION_DATABASE_PATH = "captions";
    private static final String CAPTION_VOTE_TOTAL_DATABASE_PATH = "votes";
    private static final String CAPTION_IMAGES_DATABASE_PATH = "images" ;

    private static final String CAPTION_IMAGES_STORAGE_PATH = "captions_images";


    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getCaptionsRef(String imageId) {
        return getBaseRef().child(CAPTION_DATABASE_PATH).child(imageId);
    }

    public static DatabaseReference getImagesRef() {
        return getBaseRef().child(CAPTION_IMAGES_DATABASE_PATH);
    }

    public static DatabaseReference getImageRef(String imageId) {
        return getBaseRef().child(CAPTION_IMAGES_DATABASE_PATH).child(imageId);
    }


    public static StorageReference getStorageRef() {
        return FirebaseStorage.getInstance().getReference().child(CAPTION_IMAGES_STORAGE_PATH);
    }



}
