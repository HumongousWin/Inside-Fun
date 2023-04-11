package com.github.humongouswin.insidefun.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            retrofit = Retrofit.Builder()
                .baseUrl("https://vimeo.com/showcase/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }
}