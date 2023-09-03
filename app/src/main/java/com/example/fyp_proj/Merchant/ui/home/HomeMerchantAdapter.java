package com.example.fyp_proj.Merchant.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.Merchant.ui.AddMenu.EditMenuActivity;
import com.example.fyp_proj.Merchant.ui.AddMenu.MenuData;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.ui.Payment.Payment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeMerchantAdapter extends RecyclerView.Adapter<HomeMerchantAdapter.ViewHolder> {
    android.content.Context context;
    ArrayList<MenuData> data;
    HomeMerchantAdapter.OnItemClickListener mlistener;

    public HomeMerchantAdapter(Context context, ArrayList<MenuData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public HomeMerchantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_merch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMerchantAdapter.ViewHolder holder, int position) {
        final MenuData menuData= data.get(position);
        holder.name.setText(data.get(position).getMerchname());
        holder.food.setText(data.get(position).getMenuname());
        holder.price.setText("RM "+data.get(position).getPrice());
        Picasso.get()
                .load(data.get(position).getImg())
                .placeholder(R.drawable.applogo)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_error)
                .into(holder.menupic);

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, EditMenuActivity.class);
                intent.putExtra("Menu", menuData);

                context.startActivity(intent);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String selectedkey = menuData.getmKey();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                databaseReference.child("Menu").child(selectedkey).removeValue();
                Toast.makeText(view.getContext(), "Menu Removed Sucessfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        TextView name, food, price;
        Button editBtn, deleteBtn;
        ImageView menupic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.orderName);
            food = itemView.findViewById(R.id.tv_textFood);
            price = itemView.findViewById(R.id.tv_textPrice);
            menupic = itemView.findViewById(R.id.payslipimg);
            editBtn = itemView.findViewById(R.id.edit_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mlistener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (menuItem.getItemId()){
                        case 1:
                            mlistener.onEditClick(position);
                            return true;
                        case 2:
                            mlistener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");
            MenuItem edit = contextMenu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");

            edit.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);

        void onEditClick(int position);

        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(HomeMerchantAdapter.OnItemClickListener listener){
        mlistener = listener;
    }
}
