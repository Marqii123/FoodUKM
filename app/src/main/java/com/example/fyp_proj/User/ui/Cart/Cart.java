package com.example.fyp_proj.User.ui.Cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.Merchant.ui.AddMenu.EditMenuActivity;
import com.example.fyp_proj.Merchant.ui.AddMenu.MenuData;
import com.example.fyp_proj.Merchant.ui.home.HomeFragment;
import com.example.fyp_proj.Merchant.ui.home.HomeMerchantAdapter;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.ui.Payment.Payment;
import com.example.fyp_proj.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Cart extends AppCompatActivity implements CartAdapter.OnItemClickListener{
    RecyclerView recyclerView;
    DatabaseReference databaseReference, dbref;
    FirebaseUser user;
    FirebaseAuth auth;
    ArrayList<CartData> list;
    CartAdapter adapter;
    Button btncheckout, btncheckout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.cart_recyc);

        LinearLayoutManager layoutManager = new LinearLayoutManager(Cart.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbref = FirebaseDatabase.getInstance().getReference("Customer");

        list = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        ClearData();
        getUserData();


        btncheckout = findViewById(R.id.btn_checkout);
        //btncheckout2 = findViewById(R.id.chckout_btn);

        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cart.this, Payment.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onItemClick(int position) {
        /*btncheckout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cart.this, Payment.class);
                intent.putExtra("Payment",list.get(position));

                startActivity(intent);
            }
        });*/
        Intent intent = new Intent(Cart.this, Payment.class);
        intent.putExtra("Payment",list.get(position));

        startActivity(intent);
    }

    @Override
    public void onRemoveClick(int position) {

    }
    private void Getdata() {
        Query query = databaseReference.child("Cart");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClearData();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    CartData cartData = new CartData();

                    cartData.setFood(dataSnapshot.child("food").getValue().toString());
                    cartData.setMerchname(dataSnapshot.child("merchname").getValue().toString());
                    cartData.setName(dataSnapshot.child("name").getValue().toString());
                    cartData.setPrice(dataSnapshot.child("price").getValue().toString());
                    cartData.setId(dataSnapshot.child("id").getValue().toString());
                    cartData.setMkey(dataSnapshot.getKey());

                    list.add(cartData);
                }
                adapter = new CartAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(Cart.this);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void ClearData(){
        if(list != null){
            list.clear();

            if(adapter != null){
                adapter.notifyDataSetChanged();
            }
        }
        list = new ArrayList<>();
    }
    public void getUserData(){
        Query query =dbref.child(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = new UserData();
                userData.setName(snapshot.child("name").getValue().toString());

                String custName = userData.getName();

                Query query = databaseReference.child("Cart").child(custName);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ClearData();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            CartData cartData = new CartData();

                            cartData.setId(dataSnapshot.child("id").getValue().toString());
                            cartData.setFood(dataSnapshot.child("food").getValue().toString());
                            cartData.setMerchname(dataSnapshot.child("merchname").getValue().toString());
                            cartData.setName(dataSnapshot.child("name").getValue().toString());
                            cartData.setPrice(dataSnapshot.child("price").getValue().toString());
                            cartData.setMkey(dataSnapshot.getKey());

                            list.add(cartData);
                        }
                        adapter = new CartAdapter(getApplicationContext(), list);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(Cart.this);

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void moverecord(DatabaseReference frompath, final DatabaseReference topath){
        frompath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topath.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error != null){
                            Toast.makeText(Cart.this,"Error occurred", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(Cart.this,"Success!", Toast.LENGTH_LONG).show();
                            frompath.removeValue();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Cart.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}