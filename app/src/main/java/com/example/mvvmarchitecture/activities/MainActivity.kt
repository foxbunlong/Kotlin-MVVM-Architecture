package com.example.mvvmarchitecture.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.example.mvvmarchitecture.Config
import com.example.mvvmarchitecture.R
import com.example.mvvmarchitecture.adapters.CoinRecyclerAdapter
import com.example.mvvmarchitecture.data.local.helpers.Resource
import com.example.mvvmarchitecture.data.local.models.CryptoCoin
import com.example.mvvmarchitecture.utils.AppUtils
import com.example.mvvmarchitecture.utils.TimerUtils
import com.example.mvvmarchitecture.viewmodels.CryptoCoinListViewModel
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var mViewModel: CryptoCoinListViewModel? = null

    private lateinit var rvCoins: RecyclerView
    private lateinit var lnHeader: LinearLayoutCompat
    private lateinit var lnSearch: LinearLayoutCompat
    private lateinit var cpbCounter: CircularProgressBar
    private lateinit var tvCounter: AppCompatTextView
    private lateinit var imgSearch: AppCompatImageView
    private lateinit var etSearch: AppCompatEditText
    private lateinit var btnClose: AppCompatButton
    private var mAdapter: CoinRecyclerAdapter? = null

    private var filterName = ""
    private var mJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtils.showFullScreen(this@MainActivity)
        setContentView(R.layout.activity_main)

        rvCoins = findViewById(R.id.rvCoins)
        lnHeader = findViewById(R.id.lnHeader)
        lnSearch = findViewById(R.id.lnSearch)
        cpbCounter = findViewById(R.id.cpbCounter)
        tvCounter = findViewById(R.id.tvCounter)
        imgSearch = findViewById(R.id.imgSearch)
        etSearch = findViewById(R.id.etSearch)
        btnClose = findViewById(R.id.btnClose)

        mViewModel = ViewModelProviders.of(this).get(CryptoCoinListViewModel::class.java)
        initRecyclerView()
        setupUIEvents()
        subscribeObservers()
        startRealtimeUpdate()
    }

    private fun initGlide(): RequestManager {
        val options: RequestOptions = RequestOptions()
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    private fun initRecyclerView() {
        mAdapter = CoinRecyclerAdapter(initGlide())

        rvCoins.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (rvCoins.computeVerticalScrollOffset() <= 300) {
                    lnHeader.alpha = 1f - (rvCoins.computeVerticalScrollOffset() / 300f) + 0.2f
                } else {
                    lnHeader.alpha = (0.2f)
                }

            }
        })

        rvCoins.layoutManager = LinearLayoutManager(this)
        rvCoins.adapter = mAdapter
    }

    private fun setupUIEvents() {
        lnSearch.visibility = View.GONE
        imgSearch.setOnClickListener {
            lnSearch.visibility = View.VISIBLE
            etSearch.requestFocus()
            AppUtils.showKeyboard(this, etSearch)
        }

        etSearch.addTextChangedListener { text ->
            filterName = text.toString()
            filterWithRealtimeUpdate()
        }

        btnClose.setOnClickListener {
            lnSearch.visibility = View.GONE
            AppUtils.hideKeyboard(this, etSearch)
            etSearch.setText("")
            filterName = ""
            filterWithRealtimeUpdate()
        }
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

                                    if (mJob != null && !mJob!!.isCancelled) {
                                        mJob!!.cancel()
                                    }
                                    mJob = CoroutineScope(Dispatchers.Main).launch {
                                        var countNumber =
                                            Config.UPDATE_ROUTINE_PERIOD.toFloat() / 1000

                                        TimerUtils.secondTickerFlow()
                                            .collect {
                                                // Every second
                                                if (countNumber > 0) {
                                                    cpbCounter.progressMax =
                                                        Config.UPDATE_ROUTINE_PERIOD.toFloat() / 1000
                                                    cpbCounter.progress = countNumber
                                                    tvCounter.text = "${countNumber.toInt()}"
                                                    countNumber--
                                                }
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            })
    }

    private fun startRealtimeUpdate() {
        CoroutineScope(Dispatchers.Main).launch {
            TimerUtils.tickerFlow(Config.UPDATE_ROUTINE_PERIOD, 0)
                .collect {
                    mViewModel!!.getCoinsApi("USD", filterName)
                }
        }
    }

    private fun filterWithRealtimeUpdate() {
        mViewModel!!.filterCoins(filterName)
    }

    override fun onBackPressed() {
        mViewModel!!.cancelSearchRequest()
        super.onBackPressed()
    }
}