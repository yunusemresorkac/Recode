package com.yeslabapps.friendb.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yeslabapps.friendb.model.Referral;
import com.yeslabapps.friendb.repo.InvitersRepo;

import java.util.List;

public class InvitersViewModel extends ViewModel {

    private InvitersRepo invitersRepo;
    private LiveData<List<Referral>> referralLiveData;
    private LiveData<String> errorMessage;

    public InvitersViewModel() {
        invitersRepo = new InvitersRepo();
        referralLiveData = invitersRepo.getAllInviters();
        errorMessage = invitersRepo.getError();
    }

    public void getInviters(String myId){
        invitersRepo.getInviters(myId);
    }

    public LiveData<List<Referral>> getAllInviters() {
        return referralLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}
