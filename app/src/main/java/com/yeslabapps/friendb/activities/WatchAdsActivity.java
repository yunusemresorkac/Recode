package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityWatchAdsBinding;
import com.yeslabapps.friendb.model.User;
import com.yeslabapps.friendb.viewmodel.SpinViewModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class WatchAdsActivity extends AppCompatActivity {

    private ActivityWatchAdsBinding binding;
    private FirebaseUser firebaseUser;
    private SpinViewModel viewModel;
    private InterstitialAd mInterstitialAd;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWatchAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(this,R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();


        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewModel = new ViewModelProvider(this).get(SpinViewModel.class);
        viewModel.getUserPoint(firebaseUser,binding.adsPoint);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAds();
                pd.dismiss();

            }
        },20);

        binding.watchAdsCard.setOnClickListener(view -> {
            if (mInterstitialAd!=null){
                mInterstitialAd.show(WatchAdsActivity.this);
            }else {
                StyleableToast.makeText(this,getString(R.string.try_again), R.style.customToast).show();

            }
        });


    }

    private void loadAds(){



        MobileAds.initialize(WatchAdsActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();


        InterstitialAd.load(WatchAdsActivity.this, "ca-app-pub-3940256099942544/8691691433", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                //Toast.makeText(WatchAdsActivity.this, "tıklandı", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                addPoint();
                                recreate();
                                //Toast.makeText(WatchAdsActivity.this, "kapandı", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                //Toast.makeText(WatchAdsActivity.this, "tıklandı2", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                                //Toast.makeText(getContext(), "gösteriyor", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                //Toast.makeText(getContext(), "full", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                });
    }

    private void addPoint(){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);

                            HashMap<String, Object> map = new HashMap<>();
                            if (user != null) {
                                map.put("point",user.getGold()+1);
                            }

                            FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            StyleableToast.makeText(WatchAdsActivity.this, "Point added!", R.style.customToast).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(WatchAdsActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
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