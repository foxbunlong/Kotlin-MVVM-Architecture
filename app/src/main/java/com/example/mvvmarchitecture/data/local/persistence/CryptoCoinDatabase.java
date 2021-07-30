package com.example.mvvmarchitecture.data.local.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mvvmarchitecture.data.local.models.CryptoCoin;

@Database(entities = {CryptoCoin.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class CryptoCoinDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "coins_db";

    private static CryptoCoinDatabase instance;

    public static CryptoCoinDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    CryptoCoinDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract CryptoCoinDao getCoinDao();

}






