package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.util.NetworkChangeListener;
import com.yeslabapps.friendb.viewmodel.UserViewModel;
import com.yeslabapps.friendb.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserViewModel viewModel;

    private FirebaseAuth firebaseAuth;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!=null){
            Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }



        viewModel = new ViewModelProvider(this).get(UserViewModel.class);




        binding.showLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loginLayout.setVisibility(View.VISIBLE);
                binding.registerLayout.setVisibility(View.GONE);

            }
        });
        binding.showRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loginLayout.setVisibility(View.GONE);
                binding.registerLayout.setVisibility(View.VISIBLE);

            }
        });




        binding.btnRegister.setOnClickListener(view -> {

            firebaseAuth.createUserWithEmailAndPassword(binding.emailRegister.getText().toString().trim(),binding.passwordRegister.getText().toString().trim()).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        register();

                    }
                }
            });


        });

        binding.btnLogin.setOnClickListener(view -> {
            login();
        });


        binding.forgotPassword.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_forgot_password);

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            dialog.getWindow().setGravity(Gravity.BOTTOM);

            EditText editText = dialog.findViewById(R.id.forgotPasswordEt);
            Button button = dialog.findViewById(R.id.sendPasswordLink);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mail = editText.getText().toString().trim();
                    if (mail.length() > 1) {
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                                CookieBar.build(LoginActivity.this)
                                        .setTitle(R.string.forgotinfo)
                                        .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                        .show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Bir hata oldu. Yeniden deneyin.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });

        });
    }

    private void login(){
        if (binding.emailLogin.getText().toString().trim().length()>0 && binding.passwordLogin.getText().toString().trim().length()>0){
            viewModel.loginUser(LoginActivity.this,binding.emailLogin.getText().toString().trim(),binding.passwordLogin.getText().toString().trim()
                    ,firebaseAuth);
        }

    }


    private void register(){

        if (binding.emailRegister.getText().toString().trim().length()>0 && binding.usernameRegister.getText().toString().trim().length()>0&&
        binding.passwordRegister.getText().toString().trim().length()>0){
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            viewModel.createUser(deviceId,LoginActivity.this,binding.usernameRegister.getText().toString().trim(),
                    binding.emailRegister.getText().toString().trim(),binding.passwordRegister.getText().toString().trim(), firebaseAuth);

        }

    }

//
//    //google login
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // Check condition
//        if(requestCode==100)
//        {
//
//            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn
//                    .getSignedInAccountFromIntent(data);
//
//            if(signInAccountTask.isSuccessful())
//            {
//
//                try {
//                    GoogleSignInAccount googleSignInAccount=signInAccountTask
//                            .getResult(ApiException.class);
//                    // Check condition
//                    if(googleSignInAccount!=null)
//                    {
//
//
//                        AuthCredential authCredential= GoogleAuthProvider
//                                .getCredential(googleSignInAccount.getIdToken()
//                                        ,null);
//                        // Check credential
//                        firebaseAuth.signInWithCredential(authCredential)
//                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        // Check condition
//                                        if(task.isSuccessful())
//                                        {
//                                            // When task is successful
//                                            // Redirect to profile activity
//                                            FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();
//
//                                            addToDatabase(firebaseUser);
//
//
//                                            //displayToast("Firebase authentication successful");
//                                        }
//
//                                        else
//                                        {
//                                            // When task is unsuccessful
//                                            // Display Toast
//
//                                        }
//                                    }
//                                });
//
//                    }
//                }
//                catch (ApiException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void addToDatabase(FirebaseUser firebaseUser) {
//        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        if (deviceId != null) {
//            Query query = FirebaseDatabase.getInstance()
//                    .getReference().child("Users")
//                    .orderByChild("email").equalTo(firebaseUser.getEmail());
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.getChildrenCount()<=0){
//                        String userId = firebaseAuth.getCurrentUser().getUid();
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
//                                .getReference().child("Users").child(userId);
//
//                        HashMap<String,Object> map = new HashMap<>();
//                        map.put("username",firebaseUser.getDisplayName());
//                        map.put("email",firebaseUser.getEmail());
//                        map.put("registerDate",System.currentTimeMillis());
//                        map.put("lastSeen",System.currentTimeMillis());
//                        map.put("point",0);
//                        map.put("dollar",0);
//                        map.put("userId",userId);
//                        map.put("imageUrl","default");
//                        map.put("deviceId",deviceId);
//                        map.put("accountType",0);
//                        map.put("referralLink", UUID.randomUUID().toString().substring(0,8));
//
//                        databaseReference.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }else {
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//
//
//
//      }


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


