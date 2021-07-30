package com.example.mvvmarchitecture.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.mvvmarchitecture.R;
import com.example.mvvmarchitecture.data.local.models.CryptoCoin;

public class CoinViewHolder extends RecyclerView.ViewHolder {

    ImageView imgIcon;
    TextView tvName;
    RequestManager requestManager;

    public CoinViewHolder(@NonNull View itemView,
                          RequestManager requestManager) {
        super(itemView);

        this.requestManager = requestManager;
        imgIcon = itemView.findViewById(R.id.imgIcon);
        tvName = itemView.findViewById(R.id.tvName);

    }

    public void onBind(CryptoCoin coin){

        requestManager
                .load(coin.getIcon())
                .into(imgIcon);

        tvName.setText(coin.getName());
    }
}
