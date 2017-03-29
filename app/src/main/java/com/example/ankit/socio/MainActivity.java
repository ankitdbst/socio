package com.example.ankit.socio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ankit.socio.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 200;
    private Button loginButton;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        loginButton = (Button)findViewById(R.id.button_facebook_login);
        imageView = (ImageView) findViewById(R.id.waiting);
        Glide.with(this)
                .load(R.drawable.default_loading)
                .into(imageView);

        if (isUserLogin()) {
            // already signed in
            loginUser();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                .setAllowNewEmailAccounts(false)
                                .setIsSmartLockEnabled(false)
                                .build(),
                        RC_SIGN_IN);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                writeUserInfo();
                loginUser();
                return;
            } else {
                loginButton.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    displayMessage(getString((R.string.sign_in_cancelled)));
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    displayMessage(getString((R.string.no_internet_connection)));
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    displayMessage(getString((R.string.unknown_error)));
                    return;
                }
            }

            displayMessage(getString(R.string.unknown_sign_in_response));
        }
    }

    private boolean isUserLogin(){
        return mAuth.getCurrentUser() != null;
    }

    private void writeUserInfo() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final User user = new User();
        user.uid = firebaseUser.getUid();
        user.email = firebaseUser.getEmail();
        user.fullName = firebaseUser.getDisplayName();
        if (firebaseUser.getPhotoUrl() != null) {
            user.photoURL = firebaseUser.getPhotoUrl().toString();
        }
//
//        // Facebook photo
//        String facebookUserId = null;
//        // find the Facebook profile and get the user's id
//        for (UserInfo profile : firebaseUser.getProviderData()) {
//            // check if the provider id matches "facebook.com"
//            if (profile.getProviderId().equals(getString(R.string.facebook_provider_id))) {
//                facebookUserId = profile.getUid();
//            }
//        }
//        if (facebookUserId != null) {
//            // construct the URL to the profile picture, with a custom height
//            // alternatively, use '?type=small|medium|large' instead of ?height=
//            user.photoURL = "https://graph.facebook.com/" + facebookUserId + "/picture?height=144";
//        }
//
//        // Get extra fields - gender etc, from facebook
//        GraphRequest request = GraphRequest.newMeRequest(
//                AccessToken.getCurrentAccessToken(),
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(
//                            JSONObject object,
//                            GraphResponse response) {
//                        // Application code
//                        try {
//                            user.gender = object.getString("gender");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "gender");
//        request.setParameters(parameters);
//        request.executeAndWait();

        // Write to database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(user.uid).setValue(user);
    }

    private void loginUser() {
        Intent loginIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
