package com.yeslabapps.friendb.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.yeslabapps.friendb.adapter.ReferralAdapter;
import com.yeslabapps.friendb.databinding.ActivityReferralUsersBinding;
import com.yeslabapps.friendb.model.Referral;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.yeslabapps.friendb.viewmodel.InvitersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ReferralUsersActivity extends AppCompatActivity  {

    private ActivityReferralUsersBinding binding;
    private ArrayList<Referral> referralArrayList;
    private ReferralAdapter referralAdapter;

    private FirebaseUser firebaseUser;
    private InvitersViewModel viewModel;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferralUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.toolbar.setNavigationOnClickListener(view -> finish());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        viewModel = new ViewModelProvider(this).get(InvitersViewModel.class);

        initRecycler();

        getData();

    }

    private void initRecycler(){
        referralArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        referralAdapter = new ReferralAdapter(referralArrayList,ReferralUsersActivity.this);
        binding.recyclerView.setAdapter(referralAdapter);

    }

    private void getData(){
        referralArrayList.clear();
        viewModel.getInviters(firebaseUser.getUid());
        viewModel.getAllInviters().observe((LifecycleOwner) this, posts -> {
            referralArrayList.addAll(posts);
            referralAdapter.notifyDataSetChanged();

        });
        viewModel.getErrorMessage().observe(this, error -> {
        });
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