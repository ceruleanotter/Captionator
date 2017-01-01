package io.github.ceruleanotter.captionator;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.github.ceruleanotter.captionator.databinding.CaptionRecyclerViewItemBinding;
import io.github.ceruleanotter.captionator.models.Caption;
import io.github.ceruleanotter.captionator.models.CaptionatorImage;
import timber.log.Timber;

/**
 * Created by lyla on 12/26/16.
 */

public class CaptionRecyclerAdapter extends FirebaseRecyclerAdapter<Caption,CaptionRecyclerAdapter.CaptionHolder> {

    public static final String CAPTION_IMAGES_STORAGE_PATH = "captions_images";

    StorageReference mStorageLocation;

    public CaptionRecyclerAdapter(Class<Caption> modelClass, int modelLayout, Class<CaptionHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mStorageLocation = FirebaseStorage.getInstance().getReference().child(CAPTION_IMAGES_STORAGE_PATH);




    }

    @Override
    public CaptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CaptionRecyclerViewItemBinding binding = DataBindingUtil
                .inflate(layoutInflater, viewType, parent, false);

        return new CaptionHolder(binding);
    }

    @Override
    protected void populateViewHolder(CaptionHolder viewHolder, Caption model, int position) {
        viewHolder.bindCaption(model);
    }


    public static class CaptionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CaptionRecyclerViewItemBinding mBinding;
        Caption mCaption;



        public CaptionHolder(CaptionRecyclerViewItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.voteUpImageButton.setOnClickListener(this);
            mBinding.voteDownImageButton.setOnClickListener(this);
        }
        public void bindCaption(Caption c) {
            mCaption = c;
            mBinding.authorTextView.setText(c.getAuthor());
            mBinding.captionTextView.setText(c.getCaption());
            mBinding.ratingTextView.setText(Integer.toString(c.getUpvotes()-c.getDownvotes()));
        }

        @Override
        public void onClick(View view) {
            if (view == mBinding.voteDownImageButton) {
                Timber.d("Down vote");
            } else if (view == mBinding.voteUpImageButton) {
                Timber.d("Up vote");
            }
        }
    }
}
