package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityLuckySpinBinding;
import com.yeslabapps.friendb.model.Spin;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.yeslabapps.friendb.viewmodel.SpinViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class LuckySpinActivity extends AppCompatActivity {

    private ActivityLuckySpinBinding binding;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;

    private SpinViewModel viewModel;
    private boolean mButtonRotation = true;
    private static final float number = 22.5f;
    private int mDegrees = 0, mOldDegrees = 0;

    private Random random;

    private int point;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLuckySpinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        pd = new ProgressDialog(this, R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();


        random = new Random();
        viewModel = new ViewModelProvider(this).get(SpinViewModel.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserPoint();


        setButtonForSpin();
        binding.spinBtn.setOnClickListener(view -> {
            spin();

        });

    }



//    private long getNow(){
//        AtomicLong timeL = new AtomicLong();
//        OkHttpClient client = new OkHttpClient();
//        String url = "https://api.api-ninjas.com/v1/worldtime?city=London";
//        Request request = new Request.Builder()
//                .url(url)
//                .header("X-Api-Host", "world-time-by-api-ninjas.p.rapidapi.com")
//                .header("X-Api-Key", "5y/pfzoaQ2JFgLzVy78png==ZBA9HRcfCexhLs8w")
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                System.out.println("hata " + e.toString());
//                finish();
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                if (response.isSuccessful()) {
//
//                    runOnUiThread(() -> {
//                        long now;
//
//                        String resStr = null;
//                        try {
//                            resStr = response.body().string();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            JSONObject json = new JSONObject(resStr);
//                            String time = json.getString("datetime");
//
//                            System.out.println("zaman "  + time);
//
//                            now = convertToMil(time);
//                            timeL.set(convertToMil(time));
//                            pd.dismiss();
//                            System.out.println("saniye " + now);
//                            System.out.println("mysaniye " +System.currentTimeMillis() );
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            System.out.println("hata " + e.toString());
//
//                        }
//
//                    });
//                }
//            }
//        });
//        return timeL.get();
//    }
    private Long convertToMil(String date)
    {

        long timeInMilliseconds = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yy-mm-dd hh:mm:ss");
        try {

            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();

        } catch (ParseException  e) {
            e.printStackTrace();
        }

        return  timeInMilliseconds;
    }



    private void getUserPoint() {
        viewModel.getUserPoint(firebaseUser, binding.userPoint);
        pd.dismiss();
    }

    private void animation() {
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

    private String convertTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }

    private void setButtonForSpin() {
        FirebaseDatabase.getInstance("https://bicer-807c5-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Spin").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Spin spin = snapshot.getValue(Spin.class);

                            if (spin != null) {
                                if (System.currentTimeMillis() >= spin.getLuckySpinTime() + 2 * 60 * 1000) {
                                    binding.spinBtn.setEnabled(true);
                                    binding.spinBtn.setText("Spın");


                                } else {
                                    binding.spinBtn.setEnabled(false);
                                    long nextTime = spin.getLuckySpinTime() + 2 * 60 * 1000;
                                    binding.spinBtn.setText(new StringBuilder().append("Come on ").append(convertTime(nextTime)).toString());

                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void spin() {
        if (mButtonRotation && binding.spinBtn.getText().toString().equals("Spın")) {
            FirebaseDatabase.getInstance("https://bicer-807c5-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference().child("Spin").child(firebaseUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Spin spin = snapshot.getValue(Spin.class);

                                if (System.currentTimeMillis() >= spin.getLuckySpinTime() + 36 * 60 * 60 * 1000) {
                                    animation();
                                }
                            } else {
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

        if (degrees >= (number * 0) && degrees < (number * 1)) {
            text = "200";
        }
        if (degrees >= (number * 1) && degrees < (number * 2)) {
            text = "100";
        }
        if (degrees >= (number * 2) && degrees < (number * 3)) {
            text = "50";
        }
        if (degrees >= (number * 3) && degrees < (number * 4)) {
            text = "20";
        }
        if (degrees >= (number * 4) && degrees < (number * 5)) {
            text = "50";
        }
        if (degrees >= (number * 5) && degrees < (number * 6)) {
            text = "100";
        }
        if (degrees >= (number * 6) && degrees < (number * 7)) {
            text = "20";
        }
        if (degrees >= (number * 7) && degrees < (number * 8)) {
            text = "100";
        }
        if (degrees >= (number * 8) && degrees < (number * 9)) {
            text = "200";
        }
        if (degrees >= (number * 9) && degrees < (number * 10)) {
            text = "50";
        }
        if (degrees >= (number * 10) && degrees < (number * 11)) {
            text = "100";
        }
        if (degrees >= (number * 11) && degrees < (number * 12)) {
            text = "20";
        }
        if (degrees >= (number * 12) && degrees < (number * 13)) {
            text = "50";
        }
        if (degrees >= (number * 13) && degrees < (number * 14)) {
            text = "100";
        }
        if (degrees >= (number * 14) && degrees < (number * 15)) {
            text = "50";
        }
        if (degrees >= (number * 15) && degrees < (number * 16)) {
            text = "20";
        }

        return text;


    }


    private void updatePoints() {
        HashMap<String, Object> map = new HashMap<>();
        int result = Integer.parseInt(currentNumber(360 - (mDegrees % 360)));
        try {
            point = Integer.parseInt(binding.userPoint.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        map.put("point", point + result);

        FirebaseDatabase.getInstance("https://bicer-807c5-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(task -> {
                    Toast.makeText(LuckySpinActivity.this, result + " Points Added.", Toast.LENGTH_SHORT).show();
                    updateTime();

                }).addOnFailureListener(e -> Toast.makeText(LuckySpinActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show());


    }

    private void updateTime() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("luckySpinTime", System.currentTimeMillis());
        map.put("userId", firebaseUser.getUid());
        FirebaseDatabase.getInstance("https://bicer-807c5-default-rtdb.europe-west1.firebasedatabase.app/").
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