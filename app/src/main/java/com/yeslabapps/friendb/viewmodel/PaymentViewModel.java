package com.yeslabapps.friendb.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yeslabapps.friendb.model.Order;
import com.yeslabapps.friendb.repo.PaymentRepo;

import java.util.List;

public class PaymentViewModel extends ViewModel {

    private PaymentRepo paymentRepo;
    private LiveData<String> errorMessage;
    private LiveData<List<Order>> liveData;


    public PaymentViewModel(){
        paymentRepo = new PaymentRepo();
        errorMessage= paymentRepo.getError();
        liveData = paymentRepo.getAllPayments();
    }

    public void getPayments(String userId){
        paymentRepo.getPayments(userId);
    }

    public LiveData<List<Order>> getAllPayments() {

        return liveData;
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}
