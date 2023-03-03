package com.yeslabapps.friendb.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.BuyActivity;
import com.yeslabapps.friendb.activities.ReferralUsersActivity;
import com.yeslabapps.friendb.adapter.ProductAdapter;
import com.yeslabapps.friendb.adapter.UserAdapter;
import com.yeslabapps.friendb.databinding.FragmentMarketBinding;
import com.yeslabapps.friendb.interfaces.OnClick;
import com.yeslabapps.friendb.model.Product;
import com.yeslabapps.friendb.model.User;
import com.yeslabapps.friendb.viewmodel.ProductsViewModel;
import com.yeslabapps.friendb.viewmodel.ReferralViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;


public class MarketFragment extends Fragment implements OnClick  {

    private FragmentMarketBinding binding;
    private FirebaseUser firebaseUser;
    private ArrayList<Product> productArrayList;
    private ProductAdapter productAdapter;

    private ProductsViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMarketBinding.inflate(inflater, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewModel = new ViewModelProvider(this).get(ProductsViewModel.class);

        initRecycler();


        getProducts();

        return binding.getRoot();

    }

    private void initRecycler(){
        productArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.recyclerView.setHasFixedSize(true);
        productAdapter = new ProductAdapter(productArrayList,getContext(),this);
        binding.recyclerView.setAdapter(productAdapter);
    }

    private void getProducts(){
        viewModel.getProducts();
        productArrayList.clear();
        viewModel.getAllProducts().observe(getViewLifecycleOwner(), posts -> {
            productArrayList.addAll(posts);
            productAdapter.notifyDataSetChanged();
            if (productArrayList.size()==0){
                binding.emptyMarketText.setVisibility(View.VISIBLE);
            }else {
                binding.emptyMarketText.setVisibility(View.GONE);
            }

        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
        });

    }

    @Override
    public void onClick(int position) {
            Intent intent = new Intent(getContext(), BuyActivity.class);
            intent.putExtra("productId",productArrayList.get(position).getProductId());
            startActivity(intent);

        }
    }

