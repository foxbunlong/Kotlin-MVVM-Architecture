package com.example.mvvmarchitecture.data.remote.requests;

import static com.example.mvvmarchitecture.Constants.CONNECTION_TIMEOUT;
import static com.example.mvvmarchitecture.Constants.READ_TIMEOUT;
import static com.example.mvvmarchitecture.Constants.WRITE_TIMEOUT;
import static okhttp3.logging.HttpLoggingInterceptor.Level;

import com.example.mvvmarchitecture.Constants;
import com.example.mvvmarchitecture.data.remote.CryptoCoinApi;
import com.example.mvvmarchitecture.utils.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(Level.BODY);

    private static OkHttpClient client = new OkHttpClient.Builder()

            .addInterceptor(interceptor)

            // establish connection to server
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte read from the server
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte sent to server
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            .retryOnConnectionFailure(false)

            .build();

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static CryptoCoinApi coinApi = retrofit.create(CryptoCoinApi.class);

    public static CryptoCoinApi getCoinApi(){
        return coinApi;
    }
}
