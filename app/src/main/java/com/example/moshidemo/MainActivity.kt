package com.example.moshidemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        val moshiBuilder = Moshi.Builder().add(MyEnumAdapter())
        val moshi = moshiBuilder.build()

        val clientBuilder = OkHttpClient.Builder().apply {
            addInterceptor(MyFakeInterceptor())
        }
        val client = clientBuilder.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        val service: ApiService = retrofit.create<ApiService>(ApiService::class.java)
        service.getInfoObjects()
            .subscribeOn(Schedulers.io())
            .subscribe({ infoObjects ->
                Log.d("OBJECTS", infoObjects.toString())
            }, { error ->
                Log.e("APIERROR", error.localizedMessage)
            })
    }
}