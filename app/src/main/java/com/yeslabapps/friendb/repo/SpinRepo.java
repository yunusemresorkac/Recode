package com.yeslabapps.friendb.repo;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yeslabapps.friendb.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SpinRepo {

    private MutableLiveData<String> errorMessage;


    public SpinRepo(){
        errorMessage = new MutableLiveData<>();
    }


    public void getUserPoint(FirebaseUser firebaseUser, TextView textView){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if (user!=null){
                                textView.setText(new StringBuilder().append("").append(user.getDiamond()).toString());
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public LiveData<String> getError() {
        return errorMessage;
    }


}
