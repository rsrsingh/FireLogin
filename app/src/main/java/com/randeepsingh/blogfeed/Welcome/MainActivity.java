package com.randeepsingh.blogfeed.Welcome;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.randeepsingh.blogfeed.ForgotPassword;
import com.randeepsingh.blogfeed.Home.AccountMain;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.Register.AccountReg;
import com.randeepsingh.blogfeed.Register.Register;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private TextInputLayout mEmail, mPass;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private TextView txtReg;
    private ImageView mGmail;
    private TextView forgotPass;
    private SignInButton googlebtn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private ImageView logoImg;
    private static final String TAG = "MainActivity";

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("checkey", "main onCreate: ");
        txtReg = findViewById(R.id.main_reg);
        mEmail = findViewById(R.id.main_emaillayout);
        mPass = findViewById(R.id.main_passlayout);
        btnLogin = (Button) findViewById(R.id.loginbtn);
        forgotPass = findViewById(R.id.main_forgotPassTV);
        googlebtn = findViewById(R.id.main_googlebtn);
        logoImg=findViewById(R.id.main_logo);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In...");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Log.v("GoogleSignIn", "btn Clicked");
                signIn();
            }
        });


        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharedIntent = new Intent(MainActivity.this, ForgotPassword.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(mEmail, "email_transition");
                pairs[1] = new Pair<View, String>(btnLogin, "btnLogin_transition");
                pairs[2] = new Pair<View, String>(logoImg, "logo_transition");
                ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(sharedIntent,options.toBundle());
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseFirestore = FirebaseFirestore.getInstance();
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.e("checkey", "Main onAuthStateChanged: ");
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
                
                Intent sharedIntent = new Intent(MainActivity.this, Register.class);
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(mEmail, "email_transition");
                pairs[1] = new Pair<View, String>(mPass, "password_transition");
                pairs[2] = new Pair<View, String>(btnLogin, "btnLogin_transition");
                pairs[3] = new Pair<View, String>(logoImg, "logo_transition");
                ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(sharedIntent,options.toBundle());
            }
        });


    }

    private void startSignIn() {

        String email = mEmail.getEditText().getText().toString();
        String pass = mPass.getEditText().getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {

            Toast.makeText(this, "Please enter all the info", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String tokenID = FirebaseInstanceId.getInstance().getToken();
                        String user_id = mAuth.getCurrentUser().getUid();
                        //   Log.v("tokenID"," "+tokenID+"  "+user_id);
                        Map<String, Object> map = new HashMap<>();
                        map.put("token_id", tokenID);
                        firebaseFirestore.collection("Users").document(user_id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });


                    } else if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Incorrect email or password", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }
            });
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.v("GoogleSignIn", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.v("GoogleSignIn", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.v("GoogleSignIn", "signInWithCredential:success");
                            firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (!task.getResult().exists()) {

                                        startActivity(new Intent(MainActivity.this, AccountReg.class));

                                    } else {

                                        startActivity(new Intent(MainActivity.this, AccountMain.class));

                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GoogleSignIn", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });

    }


}
