package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityDiceBinding;
import com.yeslabapps.friendb.model.Dice;
import com.yeslabapps.friendb.model.Spin;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.yeslabapps.friendb.viewmodel.SpinViewModel;

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

public class DiceActivity extends AppCompatActivity {

    private ActivityDiceBinding binding;
    private final Random random=new Random();
    private Animation animation;
    private FirebaseUser firebaseUser;
    private SpinViewModel viewModel;

    private int dia;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbar.setNavigationOnClickListener(view -> finish());

        viewModel = new ViewModelProvider(this).get(SpinViewModel.class);

        loadAds();
        getDiamonds();

        setButtonForDice();

        binding.playBtn.setOnClickListener(view -> {
            play();

        });


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
            }

            @Override
            public void onRewardedVideoExpired() {

            }

            @Override
            public void onRewardedVideoClicked() {

            }
        });
    }



    private void updateTime(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("diceTime",System.currentTimeMillis());
        map.put("userId",firebaseUser.getUid());
        FirebaseDatabase.getInstance().
                getReference().child("Dice").child(firebaseUser.getUid())
                .updateChildren(map);
    }


    private void getDiamonds(){
        viewModel.getUserPoint(firebaseUser,binding.diaCounter);
    }


    private void play(){


        try {
            dia = Integer.parseInt(binding.diaCounter.getText().toString());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        binding.playBtn.setEnabled(false);


        int a = rollDice();
        int b = rollDice2();
        int c = a+b;
        rotateDice();

        addCoins(c);

        StyleableToast.makeText(DiceActivity.this, c + " " + getString(R.string.addeddia), R.style.customToast).show();



    }





    private void addCoins(int result){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            dia = Integer.parseInt(binding.diaCounter.getText().toString());
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                        int updateCoins=dia+ result;



                        HashMap<String, Object> map = new HashMap<>();

                        map.put("diamond",updateCoins);


                        FirebaseDatabase.getInstance()
                                .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            updateTime();
                                        }else {
                                            Toast.makeText(DiceActivity.this, "Error" , Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

        return calendar.getTimeInMillis();

    }

    private String convertTime(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }

    private void setButtonForDice(){
        FirebaseDatabase.getInstance()
                .getReference().child("Dice").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Dice dice = snapshot.getValue(Dice.class);

                            if (dice != null) {
                                if (getNow() >= dice.getDiceTime() + 6 * 60 * 60 * 1000) {
                                    binding.playBtn.setEnabled(true);

                                }else {
                                    binding.playBtn.setEnabled(false);
                                    long nextTime = dice.getDiceTime() +  6 * 60 * 60 * 1000;
                                    binding.playBtn.setText(new StringBuilder().append(getString(R.string.comeon)).append(" ").append(convertTime(nextTime)).toString());

                                }

                            }
                        }else {
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void rotateDice(){
        animation= AnimationUtils.loadAnimation(this, R.anim.anim);
        binding.iv1.startAnimation(animation);
        binding.iv2.startAnimation(animation);
    }

    private int rollDice(){
        int randomNumber=random.nextInt(6)+1;

        switch (randomNumber){
            case 1:
                binding.iv2.setImageResource(R.drawable.one);
                break;
            case 2:
                binding.iv2.setImageResource(R.drawable.two);
                break;
            case 3:
                binding.iv2.setImageResource(R.drawable.three);
                break;
            case 4:
                binding.iv2.setImageResource(R.drawable.four);
                break;
            case 5:
                binding.iv2.setImageResource(R.drawable.five);
                break;
            case 6:
                binding.iv2.setImageResource(R.drawable.six);
                break;
        }
        return randomNumber;
    }

    private int  rollDice2(){
        int randomNumber2=random.nextInt(6)+1;

        switch (randomNumber2){
            case 1:
                binding.iv1.setImageResource(R.drawable.one);
                break;
            case 2:
                binding.iv1.setImageResource(R.drawable.two);
                break;
            case 3:
                binding.iv1.setImageResource(R.drawable.three);
                break;
            case 4:
                binding.iv1.setImageResource(R.drawable.four);
                break;
            case 5:
                binding.iv1.setImageResource(R.drawable.five);
                break;
            case 6:
                binding.iv1.setImageResource(R.drawable.six);
                break;
        }
        return randomNumber2;
    }


    @Override
    protected void onStart() {
        IntentFilter intentFilter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}