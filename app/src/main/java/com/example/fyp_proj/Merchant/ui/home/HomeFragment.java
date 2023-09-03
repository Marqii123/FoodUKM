package com.example.fyp_proj.Merchant.ui.home;

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

import com.example.fyp_proj.Merchant.ui.AddMenu.EditMenuActivity;
import com.example.fyp_proj.Merchant.ui.AddMenu.MenuData;
import com.example.fyp_proj.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements HomeMerchantAdapter.OnItemClickListener{

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser user;
    FirebaseAuth auth;
    ArrayList<MenuData> list;
    HomeMerchantAdapter adapter;
    FirebaseStorage storage;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.merchant_fragment_home, container, false);

        recyclerView = root.findViewById(R.id.merchantHome_recyc);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        list = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        ClearData();
        Query query = databaseReference.child("Merchant").child(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                Getdata(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Getdata();

        return root;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onEditClick(int position) {
        Intent intent = new Intent(getActivity(), EditMenuActivity.class);
        intent.putExtra("Menu",list.get(position));

        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        MenuData selectedItem = list.get(position);
        final String selectedkey = selectedItem.getmKey();

        StorageReference storageref = storage.getReferenceFromUrl(selectedItem.getImg());
        storageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child("Menu").child(selectedkey).removeValue();
                Toast.makeText(getActivity(), "Menu Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Getdata(String merchName) {
        Query query = databaseReference.child("Menu").orderByChild("merchname").equalTo(merchName);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClearData();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    MenuData menuData = new MenuData();

                    menuData.setMerchname(dataSnapshot.child("merchname").getValue().toString());
                    menuData.setMenuname(dataSnapshot.child("menuname").getValue().toString());
                    menuData.setPrice(dataSnapshot.child("price").getValue().toString());
                    menuData.setImg(dataSnapshot.child("img").getValue().toString());
                    menuData.setDesc(dataSnapshot.child("desc").getValue().toString());
                    menuData.setmKey(dataSnapshot.getKey());

                    list.add(menuData);
                }
                adapter = new HomeMerchantAdapter(getActivity(), list);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(HomeFragment.this);

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
}