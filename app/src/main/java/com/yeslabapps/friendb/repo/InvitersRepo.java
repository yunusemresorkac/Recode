package com.yeslabapps.friendb.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yeslabapps.friendb.model.Referral;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InvitersRepo {

    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<List<Referral>> mutableLiveData;
    private final List<Referral> referralList;


    public InvitersRepo(){
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        referralList = new ArrayList<>();

    }

    public void getInviters(String myId){
        FirebaseDatabase.getInstance().getReference().child("Referrals").child(myId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                            if (snapshot.exists()){
                                Referral referral = dataSnapshot.getValue(Referral.class);
                                referralList.add(referral);
                            }
                        }
                        mutableLiveData.postValue(referralList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public LiveData<List<Referral>> getAllInviters() {
        return mutableLiveData;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }

}