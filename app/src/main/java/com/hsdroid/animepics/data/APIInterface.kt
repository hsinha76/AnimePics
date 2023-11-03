package com.hsdroid.animepics.data

import com.hsdroid.animepics.models.Images
import retrofit2.http.GET
import retrofit2.http.Path

interface APIInterface {

    @GET("sfw/{tag}")
    suspend fun getData(@Path("tag") tags : String) : Images
}