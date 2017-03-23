package com.example.ankit.socio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class MainActivity extends Activity {

    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 200;
    private Button loginButton;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (isUserLogin()) {
            // already signed in
            loginUser();
        }

        setContentView(R.layout.activity_main);
        loginButton = (Button)findViewById(R.id.button_facebook_login);
        imageView = (ImageView) findViewById(R.id.waiting);
        Glide.with(this)
                .load(R.drawable.default_loading)
                .into(imageView);

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
        return auth.getCurrentUser() != null;
    }

    private void loginUser(){
        Intent loginIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
