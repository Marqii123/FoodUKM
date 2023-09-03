package com.example.fyp_proj.Merchant.ui.Orders;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.ui.Cart.CartAdapter;
import com.example.fyp_proj.User.ui.Status.Status;

import java.util.ArrayList;

public class CustOrdersAdapter extends RecyclerView.Adapter<CustOrdersAdapter.ViewHolder>{
    android.content.Context context;
    ArrayList<CartData> data;
    CustOrdersAdapter.OnItemClickListener mlistener;

    public CustOrdersAdapter(Context context, ArrayList<CartData> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public CustOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_listview, parent, false);
        return new CustOrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustOrdersAdapter.ViewHolder holder, int position) {
        holder.mName.setText(data.get(position).getFood());
        holder.cName.setText(data.get(position).getName());
        holder.price.setText("RM "+data.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        TextView mName, cName, price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.orderName);
            cName = itemView.findViewById(R.id.custName);
            price = itemView.findViewById(R.id.orderPrice);


            itemView.setOnClickListener(this);
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
            Context context = view.getContext();
            Intent intent = new Intent(context, OrdersDetails.class);
            intent.putExtra("details", data.get(getAdapterPosition()));
            context.startActivity(intent);
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
    public void setOnItemClickListener(CustOrdersAdapter.OnItemClickListener listener){
        mlistener = listener;
    }
}
