package com.example.fyp_proj.User.ui.Payment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fyp_proj.CartData;
import com.example.fyp_proj.Merchant.ui.AddMenu.MenuData;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.MainActivity_user;
import com.example.fyp_proj.User.ui.Status.Status;
import com.example.fyp_proj.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Payment extends AppCompatActivity {
    DatabaseReference databaseReference, dbref;
    FirebaseUser user;
    FirebaseAuth auth;
    ArrayList<CartData> list;
    String custName, selectedkey, Price;
    TextView totalTV;
    Button paybtn;
    RadioButton onlinebtn, cashbtn;

    String secret_key = "sk_test_51NX52OH6vaFS3owUWBZBkLPBVCP4CWtRrrO8r5HXcw1PVwTiJ6n6IrQ8IyF4yxtgA2DawfizOOrg7dtY2iKLe4Xr008TRig01K";
    String publish_key = "pk_test_51NX52OH6vaFS3owUHIcZjPSSPjuIpf7LFVtuILryaqBfXTeq3iCcFv3inwqfHt3Uzy9Zq3rR10IMPyZldasyyhcy00R8nD49cV";
    PaymentSheet paymentSheet;
    String customerID;
    String EphericalKey;
    String ClientSecretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbref = FirebaseDatabase.getInstance().getReference("Customer");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        totalTV = findViewById(R.id.tv_total);
        paybtn = findViewById(R.id.pay_Btn);

        onlinebtn = findViewById(R.id.bankBtn);
        cashbtn = findViewById(R.id.cashBtn);

        Intent intent = getIntent();
        CartData datas = intent.getParcelableExtra("Payment");
        selectedkey = datas.getMkey();

        Price = datas.getPrice();
        totalTV.setText("RM "+Price);

        //Stripe

        PaymentConfiguration.init(Payment.this, publish_key);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        //payment button
        paybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query =dbref.child(user.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserData userData = new UserData();
                        userData.setName(snapshot.child("name").getValue().toString());

                        custName = userData.getName();
                        if(onlinebtn.isChecked()){
                            PaymentFlow();
                        }
                        moverecord(databaseReference.child("Cart").child(custName).child(selectedkey),
                                databaseReference.child("Payment").child(selectedkey));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(Payment.this, MainActivity_user.class);
                startActivity(intent);

                //Stripe
                Log.d("PaymentDebug", "CustId: " + customerID);
                Log.d("PaymentDebug", "EphericalKey: " + EphericalKey);
                //PaymentFlow();
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            Toast.makeText(Payment.this, "CustomerID = "+customerID, Toast.LENGTH_LONG).show();

                            getEphericalKey(customerID);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+secret_key);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Payment.this);
        requestQueue.add(stringRequest);



    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(Payment.this, "Successful", Toast.LENGTH_LONG).show();
        }
    }

    private void getEphericalKey(String customerID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");

                            Toast.makeText(Payment.this, "EphericalKey = "+EphericalKey, Toast.LENGTH_LONG).show();

                            getClientSecret(customerID, EphericalKey);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+secret_key);
                headers.put("Stripe-Version", "2022-11-15");
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Payment.this);
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephericalKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecretKey = object.getString("client_secret");

                            Toast.makeText(Payment.this, "ClientSecret = "+ClientSecretKey, Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+secret_key);
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", Price+"00");
                params.put("currency", "myr");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Payment.this);
        requestQueue.add(stringRequest);
    }
    private void PaymentFlow() {
        paymentSheet.presentWithPaymentIntent(
                ClientSecretKey, new PaymentSheet.Configuration("ABC"
                        , new PaymentSheet.CustomerConfiguration(
                        customerID,
                        EphericalKey
                ))
        );
    }

    public void moverecord(DatabaseReference frompath, final DatabaseReference topath){
        frompath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topath.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error != null){
                            Toast.makeText(Payment.this,"Error Occurred", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(Payment.this,"Success!", Toast.LENGTH_LONG).show();

                            if(onlinebtn.isChecked()){
                                topath.child("method").setValue("Online Banking");
                                //PaymentFlow();
                            }
                            if(cashbtn.isChecked()){
                                topath.child("method").setValue("Cash Payment");
                            }
                            frompath.removeValue();

                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Payment.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

}