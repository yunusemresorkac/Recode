package com.yeslabapps.friendb.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.adapter.PaymentAdapter;
import com.yeslabapps.friendb.databinding.ActivityPaymentHistoryBinding;
import com.yeslabapps.friendb.model.Order;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.yeslabapps.friendb.viewmodel.PaymentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PaymentHistoryActivity extends AppCompatActivity {

    private ActivityPaymentHistoryBinding binding;

    private ArrayList<Order> orderArrayList;
    private PaymentAdapter paymentAdapter;
    private PaymentViewModel viewModel;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.toolbar.setNavigationOnClickListener(view -> finish());


        pd = new ProgressDialog(this,R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);



        initRecycler();

        getPayments();

    }



    private void initRecycler(){
        orderArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        paymentAdapter = new PaymentAdapter(orderArrayList,this);
        binding.recyclerView.setAdapter(paymentAdapter);
    }

    private void getPayments() {


        viewModel.getPayments(firebaseUser.getUid());
        viewModel.getAllPayments().observe((LifecycleOwner) this, posts -> {
            pd.dismiss();
            orderArrayList.addAll(posts);
            paymentAdapter.notifyDataSetChanged();

        });
        viewModel.getErrorMessage().observe(this, error -> {
            pd.dismiss();
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