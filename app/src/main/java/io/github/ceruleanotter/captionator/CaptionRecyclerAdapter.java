package io.github.ceruleanotter.captionator;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ceruleanotter.captionator.models.Caption;
import timber.log.Timber;

/**
 * Created by lyla on 12/26/16.
 */

public class CaptionRecyclerAdapter extends FirebaseRecyclerAdapter<Caption, CaptionRecyclerAdapter.CaptionHolder> {

    public static final String CAPTION_IMAGES_STORAGE_PATH = "captions_images";

    StorageReference mStorageLocation;

    public CaptionRecyclerAdapter(Class<Caption> modelClass, int modelLayout, Class<CaptionHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mStorageLocation = FirebaseStorage.getInstance().getReference().child(CAPTION_IMAGES_STORAGE_PATH);
    }


    @Override
    protected void populateViewHolder(CaptionHolder viewHolder, Caption model, int position) {
        viewHolder.bindCaption(model);
    }


    public static class CaptionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Caption mCaption;
        @BindView(R.id.author_text_view)
        TextView authorTextView;
        @BindView(R.id.caption_text_view)
        TextView captionTextView;
        @BindView(R.id.rating_text_view)
        TextView ratingTextView;
        @BindView(R.id.vote_up_image_button)
        ImageButton voteUpImageButton;
        @BindView(R.id.vote_down_image_button)
        ImageButton voteDownImageButton;


        public CaptionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            voteUpImageButton.setOnClickListener(this);
            voteDownImageButton.setOnClickListener(this);
        }

        public void bindCaption(Caption c) {
            mCaption = c;
            authorTextView.setText(c.getAuthor());
            captionTextView.setText(c.getCaption());
            ratingTextView.setText(Integer.toString(c.getUpvotes() - c.getDownvotes()));
        }

        @Override
        public void onClick(View view) {
            if (view == voteDownImageButton) {
                Timber.d("Down vote");
            } else if (view == voteUpImageButton) {
                Timber.d("Up vote");
            }
        }
    }
}
