package com.ssafy.smile

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.ssafy.smile.common.util.SharedPreferencesUtil
import com.ssafy.smile.data.*

class Application : Application()  {

    companion object{
        lateinit var sharedPreferences: SharedPreferencesUtil
        var authToken : String? = null
        var authTime : Long? = null
        var fcmToken : String? = null
        var role : String? = null

        private val okHttpInstances = OkhttpClientInstances
        private val retrofitInstances = RetrofitInstances(okHttpInstances)
        private val serviceInstances = ServiceInstances(retrofitInstances)
        private val dataSourceInstances = DataSourceInstances(serviceInstances)
        val repositoryInstances = RepositoryInstances(dataSourceInstances)
    }

    override fun onCreate() {
        super.onCreate()
        initSharedPreference()
        kakaoInit()
    }

    private fun initSharedPreference(){
        sharedPreferences = SharedPreferencesUtil(this)
        authToken = sharedPreferences.getAuthToken()
        fcmToken = sharedPreferences.getFCMToken()
    }

    private fun kakaoInit() {
        KakaoSdk.init(this, getString(R.string.kakao_native_key))
    }

}