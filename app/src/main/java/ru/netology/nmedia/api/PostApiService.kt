package ru.netology.nmedia.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import ru.netology.nmedia.dto.Post


interface PostApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>
}

object PostsApi {
    private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"
    private val retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()
    val service by lazy {

        retrofit.create<PostApiService>()
    }
}