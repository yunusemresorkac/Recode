package com.yeslabapps.friendb.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.PaymentItemBinding;
import com.yeslabapps.friendb.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyHolder> {

    private ArrayList<Order> orderArrayList;
    private Context context;

    public PaymentAdapter(ArrayList<Order> orderArrayList, Context context){
        this.orderArrayList = orderArrayList;
        this.context=context;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        PaymentItemBinding recyclerRowBinding;

        public MyHolder(@NonNull PaymentItemBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PaymentItemBinding recyclerRowBinding = PaymentItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        Order order = orderArrayList.get(position);

        holder.recyclerRowBinding.time.setText(convertTime(order.getOrderTime()));

        holder.recyclerRowBinding.orderTitle.setText(order.getOrderTitle());
        holder.recyclerRowBinding.orderPrice.setText(new StringBuilder().append("").append(order.getPrice()).toString());

        if (order.getOrderKey().equals("")){
            holder.recyclerRowBinding.orderKey.setText("Key will be appear here!");
        }else {
            holder.recyclerRowBinding.orderKey.setText(new StringBuilder().append("Key: ").append(order.getOrderKey()).toString());
        }




        switch (order.getStatus()){
            case 0:
                holder.recyclerRowBinding.status.setImageResource(R.drawable.ic_baseline_incomplete_circle_24);
                holder.recyclerRowBinding.status.setColorFilter(ContextCompat.getColor(context, R.color.orange_700));
                break;
            case 1:
                holder.recyclerRowBinding.status.setImageResource(R.drawable.ic_baseline_check_24);
                holder.recyclerRowBinding.status.setColorFilter(ContextCompat.getColor(context, R.color.green_800));

                break;

            case 2:
                holder.recyclerRowBinding.status.setImageResource(R.drawable.ic_baseline_close_24);
                holder.recyclerRowBinding.status.setColorFilter(ContextCompat.getColor(context, R.color.error));

                break;

        }

        if (!order.getOrderKey().equals("")){
            holder.itemView.setOnClickListener(view -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("key", order.getOrderKey());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
            });
        }




    }

    private String convertTime(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }


    @Override
    public int getItemCount() {
        return null!= orderArrayList ? orderArrayList.size():0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
