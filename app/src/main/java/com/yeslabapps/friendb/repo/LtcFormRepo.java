package com.yeslabapps.friendb.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yeslabapps.friendb.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LtcFormRepo {

    private MutableLiveData<String> errorMessage;


    public LtcFormRepo(){
        errorMessage = new MutableLiveData<>();
    }


    public void sendForm(double withdraw,String address,String senderId){

        long time =System.currentTimeMillis();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("time",time);
        hashMap.put("withdraw",withdraw);
        hashMap.put("address",address);
        hashMap.put("senderId",senderId);
        hashMap.put("status",0);
        hashMap.put("coinType","LTC");

        FirebaseDatabase.getInstance()
                .getReference().child("Payments").child(String.valueOf(time)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        lostDollars(senderId,withdraw);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void lostDollars(String myId,double withdraw){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);

                            HashMap<String, Object> map = new HashMap<>();
                            if (user != null) {
                                map.put("dollar",user.getDiamond() - withdraw);
                            }

                            FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(myId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });




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
