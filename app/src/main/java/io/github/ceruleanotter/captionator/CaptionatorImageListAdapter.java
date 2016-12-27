package io.github.ceruleanotter.captionator;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.github.ceruleanotter.captionator.models.CaptionatorImage;

/**
 * Created by lyla on 12/26/16.
 */

public class CaptionatorImageListAdapter extends FirebaseListAdapter<CaptionatorImage> {

    public static final String CAPTION_IMAGES_STORAGE_PATH = "captions_images";


    StorageReference mStorageLocation;
    Context mContext;

    /**
     * @param activity    The activity containing the ListView
     * @param modelClass  Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single list item. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param ref         The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public CaptionatorImageListAdapter(Activity activity, Class<CaptionatorImage> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        mStorageLocation = FirebaseStorage.getInstance().getReference().child(CAPTION_IMAGES_STORAGE_PATH);
        mContext = activity;
    }

    @Override
    protected void populateView(View v, CaptionatorImage imageModel, int position) {
        ImageView imageView = (ImageView) v.findViewById(R.id.list_image_view);
        Glide.with(mContext)
                .load(imageModel.getUrl())
                .into(imageView);
    }
}
