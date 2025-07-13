package ru.netology.nmedia.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.dto.Post


interface PostApiService {
    @GET("posts")
   suspend fun getAll(): List<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Post

    @POST("posts")
    suspend fun save(@Body post: Post): Unit

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Unit

    @DELETE("posts/{id}/likes")
    suspend fun deleteLikeById(@Path("id") id: Long): Post
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