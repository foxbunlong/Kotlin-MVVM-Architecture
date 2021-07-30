package com.example.mvvmarchitecture.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.example.mvvmarchitecture.R;
import com.example.mvvmarchitecture.data.local.models.CryptoCoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoinRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ListPreloader.PreloadModelProvider<String>
{

    private List<CryptoCoin> mCoins;
    private RequestManager requestManager;

    public CoinRecyclerAdapter(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_coin_list_item, viewGroup, false);
        return new CoinViewHolder(view, requestManager);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((CoinViewHolder)viewHolder).onBind(mCoins.get(i));
    }

    private void clearList(){
        if(mCoins == null){
            mCoins = new ArrayList<>();
        }
        else{
            mCoins.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mCoins != null){
            return mCoins.size();
        }
        return 0;
    }

    public void setCoins(List<CryptoCoin> coins){
        mCoins = coins;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        String url = mCoins.get(position).getIcon();
        if(TextUtils.isEmpty(url)){
            return Collections.emptyList();
        }
        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return requestManager.load(item);
    }
}















