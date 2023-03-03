package com.yeslabapps.friendb.viewmodel;

import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yeslabapps.friendb.repo.SpinRepo;
import com.google.firebase.auth.FirebaseUser;

public class SpinViewModel extends ViewModel {

    private SpinRepo spinRepo;
    private LiveData<String> errorMessage;


    public SpinViewModel(){
        spinRepo = new SpinRepo();
        errorMessage = spinRepo.getError();
    }

    public void getUserPoint(FirebaseUser firebaseUser, TextView textView){
        spinRepo.getUserPoint(firebaseUser,textView);
    }

}
