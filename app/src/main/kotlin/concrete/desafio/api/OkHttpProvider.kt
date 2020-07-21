package concrete.desafio.api

import concrete.desafio.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OkHttpProvider private constructor() {

    companion object {
        const val TIME_OUT: Long = 60

        private var okHttpClient: OkHttpClient? = null

        fun getInstance() : OkHttpClient {
            if(okHttpClient == null) {
                OkHttpProvider()
            }
            return okHttpClient!!
        }
    }

    init {
        val builder = OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
                })

        okHttpClient = builder.build()
    }
}