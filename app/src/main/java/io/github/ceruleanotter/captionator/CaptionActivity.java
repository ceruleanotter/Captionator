package io.github.ceruleanotter.captionator;

import android.content.DialogInterface;
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
import com.google.firebase.database.ValueEventListener;

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
    public static final String CAPTION_VOTES_DATABASE_PATH = "caption_votes";

    public static final String IMAGE_ID_EXTRA = "image_id_extra";

    DatabaseReference mImageLocation;
    DatabaseReference mCaptionLocation;
    DatabaseReference mVotesLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the image from the intent
        final String imageId = getIntent().getStringExtra(IMAGE_ID_EXTRA); // TODO store in constant

        // TODO Load the image into the image view
        // Get ref to database
        mImageLocation = FirebaseDatabase.getInstance().getReference().child(MainActivity.CAPTION_IMAGES_DATABASE_PATH).child(imageId);
        Timber.d("The location is %s ", mImageLocation.toString());

        mImageLocation.addValueEventListener(new ValueEventListener() {
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
        mCaptionLocation = FirebaseDatabase.getInstance().getReference().child(CAPTION_DATABASE_PATH).child(imageId);
        mVotesLocation = FirebaseDatabase.getInstance().getReference().child(CAPTION_VOTES_DATABASE_PATH).child(imageId);

        Timber.d("mVotesLocation is  %s", mVotesLocation);

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
                                DatabaseReference captionRef = mCaptionLocation.push();
                                Caption c = new Caption(addCaptionEditText.getText().toString(), "anon");
                                captionRef.setValue(c);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
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
                mCaptionLocation);
        mCaptionsRecyclerView.setAdapter(mCaptionAdapter);

    }
}
