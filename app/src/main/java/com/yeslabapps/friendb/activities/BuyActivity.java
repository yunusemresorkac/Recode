package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityBuyBinding;
import com.yeslabapps.friendb.model.Product;
import com.yeslabapps.friendb.model.User;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class BuyActivity extends AppCompatActivity {

    private ActivityBuyBinding binding;

    private String productId;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbar.setNavigationOnClickListener(view -> finish());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        productId = getIntent().getStringExtra("productId");

        getProductDetail();


        loadBanner();
        binding.buyBtn.setOnClickListener(view -> buyProduct());


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


    private void lostGolds(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);

                            int price = 0;
                            try {
                                price = Integer.parseInt(binding.productPrice.getText().toString());
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }

                            HashMap<String,Object> hashMap = new HashMap<>();
                            if (user != null) {
                                hashMap.put("gold", user.getGold() - price );
                                FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).updateChildren(hashMap).addOnSuccessListener(unused -> {
                                    StyleableToast.makeText(BuyActivity.this,"Purchase Successful",R.style.customToast).show();
                                    finish();
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void createOrder(){

        int price = 0;
        try {
            price = Integer.parseInt(binding.productPrice.getText().toString());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        String orderId = reference.push().getKey();
        hashMap.put("orderId",orderId);
        hashMap.put("orderTime",System.currentTimeMillis());
        hashMap.put("customerId",firebaseUser.getUid());
        hashMap.put("status",0);
        hashMap.put("orderKey","");
        hashMap.put("orderTitle",binding.productTitle.getText().toString());
        hashMap.put("price",price);
        hashMap.put("orderProductId",productId);

        if (orderId != null) {
            reference.child("Orders").child(orderId).setValue(hashMap).addOnSuccessListener(unused -> lostGolds());
        }

    }

    private void buyProduct(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    int price = 0;
                    try {
                        price = Integer.parseInt(binding.productPrice.getText().toString());
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }

                    if (user != null && user.getGold() >= price) {
                        createOrder();
                    }else {
                        CookieBar.build(BuyActivity.this)
                                .setTitle(getString(R.string.nobalance))
                                .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                .show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getProductDetail(){
        FirebaseDatabase.getInstance().getReference().child("Products").child(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Product product = snapshot.getValue(Product.class);

                            if (product!=null){
                                binding.desc.setText(product.getDescription());
                                binding.productTitle.setText(product.getTitle());
                                binding.productPrice.setText(new StringBuilder().append("").append(product.getGold()).toString());

                                Picasso.get().load(product.getImage()).into(binding.productImage);


                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



}