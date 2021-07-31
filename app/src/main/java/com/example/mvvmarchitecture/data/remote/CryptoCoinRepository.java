package com.example.mvvmarchitecture.data.remote;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.mvvmarchitecture.AppExecutors;
import com.example.mvvmarchitecture.data.local.helpers.Resource;
import com.example.mvvmarchitecture.data.local.models.CryptoCoin;
import com.example.mvvmarchitecture.data.local.persistence.CryptoCoinDao;
import com.example.mvvmarchitecture.data.local.persistence.CryptoCoinDatabase;
import com.example.mvvmarchitecture.data.remote.requests.ServiceGenerator;
import com.example.mvvmarchitecture.data.remote.responses.ApiResponse;
import com.example.mvvmarchitecture.data.remote.responses.CryptoCoinResponse;
import com.example.mvvmarchitecture.utils.NetworkBoundResource;

import java.util.List;

public class CryptoCoinRepository {

    private static final String TAG = "CryptoCoinRepository";

    private static CryptoCoinRepository instance;
    private CryptoCoinDao coinDao;

    public static CryptoCoinRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CryptoCoinRepository(context);
        }
        return instance;
    }

    private CryptoCoinRepository(Context context) {
        coinDao = CryptoCoinDatabase.getInstance(context).getCoinDao();
    }

    public LiveData<Resource<List<CryptoCoin>>> getCoinsApi(final String currencyUnit, final String filterName, final boolean shouldFetch) {
        return new NetworkBoundResource<List<CryptoCoin>, CryptoCoinResponse>(AppExecutors.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull CryptoCoinResponse item) {

                if(item.getCoins() != null){

                    CryptoCoin[] coins = new CryptoCoin[item.getCoins().size()];

                    int index = 0;
                    for(long rowid: coinDao.insertCoins((CryptoCoin[]) (item.getCoins().toArray(coins)))){
                        if(rowid == -1){
                            coinDao.updateCoin(
                                    coins[index].getBase(),
                                    coins[index].getBuy_price(),
                                    coins[index].getCounter(),
                                    coins[index].getIcon(),
                                    coins[index].getName(),
                                    coins[index].getSell_price()
                            );
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<CryptoCoin> data) {
                return shouldFetch;
            }

            @NonNull
            @Override
            protected LiveData<List<CryptoCoin>> loadFromDb() {
                return coinDao.getCoinsFilteredByName(filterName);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CryptoCoinResponse>> createCall() {
                return ServiceGenerator.getCoinApi()
                        .getCoinsData(currencyUnit);
            }
        }.getAsLiveData();
    }
}












