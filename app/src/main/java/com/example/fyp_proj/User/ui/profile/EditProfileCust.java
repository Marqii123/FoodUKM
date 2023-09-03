package com.example.fyp_proj.User.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fyp_proj.Merchant.testnavdrawer;
import com.example.fyp_proj.Merchant.ui.Profile.EditProfile;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.MainActivity_user;
import com.example.fyp_proj.UserData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileCust extends AppCompatActivity {
    Button edit, save;
    TextInputEditText nameTxt, emailTxt;
    ImageView picProfile;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference databaseReference, dbref;
    StorageReference storageReference;
    String Name, Email, Pic, Uid;
    String EditName, EditEmail, EditPic, UserID;
    int Image_Request_Code = 1;
    Uri FilePathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_edit_profile);

        storageReference = FirebaseStorage.getInstance().getReference("profile pic");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customer");
        dbref = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        edit = findViewById(R.id.EditProfBtn_cust);
        save = findViewById(R.id.saveBtn_cust);
        nameTxt = findViewById(R.id.txtName_cust);
        emailTxt = findViewById(R.id.txtEmail_cust);
        picProfile = findViewById(R.id.profPic_cust);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Name = bundle.getString("name");
            Email = bundle.getString("email");
            Pic = bundle.getString("pic");
            Uid = bundle.getString("uid");

            nameTxt.setText(Name);
            emailTxt.setText(Email);

            Picasso.get()
                    .load(Pic)
                    .placeholder(R.drawable.applogo)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .into(picProfile);

        }

        picProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FilePathUri == null) {
                    NotUpdateImage();
                }else {
                    UpdateImage();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            Picasso.get().load(FilePathUri).into(picProfile);
        }
    }
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    public void UpdateImage(){

        final StorageReference imgref = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
        imgref.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imgUrl = uri.toString();
                        EditName = nameTxt.getText().toString().trim();
                        EditEmail = emailTxt.getText().toString().trim();
                        UserID = Uid;
                        EditPic = imgUrl;

                        Toast.makeText(EditProfileCust.this, "Updated ＼(^o^)／ ", Toast.LENGTH_SHORT).show();
                        UserData updateProfile = new UserData(EditName, EditEmail, EditPic, UserID);
                        databaseReference.child(UserID).setValue(updateProfile);
                        /*dbref.child("Menu").orderByChild("merchname").equalTo(Name).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.get
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });*/

                        openHomeActivity();

                    }
                });
            }
        });

    }
    public void NotUpdateImage(){

        EditName = nameTxt.getText().toString().trim();
        EditEmail = emailTxt.getText().toString().trim();
        UserID = Uid;
        EditPic = Pic;

        Toast.makeText(EditProfileCust.this, "Updated ＼(^o^)／ ", Toast.LENGTH_SHORT).show();
        UserData updateProfile = new UserData(EditName, EditEmail, EditPic, UserID);
        databaseReference.child(UserID).setValue(updateProfile);
        openHomeActivity();

    }
    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity_user.class);
        startActivity(intent);
    }
}