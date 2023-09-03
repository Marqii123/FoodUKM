package com.example.fyp_proj.Merchant.ui.Orders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.R;
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


public class CustOrdersFragment extends Fragment implements CustOrdersAdapter.OnItemClickListener{
    RecyclerView recyclerView;
    DatabaseReference databaseReference, dbref;
    FirebaseUser user;
    FirebaseAuth auth;
    ArrayList<CartData> list;
    CustOrdersAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.merchant_fragment_cust_orders, container, false);
        recyclerView = root.findViewById(R.id.custOrder_recy);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbref = FirebaseDatabase.getInstance().getReference("Merchant");

        list = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        ClearData();
        getUserData();
        return root;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onRemoveClick(int position) {

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

                String merchName = userData.getName();

                Query query = databaseReference.child("Payment").orderByChild("statusMerc").equalTo(merchName+"Ordering");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ClearData();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            CartData cartData = new CartData();

                            cartData.setId(dataSnapshot.child("id").getValue().toString());
                            cartData.setMerchname(dataSnapshot.child("merchname").getValue().toString());
                            cartData.setName(dataSnapshot.child("name").getValue().toString());
                            cartData.setPrice(dataSnapshot.child("price").getValue().toString());
                            cartData.setFood(dataSnapshot.child("food").getValue().toString());
                            cartData.setQt(dataSnapshot.child("qt").getValue().toString());
                            cartData.setRequest(dataSnapshot.child("request").getValue().toString());
                            cartData.setStatus(dataSnapshot.child("status").getValue().toString());
                            cartData.setMethod(dataSnapshot.child("method").getValue().toString());
                            cartData.setStatusCust(dataSnapshot.child("statusCust").getValue().toString());
                            cartData.setStatusMerc(dataSnapshot.child("statusMerc").getValue().toString());
                            cartData.setMkey(dataSnapshot.getKey());

                            list.add(cartData);
                        }
                        adapter = new CustOrdersAdapter(getActivity(), list);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(CustOrdersFragment.this);

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
}