package com.example.mvvmarchitecture.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvmarchitecture.Config;
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

    private boolean isLocked = false;
    private float countNumber = Config.UPDATE_ROUTINE_PERIOD / 1000;

    public CryptoCoinListViewModel(@NonNull Application application) {
        super(application);
        coinRepository = CryptoCoinRepository.getInstance(application);
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public float getCountNumber() {
        return countNumber;
    }

    public void setCountNumber(float countNumber) {
        this.countNumber = countNumber;
    }

    public LiveData<Resource<List<CryptoCoin>>> getCoins() {
        return coins;
    }

    public void getCoinsApi(String currencyUnit, String filterName) {
        if (!isPerformingQuery) {
            this.currencyUnit = currencyUnit;
            countNumber = (Config.UPDATE_ROUTINE_PERIOD / 1000);
            executeGetData(filterName, true);
        }
    }

    public void filterCoins(String filterName) {
        executeGetData(filterName, false);
    }

    private void executeGetData(String filterName, boolean shouldFetch) {
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;
        final LiveData<Resource<List<CryptoCoin>>> repositorySource = coinRepository.getCoinsApi(currencyUnit, filterName, shouldFetch);
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















