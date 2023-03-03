package com.yeslabapps.friendb.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ProductItemBinding;
import com.yeslabapps.friendb.interfaces.OnClick;
import com.yeslabapps.friendb.model.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyHolder> {

    private ArrayList<Product> productArrayList;
    private Context context;
    private final OnClick itemClickListener;

    public ProductAdapter(ArrayList<Product>productArrayList,Context context,OnClick itemClickListener){
        this.productArrayList= productArrayList;
        this.context=context;
        this.itemClickListener = itemClickListener;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        ProductItemBinding recyclerRowBinding;

        public MyHolder(@NonNull ProductItemBinding recyclerRowBinding, OnClick itemClickListener) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;

        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductItemBinding recyclerRowBinding = ProductItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding,itemClickListener);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        Product product= productArrayList.get(position);

        holder.recyclerRowBinding.productTitle.setText(product.getTitle());
        holder.recyclerRowBinding.productPrice.setText(new StringBuilder().append("").append(product.getGold()).toString());

        Picasso.get().load(product.getImage()).into(holder.recyclerRowBinding.productImage);

        holder.itemView.setOnClickListener(view -> {
            if (itemClickListener!=null){
                int pos = holder.getAdapterPosition();
                if (pos!=RecyclerView.NO_POSITION){
                    itemClickListener.onClick(pos);
                }
            }
        });


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, ProductDetailsActivity.class);
//                intent.putExtra("productId",product.getProductId());
//                context.startActivity(intent);
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return null!=productArrayList?productArrayList.size():0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}