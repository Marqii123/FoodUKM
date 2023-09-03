package com.example.fyp_proj.User.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_proj.CartData;
import com.example.fyp_proj.Merchant.ui.AddMenu.MenuData;
import com.example.fyp_proj.R;
import com.example.fyp_proj.User.ui.MenuDetails.MenuDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeUserAdapter extends RecyclerView.Adapter<HomeUserAdapter.ViewHolder>{
    android.content.Context context;
    ArrayList<MenuData> data;
    ArrayList<CartData> data2;

    public HomeUserAdapter(Context context, ArrayList<MenuData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public HomeUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview, parent, false);
        return new HomeUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeUserAdapter.ViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<MenuData> newData) {
        data = newData;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        //to go ordering page
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name, food, price;
        ImageView menupic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.orderName);
            food = itemView.findViewById(R.id.tv_textFood);
            price = itemView.findViewById(R.id.tv_textPrice);
            menupic = itemView.findViewById(R.id.payslipimg);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, MenuDetails.class);
            intent.putExtra("Datas", data.get(getAdapterPosition()));

            context.startActivity(intent);
        }
    }

}
