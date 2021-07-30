package com.example.mvvmarchitecture.data.local.models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coins")
class CryptoCoin() : Parcelable {

    @PrimaryKey
    @NonNull
    var base: String = ""

    @ColumnInfo(name = "counter")
    var counter: String? = ""

    @ColumnInfo(name = "buy_price")
    var buy_price: String? = ""

    @ColumnInfo(name = "sell_price")
    var sell_price: String? = ""

    @ColumnInfo(name = "icon")
    var icon: String? = ""

    @ColumnInfo(name = "name")
    var name: String? = ""

    constructor(parcel: Parcel) : this() {
        base = parcel.readString()!!
        counter = parcel.readString()
        buy_price = parcel.readString()
        sell_price = parcel.readString()
        icon = parcel.readString()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(base)
        parcel.writeString(counter)
        parcel.writeString(buy_price)
        parcel.writeString(sell_price)
        parcel.writeString(icon)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CryptoCoin> {
        override fun createFromParcel(parcel: Parcel): CryptoCoin {
            return CryptoCoin(parcel)
        }

        override fun newArray(size: Int): Array<CryptoCoin?> {
            return arrayOfNulls(size)
        }
    }

}