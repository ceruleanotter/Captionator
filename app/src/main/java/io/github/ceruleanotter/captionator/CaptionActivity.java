package io.github.ceruleanotter.captionator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.github.ceruleanotter.captionator.models.Caption;
import io.github.ceruleanotter.captionator.models.CaptionatorImage;
import timber.log.Timber;

public class CaptionActivity extends AppCompatActivity {

    RecyclerView mCaptionsRecyclerView;
    CaptionRecyclerAdapter mCaptionAdapter;

    public static final String CAPTION_STORAGE_PATH = "captions";
    public static final String CAPTION_VOTES_STORAGE_PATH = "caption_votes";

    public static final String IMAGE_ID_EXTRA = "image_id_extra";

    DatabaseReference mImageLocation;
    DatabaseReference mCaptionLocation;
    DatabaseReference mVotesLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the image from the intent
        final String imageId = getIntent().getStringExtra(IMAGE_ID_EXTRA); // TODO store in constant

        // TODO Load the image into the image view
        // Get ref to database
        mImageLocation = FirebaseDatabase.getInstance().getReference().child(CaptionatorImageRecyclerAdapter.CAPTION_IMAGES_STORAGE_PATH).child(imageId);

        // Get the database location for the captions
        mCaptionLocation = FirebaseDatabase.getInstance().getReference().child(CAPTION_STORAGE_PATH).child(imageId);
        mVotesLocation = FirebaseDatabase.getInstance().getReference().child(CAPTION_VOTES_STORAGE_PATH).child(imageId);

        Timber.d("mVotesLocation is  %s", mVotesLocation);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = AddCaptionDialogFragment.newInstance(imageId);
                dialog.show(CaptionActivity.this.getSupportFragmentManager(), "AddCaptionDialogFragment");
            }
        });

//        // Setup the RecyclerView
//        mCaptionsRecyclerView = (RecyclerView) findViewById(R.id.captions_recycler_view);
//        mCaptionsRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        mCaptionsRecyclerView.setLayoutManager(layoutManager);
//
//        // Set a divider
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mCaptionsRecyclerView.getContext(),
//                layoutManager.getOrientation());
//        mCaptionsRecyclerView.addItemDecoration(dividerItemDecoration);
//
//        mCaptionAdapter = new CaptionRecyclerAdapter(
//                Caption.class,
//                R.layout.caption_recycler_view_item,
//                CaptionRecyclerAdapter.CaptionHolder.class,
//                mCaptionLocation);
//        mCaptionsRecyclerView.setAdapter(mCaptionAdapter);

    }


    public static class AddCaptionDialogFragment extends DialogFragment {

        private String mImageID;

        public static AddCaptionDialogFragment newInstance(String imageID) {
            AddCaptionDialogFragment dialog = new AddCaptionDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(IMAGE_ID_EXTRA, imageID);
            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mImageID = getArguments().getString(IMAGE_ID_EXTRA);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rootView = inflater.inflate(R.layout.dialog_add_caption, null);
            EditText addCaptionEditText = (EditText) rootView.findViewById(R.id.add_caption_edit_text);


//            addCaptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                    if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                        // Add list
//                    }
//                    return true;
//                }
//            });

            builder.setView(rootView)
                /* Add action buttons */
                    .setPositiveButton("Add Caption", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Timber.d("Add shopping list");
                        }
                    });

            return builder.create();
        }
    }


}