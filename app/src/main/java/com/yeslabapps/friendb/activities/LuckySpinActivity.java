package com.yeslabapps.friendb.activities;

import static com.android.billingclient.api.BillingClient.BillingResponseCode.OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.common.collect.ImmutableList;
import com.google.firebase.database.DatabaseReference;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityLuckySpinBinding;
import com.yeslabapps.friendb.model.User;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class LuckySpinActivity extends AppCompatActivity {

    private ActivityLuckySpinBinding binding;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;
    private BillingClient billingClient;
    private ProductDetails productDetails;
    private Purchase purchase;
    private String paketId = "get_premium";
    static final String TAG_IAP ="InAppPurchaseTag";

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLuckySpinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbar.setNavigationOnClickListener(view -> finish());


        pd = new ProgressDialog(this, R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();




        billingSetup();

        binding.makePurchase.setOnClickListener(view -> {
            makePurchase();
        });



    }



    private void billingSetup() {

        billingClient = BillingClient.newBuilder(LuckySpinActivity.this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();


        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(
                    @NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() ==
                        OK) {
                    Log.i(TAG_IAP, "OnBillingSetupFinish connected");


                    queryProduct();

                } else {
                    Log.i(TAG_IAP, "OnBillingSetupFinish failed");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.i(TAG_IAP, "OnBillingSetupFinish connection lost");
            }
        });
    }

    private void queryProduct() {

        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(paketId)
                                                .setProductType(
                                                        BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, productDetailsList) -> {

                    if (!productDetailsList.isEmpty()) {
                        productDetails = productDetailsList.get(0);
                        runOnUiThread(() -> {

                            binding.makePurchase.setEnabled(true);
                            List<String> skuList = new ArrayList<>();
                            skuList.add(paketId);
                            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                            billingClient.querySkuDetailsAsync(params.build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == OK && list != null ) {
                                    for (SkuDetails skuDetails : list) {
                                        String price = skuDetails.getPrice();

                                        binding.makePurchase.setText(getString(R.string.buy) + " - " +price);
                                        //binding.paketBilgi.setText(new StringBuilder().append("Satın Al ").append(productDetails.getName()).append(" ").append(price).toString());

                                        //System.out.println("fiyat "+price);
                                        //Toast.makeText(RemoveAdsActivity.this, "fiyat "+ price, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                            pd.dismiss();
                        });
                    } else {
                        Log.i(TAG_IAP, "onProductDetailsResponse: No products");
                        pd.dismiss();
                        finish();
                    }
                }
        );
    }

    private void makePurchase() {

        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                                ImmutableList.of(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(productDetails)
                                                .build()
                                )
                        )
                        .build();

        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {

        if (billingResult.getResponseCode() ==
                OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                completePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() ==
                BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(TAG_IAP, "onPurchasesUpdated: Purchase Canceled");
            //StyleableToast.makeText(CompleteBuyActivity.this, "Bir hata oldu. Yeniden deneyin.", R.style.customToast).show();

        } else {
            Log.i(TAG_IAP, "onPurchasesUpdated: Error");
            StyleableToast.makeText(LuckySpinActivity.this, getString(R.string.try_again), R.style.customToast).show();

        }
    };

    private void completePurchase(Purchase item) {

        purchase = item;
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            runOnUiThread(() -> {
                binding.makePurchase.setText(R.string.succes);
                FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User user = snapshot.getValue(User.class);

                                    if (user!=null){
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("accountType",1);
                                        FirebaseDatabase.getInstance()
                                                .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()){
                                                        SharedPreferences sharedPreferences = getSharedPreferences("PREFS",0);
                                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                                        editor.putInt("accountType",1);
                                                        editor.apply();
                                                        consumePurchase();

                                                        addBuyers();

                                                    }
                                                }).addOnFailureListener(e -> Toast.makeText(LuckySpinActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show());
                                    }




                                    //StyleableToast.makeText(CompleteBuyActivity.this, "Satın alım başarılı. Elmaslar bakiyene eklendi.", R.style.customToast).show();




                                }

                            }



                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            });


    }

    private void consumePurchase() {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult,
                                          @NonNull String purchaseToken) {
                if (billingResult.getResponseCode() ==
                        OK) {
                    runOnUiThread(() -> {
                        //binding.premiumInfo.setText("Purchase Consumed");
                    });
                }
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }

    private void addBuyers(){

        HashMap<String,Object> hashMap = new HashMap<>();

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Buys");

        String buyId = reference.push().getKey();

        hashMap.put("buyerId",firebaseUser.getUid());
        hashMap.put("product","Premium");
        hashMap.put("buyId",buyId);
        hashMap.put("time",""+System.currentTimeMillis());

        if (buyId != null) {
            reference.child(buyId).setValue(hashMap);
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