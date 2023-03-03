package com.yeslabapps.friendb.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductsRepo {

    private final FirebaseDatabase database;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<List<Product>> mutableLiveData;
    private final List<Product> productList;

    public ProductsRepo(){
        database = FirebaseDatabase.getInstance();
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        productList = new ArrayList<>();

    }

    public void getProducts(){
        try {
            database.getReference().child("Products")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            productList.clear();
                            try {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (snapshot.exists()){
                                        Product product = dataSnapshot.getValue(Product.class);

                                        if (product != null && product.getStock() == 1) {
                                            productList.add(product);
                                            System.out.println("başlık " + product.getTitle());
                                        }


                                    }


                                }
                                Collections.reverse(productList);
                                mutableLiveData.postValue(productList);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("hata1 " + e.getMessage());

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            errorMessage.postValue(error.getMessage());
                            System.out.println("hata2 " + error.getMessage());
                        }
                    });
        } catch (
                Exception e) {
            System.out.println("hata3 "+ e.getMessage());

            e.printStackTrace();
        }
    }


    public LiveData<List<Product>> getAllProducts() {
        return mutableLiveData;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }





}
