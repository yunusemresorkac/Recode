package com.yeslabapps.friendb.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.DiceActivity;
import com.yeslabapps.friendb.activities.LuckySpinActivity;
import com.yeslabapps.friendb.activities.PaymentHistoryActivity;
import com.yeslabapps.friendb.activities.SpinActivity;
import com.yeslabapps.friendb.activities.WatchAdsActivity;
import com.yeslabapps.friendb.adapter.MyMenuAdapter;
import com.yeslabapps.friendb.interfaces.OnClick;
import com.yeslabapps.friendb.databinding.FragmentHomeBinding;
import com.yeslabapps.friendb.model.MyMenu;
import com.yeslabapps.friendb.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomeFragment extends Fragment implements OnClick {


    private FragmentHomeBinding binding;
    private ArrayList<MyMenu> menuArrayList;
    private MyMenuAdapter menuAdapter;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        pd.setCancelable(true);
        pd.show();

        setCircular();
        loadAds();
        initRecycler();

        new Handler().postDelayed(()-> pd.dismiss(),3000);

        binding.goPremium.setOnClickListener(view -> startActivity(new Intent(getContext(), LuckySpinActivity.class)));

        return binding.getRoot();
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



    private void setCircular(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);

                            binding.homeDia.setText(new StringBuilder().append("").append(user.getDiamond()));

                            binding.progressBarCircle.setMax(250);
                            try {
                                binding.progressBarCircle.setProgress((float) user.getDiamond());
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }

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
        menuArrayList.add(new MyMenu(getString(R.string.watchearn), R.drawable.higher_order_functions_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.spinearn), R.drawable.time_complexity_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.inviteearn), R.drawable.community_share_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.dice),R.drawable.dice_svgrepo_com));

    }


    @Override
    public void onClick(int position) {
        switch (position){
            case 0:
                if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO) ){
                    Appodeal.show((Activity) getContext(),Appodeal.REWARDED_VIDEO);
                }else {
                    StyleableToast.makeText(getContext(),"Please try again!",R.style.customToast).show();
                }

                break;

            case 1:
                startActivity(new Intent(getContext(), SpinActivity.class));
                break;


            case 2:
                String shareText = new StringBuilder().append("Come to Recode and Earn Game Codes. Use my referral link. ")

                        .append("https://play.google.com/store/apps/details?id=com.yeslabapps.friendb&referrer=").append(firebaseUser.getUid()).toString();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(intent, "Share Via"));

                break;

            case 3:
                controlForDice();

                break;



        }
    }
    private void controlForDice(){
        SharedPreferences preferences= getContext().getSharedPreferences("PREFS",0);
        int accountType=preferences.getInt("accountType",0);
        if(accountType==1){
            startActivity(new Intent(getContext(), DiceActivity.class));
        }else {
            showPreDialog();
        }
    }


    private void showPreDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_inapp);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        MaterialButton button = dialog.findViewById(R.id.seePre);
        button.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(getContext(),LuckySpinActivity.class));
        });
    }
}





