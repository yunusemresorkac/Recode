package com.yeslabapps.friendb.viewmodel;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yeslabapps.friendb.repo.UserRepo;
import com.google.firebase.auth.FirebaseAuth;

public class UserViewModel extends ViewModel {

    private UserRepo userRepo;
    private LiveData<String> errorMessage;

    public UserViewModel(){
        userRepo = new UserRepo();
        errorMessage = userRepo.getError();
    }

    public void createUser(String deviceId, Activity context, String username, String email, String password, FirebaseAuth auth){
        userRepo.createUser(deviceId,context,username,email,password,auth);
    }

    public void loginUser(Activity context, String email, String password, FirebaseAuth auth){
        userRepo.loginUser(context,email,password,auth);
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}
