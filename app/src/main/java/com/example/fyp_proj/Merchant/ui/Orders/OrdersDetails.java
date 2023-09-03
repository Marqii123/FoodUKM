package com.example.fyp_proj.Merchant.ui.Orders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.Merchant.testnavdrawer;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.ui.Payment.Payment;
import com.example.fyp_proj.UserData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OrdersDetails extends AppCompatActivity {
    DatabaseReference databaseReference, dbref;
    FirebaseUser user;
    FirebaseAuth auth;
    String orderNum, merchname, custname, food, quantity, request, total, payment, key;
    TextInputEditText textFood, textCustName, textQuantity, textTotal, textpayment, textreq, textOid;
    Button finishOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_orders_details);

        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        CartData cartData = intent.getParcelableExtra("details");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbref = FirebaseDatabase.getInstance().getReference("Payment");

        orderNum = cartData.getId();
        merchname = cartData.getMerchname();
        custname = cartData.getName();
        food = cartData.getFood();
        quantity = cartData.getQt();
        request = cartData.getRequest();
        total = cartData.getPrice();
        payment = cartData.getMethod();
        key = cartData.getMkey();

        textOid = findViewById(R.id.Txt_OrderNum);
        textFood = findViewById(R.id.txt_food);
        textCustName = findViewById(R.id.txt_custName);
        textQuantity = findViewById(R.id.txt_qt);
        textTotal = findViewById(R.id.txt_total);
        textpayment = findViewById(R.id.txt_payment);
        textreq = findViewById(R.id.txt_req);

        finishOrder = findViewById(R.id.btn_finishOrder);

        textOid.setText(orderNum);
        textFood.setText(food);
        textCustName.setText(custname);
        textQuantity.setText(quantity);
        textTotal.setText(total);
        textpayment.setText(payment);
        textreq.setText(request);

        finishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbref.child(key).child("status").setValue("Prepared");
                dbref.child(key).child("statusCust").setValue(custname+"Ordering");
                dbref.child(key).child("statusMerc").setValue(merchname+"Prepared");
                Intent intent1 = new Intent(OrdersDetails.this, testnavdrawer.class);
                startActivity(intent1);
                //moverecord(databaseReference.child("Payment").child(merchname), databaseReference.child("Finished"));
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
                            Toast.makeText(OrdersDetails.this,"Error Occurred", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(OrdersDetails.this,"Success!", Toast.LENGTH_LONG).show();
                            frompath.removeValue();

                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrdersDetails.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}