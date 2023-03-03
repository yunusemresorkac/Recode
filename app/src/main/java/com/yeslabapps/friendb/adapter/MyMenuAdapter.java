package com.yeslabapps.friendb.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yeslabapps.friendb.interfaces.OnClick;
import com.yeslabapps.friendb.databinding.MenuItemBinding;
import com.yeslabapps.friendb.model.MyMenu;

import java.util.ArrayList;

public class MyMenuAdapter extends RecyclerView.Adapter<MyMenuAdapter.MyHolder> {

    private ArrayList<MyMenu> menuArrayList;
    private Context context;
    private final OnClick onClick;

    public MyMenuAdapter(ArrayList<MyMenu>menuArrayList,Context context,OnClick onClick){
        this.menuArrayList= menuArrayList;
        this.context=context;
        this.onClick = onClick;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        MenuItemBinding recyclerRowBinding;

        public MyHolder(@NonNull MenuItemBinding recyclerRowBinding,OnClick onClick) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MenuItemBinding recyclerRowBinding = MenuItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding,onClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        MyMenu menu = menuArrayList.get(position);

        holder.recyclerRowBinding.menuTitle.setText(menu.getTitle());
        holder.recyclerRowBinding.menuImage.setImageResource(menu.getIcon());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClick!=null){
                    int pos = holder.getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION){
                        onClick.onClick(pos);
                    }
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return null!=menuArrayList?menuArrayList.size():0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}