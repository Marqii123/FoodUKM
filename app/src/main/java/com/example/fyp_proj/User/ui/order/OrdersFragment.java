package com.example.fyp_proj.User.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.Merchant.ui.Orders.CustOrdersAdapter;
import com.example.fyp_proj.Merchant.ui.Orders.CustOrdersFragment;
import com.example.fyp_proj.R;
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

import java.util.ArrayList;

public class OrdersFragment extends Fragment implements OrdersAdapter.OnItemClickListener{
    RecyclerView recyclerView;
    DatabaseReference databaseReference, dbref;
    FirebaseUser user;
    FirebaseAuth auth;
    ArrayList<CartData> list;
    OrdersAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.user_fragment_orders, container, false);

        recyclerView = root.findViewById(R.id.orders_recyc);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbref = FirebaseDatabase.getInstance().getReference("Customer");

        list = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        ClearData();
        getUserData();

        return root;
    }

    @Override
    public void onItemClick(int position) {
        CartData selectedData = list.get(position);
        Intent intent = new Intent(getActivity(), Status.class);
        intent.putExtra("status", selectedData);
        startActivity(intent);
    }

    @Override
    public void onCancelClick(int position) {

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

                String name = userData.getName();



                Query query = databaseReference.child("Payment").orderByChild("statusCust").equalTo(name+"Ordering");
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
                            cartData.setStatus(dataSnapshot.child("status").getValue().toString());
                            cartData.setStatusCust(dataSnapshot.child("statusCust").getValue().toString());
                            cartData.setMkey(dataSnapshot.getKey());

                            list.add(cartData);
                        }
                        adapter = new OrdersAdapter(getActivity(), list);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(OrdersFragment.this);

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