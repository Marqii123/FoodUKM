package com.example.fyp_proj.Merchant.ui.Profile;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fyp_proj.R;
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
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    Button edit, save;
    TextInputEditText nameTxt, emailTxt;
    ImageView picProfile;
    TextView UidText;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String Name, Email, pic, uid;
    int Image_Request_Code = 1;
    Uri FilePathUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.merchant_fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        edit = root.findViewById(R.id.EditProfBtn);
        save = root.findViewById(R.id.saveBtn);
        nameTxt = root.findViewById(R.id.txtName);
        emailTxt = root.findViewById(R.id.txtEmail);
        picProfile = root.findViewById(R.id.profPic);
        UidText = root.findViewById(R.id.uidTxt);

        nameTxt.setFocusable(false);
        nameTxt.setClickable(false);
        emailTxt.setFocusable(false);
        emailTxt.setClickable(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Merchant");

        Query query = databaseReference.child(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = new UserData();
                userData.setName(snapshot.child("name").getValue().toString());
                userData.setEmail(snapshot.child("email").getValue().toString());
                userData.setUid(snapshot.child("uid").getValue().toString());
                if (snapshot.child("pic").getValue() != null){
                    userData.setPic(snapshot.child("pic").getValue().toString());
                }

                Name = userData.getName();
                Email = userData.getEmail();
                pic = userData.getPic();
                uid = userData.getUid();

                UidText.setText(uid);
                nameTxt.setText(Name);
                emailTxt.setText(Email);
                Picasso.get()
                        .load(pic)
                        .placeholder(R.drawable.applogo)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_error)
                        .into(picProfile);


                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                                Bundle bundle = new Bundle();
                                bundle.putString("name", Name);
                                bundle.putString("email", Email);
                                bundle.putString("pic", pic);
                                bundle.putString("uid", uid);
                                Intent intent = new Intent(getActivity(), EditProfile.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }
}
