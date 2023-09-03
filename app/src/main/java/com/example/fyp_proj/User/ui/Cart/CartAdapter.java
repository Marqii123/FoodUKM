package com.example.fyp_proj.User.ui.Cart;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.Merchant.ui.AddMenu.MenuData;
import com.example.fyp_proj.Merchant.ui.home.HomeMerchantAdapter;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.ui.Payment.Payment;
import com.example.fyp_proj.User.ui.home.HomeUserAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    android.content.Context context;
    ArrayList<CartData> data;
    CartAdapter.OnItemClickListener mlistener;

    public CartAdapter(Context context, ArrayList<CartData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_listview, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        /*holder.mName.setText(data.get(position).getMerchname());
        holder.cName.setText(data.get(position).getName());
        holder.price.setText("RM "+data.get(position).getPrice());*/

        final CartData cartData= data.get(position);
        String name = cartData.getName();
        holder.mName.setText(cartData.getFood());
        holder.cName.setText(cartData.getMerchname());
        holder.price.setText("RM "+cartData.getPrice());
        holder.btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, Payment.class);
                intent.putExtra("Payment", cartData);

                context.startActivity(intent);
            }
        });
        holder.btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String selectedkey = cartData.getMkey();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                databaseReference.child("Cart").child(name).child(selectedkey).removeValue();
                Toast.makeText(view.getContext(), "Removed Sucessfully!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        TextView mName, cName, price;
        Button btncheckout, btncancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);



            mName = itemView.findViewById(R.id.orderName);
            cName = itemView.findViewById(R.id.custName);
            price = itemView.findViewById(R.id.orderPrice);
            btncheckout = itemView.findViewById(R.id.chckout_btn);
            btncancel = itemView.findViewById(R.id.canc_btn);

            btncheckout.setVisibility(View.VISIBLE);
            btncancel.setVisibility(View.VISIBLE);

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
                            mlistener.onRemoveClick(position);
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
            MenuItem rem = contextMenu.add(Menu.NONE, 1, 1, "Remove");

            rem.setOnMenuItemClickListener(this);

        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);

        void onRemoveClick(int position);

    }
    public void setOnItemClickListener(CartAdapter.OnItemClickListener listener){
        mlistener = listener;
    }
}
