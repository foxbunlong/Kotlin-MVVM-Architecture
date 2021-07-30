package com.example.mvvmarchitecture.data.remote.responses;

import androidx.annotation.Nullable;

import com.example.mvvmarchitecture.data.local.models.CryptoCoin;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CryptoCoinResponse {

    @SerializedName("data")
    @Expose()
    private List<CryptoCoin> data;

    @Nullable
    public List<CryptoCoin> getCoins() {
        return data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "coins=" + data +
                '}';
    }
}
