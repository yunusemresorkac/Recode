package com.yeslabapps.friendb.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yeslabapps.friendb.model.User;
import com.yeslabapps.friendb.repo.ReferralRepo;

import java.util.List;

public class ReferralViewModel extends ViewModel {

    private ReferralRepo referralRepo;
    private LiveData<String> errorMessage;
    private LiveData<List<User>> liveData;


    public ReferralViewModel(){
        referralRepo = new ReferralRepo();
        errorMessage= referralRepo.getError();
        liveData = referralRepo.getUser();
    }

    public void getReferral(String link){
        referralRepo.getReferral(link);
    }


    public LiveData<List<User>> getUser() {

        return liveData;
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}
