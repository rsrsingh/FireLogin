package com.example.randeepsingh.firelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Register extends AppCompatActivity {

    EditText mUser,mPass,mCpass;
    Button btnSub;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    mUser=(EditText) findViewById(R.id.reg_user);
    mPass=(EditText) findViewById(R.id.reg_pass);
    btnSub=(Button) findViewById(R.id.reg_btn);
    mCpass=(EditText) findViewById(R.id.reg_cpass);

    mAuth=FirebaseAuth.getInstance();


    btnSub.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UserReg();
        }
    });


    }

    private void UserReg() {

        String email,pass,cpass;
        email=mUser.getText().toString();
        pass=mPass.getText().toString();
        cpass=mCpass.getText().toString();
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        if(!pass.equals(cpass)){
            progressDialog.dismiss();
            Toast.makeText(this, "Password did not mactch", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass))
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Please fill all the info", Toast.LENGTH_SHORT).show();

        }
        else
        {
            progressDialog.dismiss();
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(Register.this, "Failed to register ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else if(task.isSuccessful()){
                        startActivity(new Intent(Register.this,AccountReg.class));
                        finish();
                    }

                }
            });

        }

    }
}
