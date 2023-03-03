package com.yeslabapps.friendb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ReferralItemBinding;
import com.yeslabapps.friendb.interfaces.OnClick;
import com.yeslabapps.friendb.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder> {

    private ArrayList<User> userArrayList;
    private Context context;
    private final OnClick onClick;

    public UserAdapter(ArrayList<User>userArrayList, Context context, OnClick onClick){
        this.userArrayList= userArrayList;
        this.context=context;
        this.onClick = onClick;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        ReferralItemBinding recyclerRowBinding;

        public MyHolder(@NonNull ReferralItemBinding recyclerRowBinding,OnClick onClick) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReferralItemBinding recyclerRowBinding = ReferralItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding,onClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        User user = userArrayList.get(position);

        holder.recyclerRowBinding.username.setText(user.getUsername());


        holder.recyclerRowBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
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
        return null!=userArrayList?userArrayList.size():0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
