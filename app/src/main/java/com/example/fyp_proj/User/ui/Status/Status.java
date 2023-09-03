package com.example.fyp_proj.User.ui.Status;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.MainActivity_user;
import com.example.fyp_proj.User.ui.Cart.Cart;
import com.example.fyp_proj.User.ui.Cart.CartAdapter;
import com.example.fyp_proj.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Status extends AppCompatActivity {
    DatabaseReference databaseReference, dbref;
    FirebaseUser user;
    FirebaseAuth auth;
    String custName, Fstatus, statuscust, oID, ordernum;
    TextView textstatus, textOrderNum;
    Button receivedBtn;
    ImageView imgStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbref = FirebaseDatabase.getInstance().getReference("Payment");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textstatus = findViewById(R.id.text_status);
        textOrderNum = findViewById(R.id.text_orderNum);
        receivedBtn = findViewById(R.id.btn_receive);
        imgStatus = findViewById(R.id.statusImg);

        Intent intent = getIntent();
        CartData cartData = intent.getParcelableExtra("status");

        custName = cartData.getName();
        Fstatus = cartData.getStatus();
        statuscust = cartData.getStatusCust();
        oID = cartData.getMkey();

        textOrderNum.setText(oID);
        if (Fstatus.equals("Ordering")) {
            textstatus.setText("Your meal is being prepared!");
            Picasso.get()
                    .load(R.drawable.cooking)
                    .placeholder(R.drawable.applogo)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .into(imgStatus);

        } else if (Fstatus.equals("Prepared")) {
            textstatus.setText("Your meal is done! You can pick it up now!");
            receivedBtn.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(R.drawable.finish_cooking)
                    .placeholder(R.drawable.applogo)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .into(imgStatus);
        } else if (Fstatus.equals("Received")) {
            textstatus.setText("Already Picked Up");
            receivedBtn.setVisibility(View.INVISIBLE);
            Picasso.get()
                    .load(R.drawable.finish_cooking)
                    .placeholder(R.drawable.applogo)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .into(imgStatus);
        }

        receivedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbref.child(oID).child("status").setValue("Received");
                dbref.child(oID).child("statusCust").setValue(custName+"Received");
                Toast.makeText(Status.this, "Order Received", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Status.this, MainActivity_user.class));
            }
        });
    }
}