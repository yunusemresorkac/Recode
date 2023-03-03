package com.yeslabapps.friendb.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yeslabapps.friendb.model.Product;
import com.yeslabapps.friendb.repo.ProductsRepo;

import java.util.List;

public class ProductsViewModel extends ViewModel {

    private final LiveData<List<Product>> listLiveData;
    private final LiveData<String> errorMessage;
    private final ProductsRepo productsRepo;

    public ProductsViewModel(){
        productsRepo = new ProductsRepo();
        listLiveData = productsRepo.getAllProducts();
        errorMessage = productsRepo.getError();

    }

    public void getProducts(){
        productsRepo.getProducts();
    }


    public LiveData<List<Product>> getAllProducts() {
        return listLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }


}
