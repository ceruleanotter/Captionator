package io.github.ceruleanotter.captionator;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.github.ceruleanotter.captionator.models.CaptionatorImage;

/**
 * Created by lyla on 12/26/16.
 */

public class CaptionatorImageRecyclerAdapter extends FirebaseRecyclerAdapter<CaptionatorImage,CaptionatorImageRecyclerAdapter.CaptionatorItemHolder> {

    public static final String CAPTION_IMAGES_STORAGE_PATH = "captions_images";

    StorageReference mStorageLocation;
    Context mContext;

    public CaptionatorImageRecyclerAdapter(Class<CaptionatorImage> modelClass, int modelLayout, Class<CaptionatorItemHolder> viewHolderClass, Query ref,  Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mStorageLocation = FirebaseStorage.getInstance().getReference().child(CAPTION_IMAGES_STORAGE_PATH);
        mContext = context;
    }

    @Override
    public void populateViewHolder(CaptionatorItemHolder vh, CaptionatorImage image, final int position) {
        vh.setImage(image.getUrl());
        vh.getmMainView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openActivity = new Intent(mContext, CaptionActivity.class);
                openActivity.putExtra(CaptionActivity.IMAGE_ID_EXTRA, getRef(position).getKey());
                mContext.startActivity(openActivity);

;            }
        });
    }




    public static class CaptionatorItemHolder extends RecyclerView.ViewHolder {
        private final ImageView mImage;
        private final View mMainView;

        public CaptionatorItemHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.list_image_view);
            mMainView = itemView;

        }

        public void setImage(String url) {
            Glide.with(itemView.getContext())
                    .load(url)
                    .into(mImage);
        }

        public View getmMainView() {
            return mMainView;
        }
    }
}
