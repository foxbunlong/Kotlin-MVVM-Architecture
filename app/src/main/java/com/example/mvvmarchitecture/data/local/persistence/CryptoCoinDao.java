package com.example.mvvmarchitecture.data.local.persistence;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mvvmarchitecture.data.local.models.CryptoCoin;

import java.util.List;

@Dao
public interface CryptoCoinDao {

    @Insert(onConflict = REPLACE)
    long[] insertCoins(CryptoCoin... coin);

    @Query("UPDATE coins SET buy_price = :buy_price, counter = :counter, icon = :icon, name = :name, sell_price = :sell_price  " +
            "WHERE base = :base")
    void updateCoin(String base, String buy_price, String counter, String icon, String name, String sell_price);

    @Query("SELECT * FROM coins WHERE name LIKE '%' || :filterName || '%'")
    LiveData<List<CryptoCoin>> getCoinsFilteredByName(String filterName);

}









