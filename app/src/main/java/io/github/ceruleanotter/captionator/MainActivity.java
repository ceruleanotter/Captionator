package io.github.ceruleanotter.captionator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import io.github.ceruleanotter.captionator.models.CaptionatorImage;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PHOTO_PICKER = 1;
    private static final String CAPTION_IMAGES_DATABASE_PATH = "images" ;

    StorageReference mStorageLocation;
    DatabaseReference mDatabaseReferenceImages;

    ListView mImagesListView;
    CaptionatorImageListAdapter mImagesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_CODE_PHOTO_PICKER);
            }
        });

        // Get ref to database
        mDatabaseReferenceImages = FirebaseDatabase.getInstance().getReference().child(CAPTION_IMAGES_DATABASE_PATH);

        //Get a reference to the storage location
        mStorageLocation = FirebaseStorage.getInstance().getReference().child(CaptionatorImageListAdapter.CAPTION_IMAGES_STORAGE_PATH);



        // Setup the ListView
        mImagesListView = (ListView) findViewById(R.id.captionator_images_list_view);
        mImagesAdapter = new CaptionatorImageListAdapter(this,CaptionatorImage.class, R.layout.main_list_view_item, mDatabaseReferenceImages);
        mImagesListView.setAdapter(mImagesAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            String imageId = selectedImageUri.getLastPathSegment();

            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mStorageLocation.child(imageId);

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();


                            // Set the download URL to the message box, so that the user can send it to the database
                            CaptionatorImage image = new CaptionatorImage(
                                    downloadUrl.toString(),
                                    "anon",
                                    System.currentTimeMillis());
                            mDatabaseReferenceImages.push().setValue(image);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImagesAdapter.cleanup();
    }
}
