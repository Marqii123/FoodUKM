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
import android.widget.Toast;

import com.example.fyp_proj.Merchant.testnavdrawer;
import com.example.fyp_proj.User.MainActivity_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    TextInputEditText regname, regemail, regpass;
    Button regbtn;
    RadioButton userRadio, merchRadio;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), testnavdrawer.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //progressbar
        progressBar = findViewById(R.id.progressbar);

        //inputText
        regname = findViewById(R.id.regNameText);
        regemail = findViewById(R.id.regMailText);
        regpass = findViewById(R.id.regPassText);

        //Radio button
        userRadio = findViewById(R.id.UserRadio);
        merchRadio = findViewById(R.id.MerchRadio);

        //button
        regbtn = findViewById(R.id.Registerbtn);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //register
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,email,password;

                progressBar.setVisibility(View.VISIBLE);

                name = String.valueOf(regname.getText());
                email = String.valueOf(regemail.getText());
                password = String.valueOf(regpass.getText());

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(Register.this, "Please enter your name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Please enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Please enter your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!userRadio.isChecked() && !merchRadio.isChecked()){
                    Toast.makeText(Register.this, "Please select your type of user!", Toast.LENGTH_SHORT).show();
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(Register.this, "Successfully registered.",
                                            Toast.LENGTH_SHORT).show();

                                    if(merchRadio.isChecked()){
                                        FirebaseUser user = task.getResult().getUser();
                                        String nama = regname.getText().toString();

                                        DatabaseReference dbrefUser = database.getReference().child("Merchant");
                                        dbrefUser.child(user.getUid()).child("uid").setValue(user.getUid());
                                        dbrefUser.child(user.getUid()).child("name").setValue(nama);
                                        dbrefUser.child(user.getUid()).child("email").setValue(user.getEmail());

                                        Intent intent = new Intent(getApplicationContext(), testnavdrawer.class);
                                        startActivity(intent);
                                        finish();
                                    }else if(userRadio.isChecked()){
                                        FirebaseUser user = task.getResult().getUser();
                                        String nama = regname.getText().toString();

                                        DatabaseReference dbrefUser = database.getReference().child("Customer");
                                        dbrefUser.child(user.getUid()).child("uid").setValue(user.getUid());
                                        dbrefUser.child(user.getUid()).child("name").setValue(nama);
                                        dbrefUser.child(user.getUid()).child("email").setValue(user.getEmail());

                                        Intent intent = new Intent(getApplicationContext(), MainActivity_user.class);
                                        startActivity(intent);
                                        finish();
                                    }


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}