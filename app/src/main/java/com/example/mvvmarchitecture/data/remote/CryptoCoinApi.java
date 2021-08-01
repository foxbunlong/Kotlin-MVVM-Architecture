package com.example.mvvmarchitecture.data.remote;

import androidx.lifecycle.LiveData;

import com.example.mvvmarchitecture.data.remote.responses.ApiResponse;
import com.example.mvvmarchitecture.data.remote.responses.CryptoCoinResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CryptoCoinApi {

    // SEARCH
    @GET("price/all_prices_for_mobile")
    LiveData<ApiResponse<CryptoCoinResponse>> getCoinsData(
            @Query("counter_currency") String currencyUnit
    );
}
