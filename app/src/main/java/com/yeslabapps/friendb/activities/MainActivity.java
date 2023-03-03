package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.MenuItem;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityMainBinding;
import com.yeslabapps.friendb.fragments.HomeFragment;
import com.yeslabapps.friendb.fragments.ProfileFragment;
import com.yeslabapps.friendb.fragments.MarketFragment;
import com.yeslabapps.friendb.model.User;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements InstallReferrerStateListener {


    private ActivityMainBinding binding;
    private Fragment selectorFragment;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private InstallReferrerClient mReferrerClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser==null){
            finish();
        }



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
        mReferrerClient = InstallReferrerClient.newBuilder(this).build();
        mReferrerClient.startConnection(this);

        binding.bottom.setItemIconTintList(null);

        binding.bottom.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navHome:
                    selectorFragment = new HomeFragment();
                    break;

                case R.id.navInvite:
                    selectorFragment = new MarketFragment();
                    break;

                case R.id.navProfile:
                    selectorFragment = new ProfileFragment();
                    break;




            }
            if (selectorFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
            }
            return true;
        });

        updateLastSeen();



    }

    private void sendPointToInviter(String userId){
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    HashMap<String,Object> map = new HashMap<>();
                    if (user!=null){
                        FirebaseDatabase.getInstance().getReference().child("Points").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    map.put("diamond", user.getDiamond() +50);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                                            .updateChildren(map);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void saveUserToMyReferrals(String userId){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("inviterId",userId);
        hashMap.put("receiverId",firebaseUser.getUid());

        FirebaseDatabase.getInstance()
                .getReference().child("Referrals").child(userId).child(firebaseUser.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(unused -> {

                }).addOnFailureListener(e -> {
                });
    }
    @Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.OK:
                try {
                    ReferrerDetails response = mReferrerClient.getInstallReferrer();
                    String referrer = response.getInstallReferrer();

                    FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user = snapshot.getValue(User.class);
                                if (user != null && user.getReferralStatus() == 0) {
                                    if (referrer.length()==28){
                                        sendPointToInviter(referrer);
                                        saveUserToMyReferrals(referrer);
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("referralStatus", 1);
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                                                .updateChildren(hashMap);
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    System.out.println("davet " + referrer);
                    mReferrerClient.endConnection();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                break;
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                break;
            default:
                ;
        }
    }

    @Override
    public void onInstallReferrerServiceDisconnected() {

    }

    private void updateLastSeen(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("lastSeen",System.currentTimeMillis());

        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);

    }



    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }



}