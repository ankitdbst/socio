package com.example.ankit.socio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BioEditActivity extends AppCompatActivity {
    private EditText mEditText;
    private static final String EXTRA_BIO_TAG = "extra_bio";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String bio = intent.getStringExtra(EXTRA_BIO_TAG);

        setContentView(R.layout.activity_bio);
        mEditText = (EditText) findViewById(R.id.profile_edit_bio);
        mEditText.setText(bio);

        Button btnOk = (Button) findViewById(R.id.btn_bio_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBio(mEditText.getText().toString());
                finish();
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btn_bio_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void updateBio(String text) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(user.getUid()).child("bio").setValue(text);
    }
}
