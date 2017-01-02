package io.github.ceruleanotter.captionator.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ceruleanotter.captionator.R;
import io.github.ceruleanotter.captionator.models.CaptionatorImage;
import io.github.ceruleanotter.captionator.utils.FirebaseUtilities;

/**
 * Created by lyla on 12/26/16.
 */

public class ImageRecyclerAdapter extends FirebaseRecyclerAdapter<CaptionatorImage,ImageRecyclerAdapter.ImageItemHolder> {

    StorageReference mStorageReference;
    Context mContext;

    public ImageRecyclerAdapter(Class<CaptionatorImage> modelClass, int modelLayout, Class<ImageItemHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mStorageReference = FirebaseUtilities.getStorageRef();
        mContext = context;
    }

    @Override
    public void populateViewHolder(ImageItemHolder vh, CaptionatorImage image, final int position) {
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


    public static class ImageItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_image_view) ImageView mImage;
        private final View mMainView;

        public ImageItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
