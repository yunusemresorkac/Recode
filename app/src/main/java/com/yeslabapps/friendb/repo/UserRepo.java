package com.yeslabapps.friendb.repo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.UUID;

public class UserRepo {

    private MutableLiveData<String> errorMessage;


    public UserRepo(){
        errorMessage = new MutableLiveData<>();
    }

    public void loginUser(Activity context, String email, String password, FirebaseAuth auth){
        ProgressDialog pd = new ProgressDialog(context,R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            pd.dismiss();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);


                        } else {
                            pd.dismiss();

                            Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();


                        }


                    }
                });

    }


    public void createUser(String deviceId, Activity context, String username, String email, String password, FirebaseAuth auth){

        ProgressDialog pd = new ProgressDialog(context,R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        if (deviceId != null){
            Query query = FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .orderByChild("deviceId").equalTo(deviceId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount()>0){
                        Toast.makeText(context, "You have already another account", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }else if(username.trim().length()>1 && email.trim().length() > 1 && password.trim().length() > 5 ){
                        Query usernameQuery = FirebaseDatabase.getInstance()
                                .getReference().child("Users")
                                .orderByChild("username").equalTo(username.trim());
                        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount()>0){
                                    pd.dismiss();
                                    Toast.makeText(context, "Username already exists.", Toast.LENGTH_SHORT).show();
                                }else {

                                    String userId = auth.getCurrentUser().getUid();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                            .getReference().child("Users").child(userId);

                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("username",username);
                                    map.put("email",email);
                                    map.put("registerDate",System.currentTimeMillis());
                                    map.put("lastSeen",System.currentTimeMillis());
                                    map.put("gold",0);
                                    map.put("diamond",0);
                                    map.put("referralStatus",0);
                                    map.put("userId",userId);
                                    map.put("deviceId",deviceId);
                                    map.put("accountType",0);

                                    databaseReference.setValue(map);

                                    pd.dismiss();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);



                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                pd.dismiss();
                            }
                        });
                    }else {
                        pd.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    pd.dismiss();
                }
            });


        }

    }

    public LiveData<String> getError() {
        return errorMessage;
    }



}
