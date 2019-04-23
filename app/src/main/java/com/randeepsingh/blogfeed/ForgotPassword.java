package com.randeepsingh.blogfeed;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private TextInputLayout userEmail;
    private Button btnSub;
    private String mEmail;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userEmail = findViewById(R.id.forgotpass_emaillayout);
        btnSub = findViewById(R.id.forgotPass_btnSub);
        auth = FirebaseAuth.getInstance();

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userEmail.getEditText().getText().toString().equals("")) {
                    Toast.makeText(ForgotPassword.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                } else if (!userEmail.getEditText().getText().toString().equals("")) {
                    mEmail = userEmail.getEditText().getText().toString();
                    auth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ForgotPassword.this, "Check your email inobx to reset password.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ForgotPassword.this, "Failed. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


    }
}
