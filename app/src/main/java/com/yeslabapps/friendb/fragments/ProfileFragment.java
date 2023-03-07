package com.yeslabapps.friendb.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.EditProfileActivity;
import com.yeslabapps.friendb.activities.PaymentHistoryActivity;
import com.yeslabapps.friendb.activities.ReferralUsersActivity;
import com.yeslabapps.friendb.activities.SettingsActivity;
import com.yeslabapps.friendb.activities.WatchAdsActivity;
import com.yeslabapps.friendb.adapter.MyMenuAdapter;
import com.yeslabapps.friendb.interfaces.OnClick;
import com.yeslabapps.friendb.databinding.FragmentProfileBinding;
import com.yeslabapps.friendb.model.MyMenu;
import com.yeslabapps.friendb.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class ProfileFragment extends Fragment implements OnClick {


    private FragmentProfileBinding binding;
    private ProgressDialog pd;

    private FirebaseUser firebaseUser;
    private ArrayList<MyMenu> menuArrayList;
    private MyMenuAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        pd = new ProgressDialog(getContext(), R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        initRecycler();

        getUserData();

        binding.goEditProfile.setOnClickListener(view -> startActivity(new Intent(getContext(), EditProfileActivity.class)));



        loadAds();


        return binding.getRoot();
    }


    private void getUserData(){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if (user!=null){

                                binding.goldText.setText(new StringBuilder().append("").append(user.getGold()).toString());
                                binding.diamondText.setText(new StringBuilder().append("").append(user.getDiamond()).toString());

                                binding.mainName.setText(new StringBuilder().append("").append(user.getUsername()).toString());

                                pd.dismiss();
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pd.dismiss();
                    }
                });
    }



    private void converter(){
        int point;
        try {
            point = Integer.parseInt(binding.diamondText.getText().toString());
            if (point >249){
                addGold();
            }else {
                StyleableToast.makeText(getContext(), getString(R.string.minimum), R.style.customToast).show();
            }

        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

    private void addGold(){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);

                            HashMap<String, Object> map = new HashMap<>();
                            if (user != null) {
                                map.put("gold",user.getGold()+ 1 );
                            }

                            FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            int point;
                                            try {
                                                point = Integer.parseInt(binding.diamondText.getText().toString());
                                                if (point>0){
                                                    lostDiamond();

                                                }

                                            }catch (NumberFormatException e){
                                                e.printStackTrace();
                                            }
                                            StyleableToast.makeText(getContext(), "Gold added.", R.style.customToast).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                        }
                                    });




                        }


                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addDia(){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);

                            HashMap<String, Object> map = new HashMap<>();
                            if (user != null) {
                                map.put("diamond",user.getDiamond()+ 3 );
                            }

                            FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            StyleableToast.makeText(getContext(), "Diamonds added.", R.style.customToast).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                        }
                                    });




                        }


                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }









    private void initRecycler(){
        menuArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.recyclerView.setHasFixedSize(true);
        menuAdapter = new MyMenuAdapter(menuArrayList,getContext(),this);
        binding.recyclerView.setAdapter(menuAdapter);
        menuArrayList.add(new MyMenu(getString(R.string.watchearn), R.drawable.best_practices_3_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.convert),R.drawable.time_to_interactive_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.history),R.drawable.scrum_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.settings),R.drawable.css_variables_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.myreferrals),R.drawable.community_share_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.about),R.drawable.info_svgrepo_com));


    }

    private void loadAds(){
        Appodeal.initialize((Activity) getContext(),getString(R.string.appodealappid),Appodeal.REWARDED_VIDEO);
        Appodeal.isLoaded(Appodeal.REWARDED_VIDEO);
        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean b) {

            }

            @Override
            public void onRewardedVideoFailedToLoad() {

            }

            @Override
            public void onRewardedVideoShown() {
            }

            @Override
            public void onRewardedVideoShowFailed() {

            }

            @Override
            public void onRewardedVideoFinished(double v, String s) {

            }

            @Override
            public void onRewardedVideoClosed(boolean b) {
                addDia();
            }

            @Override
            public void onRewardedVideoExpired() {

            }

            @Override
            public void onRewardedVideoClicked() {

            }
        });
    }


    @Override
    public void onClick(int position) {
        switch (position){
            case 0:
                if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO) ){
                    Appodeal.show((Activity) getContext(),Appodeal.REWARDED_VIDEO);
                }else {
                    StyleableToast.makeText(getContext(),getString(R.string.try_again),R.style.customToast).show();
                }
                break;

            case 1:
                converter();
                break;

            case 2:
                startActivity(new Intent(getContext(), PaymentHistoryActivity.class));

                break;
            case 3:
                startActivity(new Intent(getContext(), SettingsActivity.class));

                break;
            case 4:
                startActivity(new Intent(getContext(), ReferralUsersActivity.class));

                break;

            case 5:
                aboutUsDialog();
                break;
        }
    }

    private void aboutUsDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_summary);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

    }

    private void lostDiamond(){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);

                            HashMap<String, Object> map = new HashMap<>();
                            if (user != null) {
                                map.put("diamond",user.getDiamond() - 250);
                            }

                            FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                        }
                                    });




                        }


                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }





}
