package com.example.fyp_proj.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_proj.Login;
import com.example.fyp_proj.Merchant.testnavdrawer;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.ui.Cart.Cart;
import com.example.fyp_proj.UserData;
import com.example.fyp_proj.databinding.ActivityMainUser2Binding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity_user extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainUser2Binding binding;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainUser2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainActivityUser.toolbar);

        //auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user == null){

            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }else {


            Toast.makeText(getApplicationContext(), "User "+user.getEmail(), Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_Orders, R.id.nav_past, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_user);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.nav_logout) {
                    LogOut();
                    return true;
                }
                else {
                    boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                    if (handled)
                        drawer.closeDrawer(navigationView);
                    return handled;
                }
            }
        });

        //nav header
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.usernameTxt);
        TextView email = headerView.findViewById(R.id.useremailTxt);
        ImageView proPic = headerView.findViewById(R.id.userPic);

        String Email = user.getEmail();

        email.setText(Email);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customer");

        Query query =databaseReference.child(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = new UserData();
                userData.setName(snapshot.child("name").getValue().toString());
                if (snapshot.child("pic").getValue() != null){
                    userData.setPic(snapshot.child("pic").getValue().toString());
                }

                String Name = userData.getName();
                String pic = userData.getPic();

                username.setText(Name);
                Picasso.get()
                        .load(pic)
                        .placeholder(R.drawable.ic_person)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_error)
                        .into(proPic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_user, menu);



        MenuItem cartItem = menu.findItem(R.id.action_cart);
        cartItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), Cart.class);
                startActivity(intent);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_user);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void LogOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_user.this);
        builder.setMessage("Logging Out?");
        builder.setCancelable(true);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity_user.this, Login.class);
                startActivity(intent);
                MainActivity_user.this.finish();
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}