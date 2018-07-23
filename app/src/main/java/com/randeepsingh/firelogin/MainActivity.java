package com.randeepsingh.firelogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmail, mPass;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private TextView txtReg;
    private ImageView mGmail;
private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtReg = (TextView) findViewById(R.id.reg);
        mEmail = (AutoCompleteTextView) findViewById(R.id.email);
        mPass = (AutoCompleteTextView) findViewById(R.id.pass);
        btnLogin = (Button) findViewById(R.id.loginbtn);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
firebaseFirestore=FirebaseFirestore.getInstance();

                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, AccountMain.class));
                    finish();
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();
            }
        });
        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });


    }

    private void startSignIn() {

        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {

            Toast.makeText(this, "Please enter all the info", Toast.LENGTH_SHORT).show();
        } else {

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                           String tokenID= FirebaseInstanceId.getInstance().getToken();
                           String user_id=mAuth.getCurrentUser().getUid();
                             //   Log.v("tokenID"," "+tokenID+"  "+user_id);
                                Map<String,Object> map=new HashMap<>();
                                map.put("token_id",tokenID);
                            firebaseFirestore.collection("Users").document(user_id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();
                                }
                            });



                    }
                    else if(!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Incorrect email or password", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }


}
