package com.example.fyp_proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_proj.Merchant.MainMerchant;
import com.example.fyp_proj.Merchant.testnavdrawer;
import com.example.fyp_proj.Merchant.ui.home.HomeFragment;
import com.example.fyp_proj.User.MainActivity_user;
import com.example.fyp_proj.User.MainUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    TextView register;
    TextInputEditText logemail, logpass;
    RadioButton user, merchant;
    Button login;
    FirebaseAuth mAuth, auth;
    FirebaseUser Fuser;
    FirebaseDatabase database;
    DatabaseReference databaseReference, merchref, custref;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        //test
        auth = FirebaseAuth.getInstance();
        Fuser = auth.getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.
        merchref = FirebaseDatabase.getInstance().getReference("Merchant");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Query query = databaseReference.child("Merchant").orderByChild("email").equalTo(currentUser.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Toast.makeText(Login.this, "Successfully Login.",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), testnavdrawer.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(Login.this, "Invalid type of user.",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity_user.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            /*Intent intent = new Intent(getApplicationContext(), testnavdrawer.class);
            startActivity(intent);
            finish();*/
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.resgisterlink);

        progressBar = findViewById(R.id.progressBar);

        user = findViewById(R.id.userRadio);
        merchant = findViewById(R.id.merchRadio);
        login = findViewById(R.id.loginbtn);

        //inputText
        logemail = findViewById(R.id.logemailText);
        logpass = findViewById(R.id.logpassText);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email,password;

                progressBar.setVisibility(View.VISIBLE);
                email = String.valueOf(logemail.getText());
                password = String.valueOf(logpass.getText());


                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Please enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Please enter your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!user.isChecked() && !merchant.isChecked()){
                    Toast.makeText(Login.this, "Please select your type of user!", Toast.LENGTH_SHORT).show();
                }


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                               // progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();


                                    //check if account is valid
                                    if(merchant.isChecked()){

                                        //method 2
                                        Query query = databaseReference.child("Merchant").orderByChild("email").equalTo(email);
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    Toast.makeText(Login.this, "Successfully Login.",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), testnavdrawer.class);
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    Toast.makeText(Login.this, "Invalid type of user.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }else {

                                        Query query = databaseReference.child("Customer").orderByChild("email").equalTo(email);
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    Toast.makeText(Login.this, "Successfully Login.",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity_user.class);
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    Toast.makeText(Login.this, "Invalid type of user.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }



                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }
}