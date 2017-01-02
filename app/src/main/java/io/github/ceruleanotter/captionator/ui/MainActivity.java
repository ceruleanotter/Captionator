package io.github.ceruleanotter.captionator.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ceruleanotter.captionator.R;
import io.github.ceruleanotter.captionator.models.CaptionatorImage;
import io.github.ceruleanotter.captionator.utils.FirebaseUtilities;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_CODE_PHOTO_PICKER = 2;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseImagesReference;

    @BindView(R.id.fab) FloatingActionButton mFAB;
    @BindView(R.id.toolbar)Toolbar mToolBar;

    @BindView(R.id.captionator_images_recycler_view) RecyclerView mImagesRecyclerView;
    ImageRecyclerAdapter mImagesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);


        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_CODE_PHOTO_PICKER);
            }
        });

        // Get ref to database
        mDatabaseImagesReference = FirebaseUtilities.getImagesRef();

        //Get a reference to the storage location
        mStorageReference = FirebaseUtilities.getStorageRef();


        // Setup the RecyclerView
        mImagesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mImagesRecyclerView.setLayoutManager(layoutManager);

        // Set a divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mImagesRecyclerView.getContext(),
                layoutManager.getOrientation());
        mImagesRecyclerView.addItemDecoration(dividerItemDecoration);

        mImagesAdapter = new ImageRecyclerAdapter(
                CaptionatorImage.class,
                R.layout.main_recycler_view_item,
                ImageRecyclerAdapter.ImageItemHolder.class,
                mDatabaseImagesReference,
                this);
        mImagesRecyclerView.setAdapter(mImagesAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            String imageId = selectedImageUri.getLastPathSegment();

            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mStorageReference.child(imageId);

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();


                            // Set the download URL to the message box, so that the user can send it to the database
                            CaptionatorImage image = new CaptionatorImage(
                                    downloadUrl.toString(),
                                    mCurrentUser.getUid());
                            mDatabaseImagesReference.push().setValue(image);
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            AuthUI.getInstance().signOut(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImagesAdapter.cleanup();
    }
}
