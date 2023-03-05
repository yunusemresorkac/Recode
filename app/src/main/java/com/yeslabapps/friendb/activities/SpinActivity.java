package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivitySpinBinding;
import com.yeslabapps.friendb.model.Spin;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.yeslabapps.friendb.viewmodel.SpinViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SpinActivity extends AppCompatActivity {

    private ActivitySpinBinding binding;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;

    private SpinViewModel viewModel;
    private boolean mButtonRotation = true;
    private static final float number = 45f;
    private int mDegrees = 0, mOldDegrees = 0;

    private Random random;

    private int point;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbar.setNavigationOnClickListener(view -> finish());


        pd = new ProgressDialog(this,R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();



        random = new Random();
        viewModel = new ViewModelProvider(this).get(SpinViewModel.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserPoint();
        loadAds();

        setButtonForSpin();
        binding.spinBtn.setOnClickListener(view -> {
            if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO) ){
                Appodeal.show(this,Appodeal.REWARDED_VIDEO);
            }else {
                StyleableToast.makeText(this,getString(R.string.try_again),R.style.customToast).show();
            }
        });

    }

    private long getNow(){
        final Calendar calendar = Calendar.getInstance();
        OkHttpClient client = new OkHttpClient();
        String url = "https://www.timeapi.io/api/Time/current/zone?timeZone=Europe/Istanbul";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        String resStr = null;
                        try {
                            resStr = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        try {
                            JSONObject object = null;
                            if (resStr != null) {
                                object = new JSONObject(resStr);
                            }
                            int year = object.getInt("year");
                            int month = object.getInt("month");
                            int day = object.getInt("day");
                            int hour = object.getInt("hour");
                            int minute = object.getInt("minute");
                            calendar.set(year, month, day,
                                    hour, minute, 0);


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    });
                }
            }
        });

        return calendar.getTimeInMillis()/1000;

    }


    private void loadAds(){
        Appodeal.initialize(this,getString(R.string.appodealappid),Appodeal.REWARDED_VIDEO);
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
                spin();
            }

            @Override
            public void onRewardedVideoExpired() {

            }

            @Override
            public void onRewardedVideoClicked() {

            }
        });
    }

    private void getUserPoint(){
        viewModel.getUserPoint(firebaseUser,binding.userPoint);
    }

    private void animation(){
        mOldDegrees = mDegrees % 360;
        mDegrees = random.nextInt(3600) + 720;
        RotateAnimation rotateAnimation = new RotateAnimation(mOldDegrees, mDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(9000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mButtonRotation = false;
                binding.spinBtn.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mButtonRotation = true;
                binding.spinBtn.setEnabled(true);
                updatePoints();


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.luckyWheel.startAnimation(rotateAnimation);
    }

    private String convertTime(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }

    private void setButtonForSpin(){
        FirebaseDatabase.getInstance()
                .getReference().child("Spin").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Spin spin = snapshot.getValue(Spin.class);

                            if (spin != null) {
                                if (getNow() >= spin.getNormalSpinTime() + 60 * 60 * 1000) {
                                    binding.spinBtn.setEnabled(true);

                                }else {
                                    binding.spinBtn.setEnabled(false);
                                    long nextTime = spin.getNormalSpinTime() +  60 * 60 * 1000;
                                    binding.spinBtn.setText(new StringBuilder().append("Come on ").append(convertTime(nextTime)).toString());

                                }
                                pd.dismiss();

                            }
                        }else {
                            pd.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void spin(){
        if (mButtonRotation ) {
            FirebaseDatabase.getInstance()
                    .getReference().child("Spin").child(firebaseUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Spin spin = snapshot.getValue(Spin.class);

                                if (getNow() >= spin.getNormalSpinTime() + 60 * 60 * 1000) {
                                    animation();
                                }
                            }else {
                                animation();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }


    }
    private String currentNumber(int degrees) {
        String text = "";

        if (degrees >= (number * 0) && degrees < (number * 1)){
            text="2";
        }
        if (degrees >= (number * 1) && degrees < (number * 2)){
            text="1";
        }
        if (degrees >= (number * 2) && degrees < (number * 3)){
            text="2";
        }
        if (degrees >= (number * 3) && degrees < (number * 4)){
            text="1";
        }
        if (degrees >= (number * 4) && degrees < (number * 5)){
            text="3";
        }
        if (degrees >= (number * 5) && degrees < (number * 6)){
            text="1";
        }
        if (degrees >= (number * 6) && degrees < (number * 7)){
            text="2";
        }
        if (degrees >= (number * 7) && degrees < (number * 8)){
            text="3";
        }


        return text;


    }



    private void updatePoints(){
        HashMap<String,Object> map = new HashMap<>();
        int result= Integer.parseInt ( currentNumber (360-(mDegrees %  360)));
        try {
            point = Integer.parseInt(binding.userPoint.getText().toString());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        map.put("diamond",point + result);

        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(task -> {
                    StyleableToast.makeText(SpinActivity.this, result+ " " +getString(R.string.addeddia), R.style.customToast).show();


                    updateTime();

                }).addOnFailureListener(e -> Toast.makeText(SpinActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show());


    }

    private void updateTime(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("normalSpinTime",System.currentTimeMillis());
        map.put("userId",firebaseUser.getUid());
        FirebaseDatabase.getInstance().
                getReference().child("Spin").child(firebaseUser.getUid())
                .updateChildren(map);
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