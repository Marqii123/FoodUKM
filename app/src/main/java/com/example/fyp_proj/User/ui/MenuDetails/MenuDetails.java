package com.example.fyp_proj.User.ui.MenuDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.User.MainActivity_user;
import com.example.fyp_proj.UserData;
import com.example.fyp_proj.Merchant.testnavdrawer;
import com.example.fyp_proj.Merchant.ui.AddMenu.MenuData;
import com.example.fyp_proj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

public class MenuDetails extends AppCompatActivity {
    Button addBtn, minusBtn, cartBtn;
    TextView qt, Food, Price;
    String merchName, foodName, foodPrice, img;
    String MerchName, Name, FoodName, FoodPrice, Request, Quantity, Status;
    ImageView foodImg;
    EditText request;
    DatabaseReference dbref, databaseReference;
    FirebaseUser user;
    FirebaseAuth auth;
    int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_details);

        getSupportActionBar().setTitle("Menu Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbref = FirebaseDatabase.getInstance().getReference("Customer");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        addBtn = findViewById(R.id.btn_add);
        minusBtn = findViewById(R.id.btn_minus);
        cartBtn = findViewById(R.id.btn_addCart);

        Intent intent = getIntent();
        MenuData menuData = intent.getParcelableExtra("Datas");

        foodName = menuData.getMenuname();
        foodPrice = menuData.getPrice();
        merchName = menuData.getMerchname();
        img = menuData.getImg();

        Food = findViewById(R.id.tv_food);
        Price = findViewById(R.id.tv_foodPrice);
        request = findViewById(R.id.et_Request);
        qt = findViewById(R.id.text_num);
        foodImg = findViewById(R.id.imageView5);

        Food.setText(foodName);
        Price.setText("RM "+foodPrice);
        Picasso.get()
                .load(img)
                .placeholder(R.drawable.applogo)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_error)
                .into(foodImg);


        quantity = 1;
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                qt.setText(String.valueOf(quantity));
            }
        });
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity--;
                qt.setText(String.valueOf(quantity));
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToCart();
            }
        });

    }
    public void AddToCart(){
        //to add to cart
        MerchName = merchName;
        FoodName = foodName;
        FoodPrice = foodPrice;
        Request = request.getText().toString().trim();
        Quantity = qt.getText().toString();
        Status = "Ordering";

        int Total = Integer.valueOf(Quantity)*Integer.valueOf(FoodPrice);

        Query query =dbref.child(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = new UserData();
                userData.setName(snapshot.child("name").getValue().toString());

                String orderID = databaseReference.push().getKey();
                String custName = userData.getName();
                Toast.makeText(MenuDetails.this, "Success!", Toast.LENGTH_SHORT).show();
                CartData cartData = new CartData(MerchName, custName, FoodName, String.valueOf(Total), Request, Quantity, Status,
                        "not paid", custName+Status, MerchName+Status, orderID);
                databaseReference.child("Cart").child(custName).child(orderID).setValue(cartData);

                Intent intent = new Intent(getApplicationContext(), MainActivity_user.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}