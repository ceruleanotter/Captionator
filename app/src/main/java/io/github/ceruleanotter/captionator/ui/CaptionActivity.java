package io.github.ceruleanotter.captionator.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.ivankocijan.magicviews.views.MagicTextView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ceruleanotter.captionator.R;
import io.github.ceruleanotter.captionator.models.Caption;
import io.github.ceruleanotter.captionator.models.CaptionatorImage;
import io.github.ceruleanotter.captionator.utils.FirebaseUtilities;
import timber.log.Timber;

public class CaptionActivity extends BaseActivity {

    @BindView(R.id.caption_image_view) ImageView mCaptionImageView;
    @BindView(R.id.fab) FloatingActionButton mFAB;
    @BindView(R.id.toolbar)Toolbar mToolBar;
    @BindView(R.id.caption_magic_text_view) MagicTextView mCaptionTextView;


    @BindView(R.id.captions_recycler_view) RecyclerView mCaptionsRecyclerView;
    CaptionRecyclerAdapter mCaptionAdapter;

    public static final String IMAGE_ID_EXTRA = "image_id_extra";

    DatabaseReference mImageReference;

    // TODO might need to change to strings
    DatabaseReference mCaptionReference;

    ValueEventListener mCaptionListener;
    Query mFirstCaptionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the image from the intent
        final String imageId = getIntent().getStringExtra(IMAGE_ID_EXTRA);

        // Get ref to database
        mImageReference = FirebaseUtilities.getImageRef(imageId);
        Timber.d("The location is %s ", mImageReference.toString());

        mImageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast toast = Toast.makeText(CaptionActivity.this, "Image Deleted", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                } else {

                    CaptionatorImage captionImage = dataSnapshot.getValue(CaptionatorImage.class);
                    Glide.with(CaptionActivity.this)
                            .load(captionImage.getUrl())
                            .into(mCaptionImageView);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(CaptionActivity.this, "No permission to view image", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });



        // Get the database location for the captions
        mCaptionReference = FirebaseUtilities.getCaptionsRef(imageId);
        Query sortedCaptionReference = mCaptionReference.orderByChild("votes");

        mFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CaptionActivity.this);
                LayoutInflater inflater = CaptionActivity.this.getLayoutInflater();
                View rootView = inflater.inflate(R.layout.dialog_add_caption, null);
                final EditText addCaptionEditText = (EditText) rootView.findViewById(R.id.add_caption_edit_text);

                alertDialogBuilder.setView(rootView)
                /* Add action buttons */
                        .setPositiveButton("Add Caption", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseReference captionRef = mCaptionReference.push();
                                Caption c = new Caption(addCaptionEditText.getText().toString(), mCurrentUser.getDisplayName(), mCurrentUser.getUid());
                                captionRef.setValue(c);
                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        // Setup the RecyclerView
        mCaptionsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //Reverse list
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mCaptionsRecyclerView.setLayoutManager(layoutManager);

        //Setup the adapter
        mCaptionAdapter = new CaptionRecyclerAdapter(
                Caption.class,
                R.layout.caption_recycler_view_item,
                CaptionRecyclerAdapter.CaptionItemHolder.class,
                sortedCaptionReference,
                this);
        mCaptionsRecyclerView.setAdapter(mCaptionAdapter);

        /** Set the caption **/
        //Last because of silly ordering
        mFirstCaptionReference = mCaptionReference.orderByChild("votes").limitToLast(1);

        mCaptionListener = mFirstCaptionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChildren()) {
                    mCaptionTextView.setText("Be the first to add a caption");
                } else {
                    for (DataSnapshot capSnapshot : dataSnapshot.getChildren()) {
                        Timber.d("DataSnapshot is %s ", capSnapshot.toString());
                        Caption c = capSnapshot.getValue(Caption.class);
                        mCaptionTextView.setText(c.getCaption());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void vote(String mKey, final boolean isUp) {
        Timber.d("Down vote on key %s", mKey);
        DatabaseReference refCaption = mCaptionReference.child(mKey);

        refCaption.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                String currentUserId = mCurrentUser.getUid();

                Caption c = mutableData.getValue(Caption.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }

                //check if there is a map and make one if not
                if (c.getVoters() == null) {
                    c.setVoters(new HashMap<String, Boolean>());
                }

                //if they've already voted
                if (c.getVoters().containsKey(currentUserId)) {

                    if (c.getVoters().get(currentUserId) != isUp) {
                        if (isUp) {
                            // from down vote to up vote
                            c.setVotes(c.getVotes()+2);

                        } else {
                            // from up vote to down vote
                            // from down vote to up vote
                            c.setVotes(c.getVotes()-2);
                        }
                        c.getVoters().put(currentUserId, isUp);
                    }

                } else {
                 //otherwise if they haven't voted
                    if (isUp) {
                        // from down vote to up vote
                        c.setVotes(c.getVotes()+1);
                    } else {
                        // from down vote to up vote
                        c.setVotes(c.getVotes()-1);
                    }

                    c.getVoters().put(currentUserId, isUp);
                }

                // Set value and report transaction success
                mutableData.setValue(c);


                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                //Timber.e("postTransaction:onComplete");
            }
        });
    }

    public void downVote(String mKey) {
        Timber.d("Down Vote on key %s", mKey);
        vote(mKey, false);
    }
    public void upVote(String mKey) {
        Timber.d("Up Vote on key %s", mKey);
        vote(mKey, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirstCaptionReference.removeEventListener(mCaptionListener);
    }
}
