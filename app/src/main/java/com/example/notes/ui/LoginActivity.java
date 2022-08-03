package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    ImageView imagegoogle;

    // TO GOOGLE
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    Button siginin_signup,btn_loginin;
    View view;
    AlertDialog alertDialog;
    EditText email,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pass=findViewById(R.id.pass);;
        email=findViewById(R.id.email);;
        btn_loginin=findViewById(R.id.btn_loginin);;
        siginin_signup=findViewById(R.id.siginin_signup);
        imagegoogle=findViewById(R.id.imagegoogle);
        /// TO SIGN IN WITH GOOGLE
        mAuth = FirebaseAuth.getInstance();

        createRequest();
        imagegoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInwithgoogle();
            }
        });

        siginin_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertBuilder= new AlertDialog.Builder(LoginActivity.this);
                view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.sing_up,null,false);
                Button btn_signup_signup=view.findViewById(R.id.btn_signup_signup);
                EditText email_signup=view.findViewById(R.id.email_signup);
                EditText pass_signup=view.findViewById(R.id.pass_signup);
                alertBuilder.setView(view);
                alertDialog = alertBuilder.create();
                alertDialog.show();

                btn_signup_signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(email_signup.getText().toString().equals("")||pass_signup.getText().toString().equals("")){
                            Toast.makeText(LoginActivity.this,"complete the information",Toast.LENGTH_LONG).show();
                        }else{

                            mAuth.createUserWithEmailAndPassword(email_signup.getText().toString(),pass_signup.getText().toString()).
                                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
/*
                                                mAuth.getCurrentUser().sendEmailVerification().
                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(LoginActivity.this," the success send verity",Toast.LENGTH_LONG).show();
                                                                    alertDialog.dismiss();
                                                                }
                                                                else{
                                                                    Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();

                                                                }
                                                            }
                                                        });

 */
                                                        Toast.makeText(LoginActivity.this," the success send verity",Toast.LENGTH_LONG).show();
                                                                    alertDialog.dismiss();

                                            }else{
                                                Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }

                    }
                });
            }
        });


        btn_loginin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass.getText().toString().equals("")||email.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this,"complete the information",Toast.LENGTH_LONG).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //verity of email
                               //         if(mAuth.getCurrentUser().isEmailVerified()){
                                            Toast.makeText(LoginActivity.this," welcome "+email.getText().toString(),Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                 //       }
                              //          else{
                                //            Toast.makeText(LoginActivity.this," please verity "+email.getText().toString(),Toast.LENGTH_LONG).show();
                                  //      }
                                    }else{
                                        Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        //if user is sign in with email auto to mainactivity dont need sign in new
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }


    // to create request with api google
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // to show list with emails of google on phone
    private void signInwithgoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //to get the email google and to send firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                Toast.makeText(LoginActivity.this, "signin success.", Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this, "signin failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //SUCCESS AND MOVE TO MAINACTIVITY
                            Toast.makeText(LoginActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        } else {
                            //failed
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}