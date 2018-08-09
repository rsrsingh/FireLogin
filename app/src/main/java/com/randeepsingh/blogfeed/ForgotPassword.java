package com.randeepsingh.blogfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText editText;
    Button button;
    String mEmail;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editText=findViewById(R.id.forgotPass_input);
        button=findViewById(R.id.forgotPass_btn);
auth=FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText.equals("")){
                    Toast.makeText(ForgotPassword.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                }
                else if (!editText.equals("")){
                    mEmail=editText.getText().toString();
                    auth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ForgotPassword.this, "Check your email inobx for password reset.", Toast.LENGTH_SHORT).show();
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
