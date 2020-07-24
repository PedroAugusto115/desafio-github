package github.desafio.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class CallbackRequest<T> : Callback<T> {

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        if(response != null && response.isSuccessful) {
            success(response)
            return
        }
        failureHttp(response)
    }

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        failure(t!!)
    }

    protected abstract fun success(response: Response<T>)

    protected abstract fun failureHttp(response: Response<T>?)

    protected abstract fun failure(throwable: Throwable)
}