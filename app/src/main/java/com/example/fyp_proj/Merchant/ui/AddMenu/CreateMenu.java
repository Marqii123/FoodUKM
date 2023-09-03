package com.example.fyp_proj.Merchant.ui.AddMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_proj.UserData;
import com.example.fyp_proj.Merchant.testnavdrawer;
import com.example.fyp_proj.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreateMenu extends AppCompatActivity {
    TextView name;
    ImageView menupic;
    TextInputEditText food, price, desc;
    String Sfood, Sprice, Sdesc, tempimg;
    Button addbtn;
    DatabaseReference databaseReference, dbref;
    StorageReference storageReference;
    FirebaseUser user;
    FirebaseAuth auth;
    int Image_Request_Code = 1;
    Uri FilePathUri;
    MenuData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);

        //action bar
        getSupportActionBar().setTitle("Menu Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbref = FirebaseDatabase.getInstance().getReference("Merchant");
        storageReference = FirebaseStorage.getInstance().getReference("menu image");

        food = findViewById(R.id.inputFood);
        price = findViewById(R.id.inputPrice);
        desc = findViewById(R.id.inputDesc);
        menupic = findViewById(R.id.menuImg);
        addbtn = findViewById(R.id.addBtn);
        name = findViewById(R.id.textView5);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        test();

        menupic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload();
                //test();

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            Picasso.get().load(FilePathUri).into(menupic);
        }
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    public void test(){
        Query query =dbref.child(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String merchantName = snapshot.child("name").getValue().toString();

                UserData userData = new UserData();
                userData.setName(snapshot.child("name").getValue().toString());

                String merchName = userData.getName();
                //Toast.makeText(CreateMenu.this, merchName, Toast.LENGTH_LONG).show();
                name.setText(merchName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void Upload(){
        if(FilePathUri != null) {
            final StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            storageReference2.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String downloadurl = uri.toString();
                            Sfood = food.getText().toString().trim();
                            Sprice = price.getText().toString().trim();
                            Sdesc = desc.getText().toString().trim();
                            tempimg = downloadurl;

                            Query query =dbref.child(user.getUid());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserData userData = new UserData();
                                    userData.setName(snapshot.child("name").getValue().toString());

                                    String merchName = userData.getName();
                                    //Toast.makeText(CreateMenu.this, merchName, Toast.LENGTH_LONG).show();
                                    //name.setText(merchName);
                                    Toast.makeText(CreateMenu.this, "Success!", Toast.LENGTH_SHORT).show();
                                    MenuData menuData = new MenuData(merchName, Sfood, Sprice, Sdesc, tempimg);
                                    String imgid = databaseReference.push().getKey();
                                    databaseReference.child("Menu").child(imgid).setValue(menuData);

                                    Intent intent = new Intent(getApplicationContext(), testnavdrawer.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        }
                    });
                }
            });
        }else {
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_LONG).show();
        }
    }

}