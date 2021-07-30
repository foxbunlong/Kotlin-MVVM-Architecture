package com.example.mvvmarchitecture.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvmarchitecture.data.local.helpers.Resource;
import com.example.mvvmarchitecture.data.local.models.CryptoCoin;
import com.example.mvvmarchitecture.data.remote.CryptoCoinRepository;

import java.util.List;

public class CryptoCoinListViewModel extends AndroidViewModel {

    private static final String TAG = "ListViewModel";

    private MediatorLiveData<Resource<List<CryptoCoin>>> coins = new MediatorLiveData<>();
    private CryptoCoinRepository coinRepository;

    // query extras
    private boolean isPerformingQuery = false;
    private String currencyUnit;
    private boolean cancelRequest;
    private long requestStartTime;

    public CryptoCoinListViewModel(@NonNull Application application) {
        super(application);
        coinRepository = CryptoCoinRepository.getInstance(application);
    }

    public LiveData<Resource<List<CryptoCoin>>> getCoins() {
        return coins;
    }

    public void getCoinsApi(String currencyUnit) {
        if (!isPerformingQuery) {
            this.currencyUnit = currencyUnit;
            executeGetData();
        }
    }

    private void executeGetData() {
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;
        final LiveData<Resource<List<CryptoCoin>>> repositorySource = coinRepository.getCoinsApi(currencyUnit);
        coins.addSource(repositorySource, listResource -> {
            if (!cancelRequest) {
                if (listResource != null) {
                    if (listResource.status == Resource.Status.SUCCESS) {

                        Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                        isPerformingQuery = false;
                        coins.removeSource(repositorySource);

                    } else if (listResource.status == Resource.Status.ERROR) {

                        Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                        isPerformingQuery = false;
                        coins.removeSource(repositorySource);

                    }
                    coins.setValue(listResource);
                } else {
                    coins.removeSource(repositorySource);
                }
            } else {
                coins.removeSource(repositorySource);
            }
        });
    }

    public void cancelSearchRequest() {
        if (isPerformingQuery) {
            Log.d(TAG, "cancelSearchRequest: canceling the search request.");
            cancelRequest = true;
            isPerformingQuery = false;
        }
    }
}















