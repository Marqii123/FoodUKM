package com.example.fyp_proj.User.ui.logout;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fyp_proj.Login;
import com.example.fyp_proj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogoutFragment extends Fragment {
    TextView prof, uid;
    Button logoutBtn;
    FirebaseAuth auth;
    FirebaseUser user;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.user_fragment_logout, container, false);

        prof = root.findViewById(R.id.textUserEmail);
        uid = root.findViewById(R.id.textUserUID);


        logoutBtn = root.findViewById(R.id.logoutBtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        prof.setText(user.getEmail());
        uid.setText(user.getUid());


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return root;
    }
}