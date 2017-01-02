package io.github.ceruleanotter.captionator;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ceruleanotter.captionator.models.Caption;
import io.github.ceruleanotter.captionator.models.CaptionatorImage;
import timber.log.Timber;

public class CaptionActivity extends AppCompatActivity {

    @BindView(R.id.caption_image_view) ImageView mCaptionImageView;
    @BindView(R.id.fab) FloatingActionButton mFAB;
    @BindView(R.id.toolbar)Toolbar mToolBar;

    @BindView(R.id.captions_recycler_view) RecyclerView mCaptionsRecyclerView;
    CaptionRecyclerAdapter mCaptionAdapter;

    public static final String CAPTION_DATABASE_PATH = "captions";
    public static final String CAPTION_UP_VOTE_TOTAL_DATABASE_PATH = "upvotes";
    public static final String CAPTION_DOWN_VOTE_TOTAL_DATABASE_PATH = "downvotes";

    public static final String IMAGE_ID_EXTRA = "image_id_extra";

    DatabaseReference mImageReference;

    // TODO might need to change to strings
    DatabaseReference mCaptionReference;

    DatabaseReference mRootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRootReference = FirebaseDatabase.getInstance().getReference();

        // Get the image from the intent
        final String imageId = getIntent().getStringExtra(IMAGE_ID_EXTRA); // TODO store in constant

        // TODO Load the image into the image view
        // Get ref to database
        mImageReference = mRootReference.child(MainActivity.CAPTION_IMAGES_DATABASE_PATH).child(imageId);
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
        mCaptionReference = mRootReference.child(CAPTION_DATABASE_PATH).child(imageId);

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
                                Caption c = new Caption(addCaptionEditText.getText().toString(), "anon");
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
        mCaptionsRecyclerView.setLayoutManager(layoutManager);

        //Setup the adapter
        mCaptionAdapter = new CaptionRecyclerAdapter(
                Caption.class,
                R.layout.caption_recycler_view_item,
                CaptionRecyclerAdapter.CaptionHolder.class,
                mCaptionReference,
                this);
        mCaptionsRecyclerView.setAdapter(mCaptionAdapter);

    }

    private void vote(String mKey, final boolean isUp) {
        Timber.d("Down vote on key %s", mKey);
        DatabaseReference refCaption = mCaptionReference.child(mKey);

        refCaption.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                String currentUser = "anon";

                Caption c = mutableData.getValue(Caption.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }

                //check if there is a map and make one if not
                if (c.getVoters() == null) {
                    c.setVoters(new HashMap<String, Boolean>());
                }

                //if they've already voted
                if (c.getVoters().containsKey(currentUser)) {

                    if (c.getVoters().get(currentUser) != isUp) {
                        if (isUp) {
                            // from down vote to up vote
                            c.setUpvotes(c.getUpvotes() + 1);
                            c.setDownvotes(c.getDownvotes() - 1);

                        } else {
                            // from up vote to down vote
                            c.setUpvotes(c.getUpvotes() - 1);
                            c.setDownvotes(c.getDownvotes() + 1);
                        }
                        c.getVoters().put(currentUser, isUp);
                    }

                } else {
                 //otherwise if they haven't voted
                    if (isUp) {
                        c.setUpvotes(c.getUpvotes() + 1);
                    } else {
                        c.setDownvotes(c.getDownvotes() + 1);
                    }

                    c.getVoters().put(currentUser, isUp);
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
}
