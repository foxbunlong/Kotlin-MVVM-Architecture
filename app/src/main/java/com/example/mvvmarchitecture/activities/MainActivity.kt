package com.example.mvvmarchitecture.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.example.mvvmarchitecture.R
import com.example.mvvmarchitecture.adapters.CoinRecyclerAdapter
import com.example.mvvmarchitecture.data.local.helpers.Resource
import com.example.mvvmarchitecture.data.local.models.CryptoCoin
import com.example.mvvmarchitecture.viewmodels.CryptoCoinListViewModel

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var mViewModel: CryptoCoinListViewModel? = null

    private lateinit var rvCoins: RecyclerView
    private var mAdapter: CoinRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvCoins = findViewById(R.id.rvCoins)

        mViewModel = ViewModelProviders.of(this).get(CryptoCoinListViewModel::class.java)
        initRecyclerView()
        subscribeObservers()
        mViewModel!!.getCoinsApi("USD")
    }

    private fun initGlide(): RequestManager {
        val options: RequestOptions = RequestOptions()
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    private fun initRecyclerView() {
        mAdapter = CoinRecyclerAdapter(initGlide())
        rvCoins.layoutManager = LinearLayoutManager(this)
        rvCoins.adapter = mAdapter
    }

    private fun subscribeObservers() {
        mViewModel!!.coins
            .observe(this, object : Observer<Resource<List<CryptoCoin>>> {
                override fun onChanged(@Nullable listResource: Resource<List<CryptoCoin>>) {
                    if (listResource != null) {
                        Log.d(TAG, "onChanged: status: " + listResource.status)
                        if (listResource.data != null) {
                            when (listResource.status) {
                                Resource.Status.LOADING -> {
                                    print("Loading");
                                }
                                Resource.Status.ERROR -> {
                                    Log.e(TAG, "onChanged: cannot refresh the cache.")
                                    Log.e(TAG, "onChanged: ERROR message: " + listResource.message)
                                    Log.e(
                                        TAG,
                                        "onChanged: status: ERROR, #coins: " + listResource.data.size
                                    )
                                    mAdapter!!.setCoins(listResource.data)
                                    Toast.makeText(
                                        this@MainActivity,
                                        listResource.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                Resource.Status.SUCCESS -> {
                                    Log.d(TAG, "onChanged: cache has been refreshed.")
                                    Log.d(
                                        TAG,
                                        "onChanged: status: SUCCESS, #coins: " + listResource.data.size
                                    )
                                    mAdapter!!.setCoins(listResource.data)
                                }
                            }
                        }
                    }
                }
            })
    }

    override fun onBackPressed() {
        mViewModel!!.cancelSearchRequest()
        super.onBackPressed()
    }
}