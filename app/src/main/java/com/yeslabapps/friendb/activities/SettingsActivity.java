package com.yeslabapps.friendb.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.adapter.MyMenuAdapter;
import com.yeslabapps.friendb.databinding.ActivitySettingsBinding;
import com.yeslabapps.friendb.interfaces.OnClick;
import com.yeslabapps.friendb.model.MyMenu;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements OnClick {

    private ActivitySettingsBinding binding;

    private ArrayList<MyMenu> menuArrayList;
    private MyMenuAdapter menuAdapter;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();

        initRecycler();
        loadBanner();

    }

    private void loadBanner(){

        Appodeal.initialize(this,getString(R.string.appodealappid),Appodeal.BANNER);
        Appodeal.show(this,Appodeal.BANNER);
        Appodeal.isLoaded(Appodeal.BANNER);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int i, boolean b) {
            }

            @Override
            public void onBannerFailedToLoad() {

            }

            @Override
            public void onBannerShown() {
            }

            @Override
            public void onBannerShowFailed() {

            }

            @Override
            public void onBannerClicked() {

            }

            @Override
            public void onBannerExpired() {

            }
        });

    }



    private void initRecycler(){
        menuArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        binding.recyclerView.setHasFixedSize(true);
        menuAdapter = new MyMenuAdapter(menuArrayList,this,this);
        binding.recyclerView.setAdapter(menuAdapter);
        menuArrayList.add(new MyMenu(getString(R.string.edit_profile), R.drawable.pencil_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.support), R.drawable.headset_support_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.rateus), R.drawable.star_review_ecommerce_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.shareapp), R.drawable.share_svgrepo_com));
        menuArrayList.add(new MyMenu(getString(R.string.signout), R.drawable.ic_baseline_logout_24));

    }


    @Override
    public void onClick(int position) {
        switch (position){
            case 0:startActivity(new Intent(SettingsActivity.this,EditProfileActivity.class));
                break;
            case 1:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:yeslabapps@gmail.com"));
                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                break;
            case 2:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.yeslabapps.friendb")));

                break;
            case 3:
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"");
                String app_url = "https://play.google.com/store/apps/details?id=com.yeslabapps.friendb";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                startActivity(Intent.createChooser(shareIntent,"Share Via"));
                break;
            case 4:
                firebaseAuth.signOut();
                Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
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