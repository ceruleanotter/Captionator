package io.github.ceruleanotter.captionator.ui;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ceruleanotter.captionator.R;
import io.github.ceruleanotter.captionator.models.Caption;

/**
 * Created by lyla on 12/26/16.
 */

public class CaptionRecyclerAdapter extends FirebaseRecyclerAdapter<Caption, CaptionRecyclerAdapter.CaptionItemHolder> {
    CaptionActivity mCaptionActivity;

    public CaptionRecyclerAdapter(Class<Caption> modelClass, int modelLayout, Class<CaptionItemHolder> viewHolderClass, Query ref, CaptionActivity captionActivity) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mCaptionActivity = captionActivity;

    }


    @Override
    protected void populateViewHolder(CaptionItemHolder viewHolder, Caption model, int position) {
        viewHolder.bindCaption(model, this.getRef(position).getKey(), mCaptionActivity);
    }


    public static class CaptionItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Caption mCaption;
        String mKey;
        CaptionActivity mCaptionActivity;

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


        public CaptionItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            voteUpImageButton.setOnClickListener(this);
            voteDownImageButton.setOnClickListener(this);
        }

        public void bindCaption(Caption c, String key, CaptionActivity activity) {
            mCaption = c;
            mKey = key;
            authorTextView.setText(c.getAuthor());
            captionTextView.setText(c.getCaption());
            ratingTextView.setText(Integer.toString(c.getVotes()));
            // TODO looking into binding this every time
            mCaptionActivity  = activity;
        }

        @Override
        public void onClick(View view) {
            if (view == voteDownImageButton) {
                mCaptionActivity.downVote(mKey);


            } else if (view == voteUpImageButton) {
                mCaptionActivity.upVote(mKey);
            }
        }
    }
}
