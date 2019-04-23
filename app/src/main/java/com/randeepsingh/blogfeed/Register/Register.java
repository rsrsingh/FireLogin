package com.randeepsingh.blogfeed.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.randeepsingh.blogfeed.R;


public class Register extends AppCompatActivity {

    private TextInputLayout mEmail, mPass, mCpass;
    private Button btnSub;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail =  findViewById(R.id.reg_emaillayout);
        mPass =  findViewById(R.id.reg_Passlayout);
        btnSub = findViewById(R.id.reg_btnSub);
        mCpass =  findViewById(R.id.reg_cPasslayout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserReg();
            }
        });


    }

    private void UserReg() {

        String email, pass, cpass;
        email = mEmail.getEditText().getText().toString();
        pass = mPass.getEditText().getText().toString();
        cpass = mCpass.getEditText().getText().toString();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        if (!pass.equals(cpass)) {
            progressDialog.dismiss();
            Toast.makeText(this, "Password did not mactch", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please fill all the info", Toast.LENGTH_SHORT).show();

        } else {
            progressDialog.dismiss();
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(Register.this, "Failed to register ", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (task.isSuccessful()) {
                        startActivity(new Intent(Register.this, AccountReg.class));
                        finish();
                    }

                }
            });

        }

    }
}
