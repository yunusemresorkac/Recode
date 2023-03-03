package com.yeslabapps.friendb.repo;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yeslabapps.friendb.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaymentRepo {

    private FirebaseDatabase database;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<List<Order>> mutableLiveData;
    private List<Order> orderList;

    public PaymentRepo(){
        database = FirebaseDatabase.getInstance();
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        orderList = new ArrayList<>();

    }

    public void getPayments(String userId){
        try {
            database.getReference().child("Orders")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            orderList.clear();
                            try {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (snapshot.exists()){
                                        Order order = dataSnapshot.getValue(Order.class);

                                        if (order != null && userId.equals(order.getCustomerId())) {
                                            orderList.add(order);
                                        }


                                    }


                                }
                                Collections.reverse(orderList);
                                mutableLiveData.postValue(orderList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            errorMessage.postValue(error.getMessage());
                        }
                    });
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }


    public LiveData<List<Order>> getAllPayments() {
        return mutableLiveData;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }


}
