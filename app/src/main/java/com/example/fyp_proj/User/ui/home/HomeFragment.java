package com.example.fyp_proj.User.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_proj.Merchant.ui.AddMenu.MenuData;
import com.example.fyp_proj.Merchant.ui.home.HomeMerchantAdapter;
import com.example.fyp_proj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements HomeUserAdapter.OnItemClickListener{
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser user;
    FirebaseAuth auth;
    ArrayList<MenuData> list;
    HomeUserAdapter adapter;
    android.widget.SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.user_fragment_home, container, false);

        recyclerView = root.findViewById(R.id.userHomeRec);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        list = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //searching
        searchView = root.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchList(s);
                return true;
            }
        });


        ClearData();
        Getdata();

        return root;
    }
    private void Getdata() {
        Query query = databaseReference.child("Menu");
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
                    menuData.setmKey(dataSnapshot.getKey());

                    list.add(menuData);
                }
                adapter = new HomeUserAdapter(getActivity(), list);
                recyclerView.setAdapter(adapter);



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
    private void performSearch(String searchText){
        Query query = databaseReference.orderByChild("Menu").equalTo(searchText);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<MenuData> searchResults = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String result = snapshot.getValue().toString();
                    //searchResults.add(result);
                }

                // Update the adapter's data and notify the RecyclerView
                adapter.setData(searchResults);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }
    private void searchList(String text){
        ArrayList<MenuData> searchList = new ArrayList<>();
        for (MenuData menuData: list){
            if(menuData.getMenuname().toLowerCase().contains(text.toLowerCase())){
                searchList.add(menuData);
            }
        }
        adapter.setData(searchList);
    }
}