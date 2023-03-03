package com.yeslabapps.friendb.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yeslabapps.friendb.repo.LtcFormRepo;

public class LtcFormViewModel extends ViewModel {

    private LtcFormRepo formRepo;
    private LiveData<String> errorMessage;


    public LtcFormViewModel(){
        formRepo = new LtcFormRepo();
        errorMessage = formRepo.getError();
    }

    public void sendForm(double withdraw,String address,String senderId){
        formRepo.sendForm(withdraw,address,senderId);
    }

}
