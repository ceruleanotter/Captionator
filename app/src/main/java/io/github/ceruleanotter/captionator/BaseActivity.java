package io.github.ceruleanotter.captionator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import timber.log.Timber;

/**
 * Created by lyla on 1/1/17.
 */

public class BaseActivity extends AppCompatActivity {
    protected FirebaseUser mCurrentUser;
    public static final int REQUEST_CODE_SIGN_IN = 1;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("Base Activity was called");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mCurrentUser = firebaseAuth.getCurrentUser();
                if (mCurrentUser == null) {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setIsSmartLockEnabled(false)
                                    .build(),
                            REQUEST_CODE_SIGN_IN);
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                return;
            }

            // Sign in canceled
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }

            // No network
            if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                Toast.makeText(this, "No network cannot sign in", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
