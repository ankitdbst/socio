package com.example.ankit.socio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private static final String EXTRA_BIO_TAG = "extra_bio";

    private int PLACE_PICKER_REQUEST = 1;
    private FirebaseUser mUser;
    private DatabaseReference mProfileRef;
    private ValueEventListener mProfileListener;

    private ImageView mProfilePicture;
    private TextView mProfileName;
    private TextView mBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_profile);

        mProfileName = (TextView) findViewById(R.id.profile_name);
        mBio = (TextView) findViewById(R.id.profile_bio);

        mProfilePicture = (ImageView) findViewById(R.id.profile_image);
        mProfileRef = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
    }

    private void updateUI(Map<String, Object> values) {
        if (values.containsKey("photoURL")) {
            Glide.with(ProfileActivity.this)
                    .load(values.get("photoURL"))
                    .thumbnail(0.1f)
                    .into(mProfilePicture);
        } else {
            Glide.with(ProfileActivity.this)
                    .load(R.drawable.default_profile_picture)
                    .into(mProfilePicture);
        }

        if (values.containsKey("fullName")) {
            mProfileName.setText((String) values.get("fullName"));
        }
        if (values.containsKey("bio")) {
            mBio.setText((String) values.get("bio"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateUI((Map<String, Object>) dataSnapshot.getValue());
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadProfile:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(ProfileActivity.this, "Failed to load profile.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mProfileRef.addValueEventListener(profileListener);
        mProfileListener = profileListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mProfileListener != null) {
            mProfileRef.removeEventListener(mProfileListener);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void editBio(View view) {
        Intent bioIntent = new Intent(ProfileActivity.this, BioEditActivity.class);
        bioIntent.putExtra(EXTRA_BIO_TAG, mBio.getText());
        ProfileActivity.this.startActivity(bioIntent);
    }

    private void selectLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_set_location:
                selectLocation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
