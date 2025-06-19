package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.Exception


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(gson.fromJson(response.body?.string(), typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun getAllAsyncRetrofit(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.service.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
            override fun onResponse(
                call: retrofit2.Call<List<Post>>,
                response: retrofit2.Response<List<Post>>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                } else {
                    callback.onError(RuntimeException(response.code().toString()))
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                callback.onError(RuntimeException("Не удалось , попробуйте снова"))
            }

        })
    }


    override fun likeById(id: Long): Post {
        val request: Request = Request.Builder()
            .post(
                EMPTY_REQUEST
            )
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java)
            }

    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .post(
                EMPTY_REQUEST
            )
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun likeByIdRetrofit(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.service.likeById(id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(
                call: retrofit2.Call<Post>,
                response: retrofit2.Response<Post>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                } else {
                    callback.onError(RuntimeException(response.code().toString()))
                }
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                callback.onError(RuntimeException("Не удалось , попробуйте снова"))
            }

        })
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun saveAsync(post: Post, callback: PostRepository.Callback<Unit>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(Unit)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun saveRetrofit(post: Post, callback: PostRepository.Callback<Unit>) {
        PostsApi.service.save(post).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(
                call: retrofit2.Call<Unit>,
                response: retrofit2.Response<Unit>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.code().toString()))
                }
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback.onError(RuntimeException("Не удалось , попробуйте снова"))
            }

        })
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(Unit)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })

    }

    override fun removeByIdRetrofit(id: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.service.removeById(id).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(
                call: retrofit2.Call<Unit>,
                response: retrofit2.Response<Unit>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.code().toString()))
                } else {
                    callback.onSuccess(Unit)
                }
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback.onError(RuntimeException("Не удалось , попробуйте снова"))
            }

        })
    }

    override fun deleteLikeById(id: Long): Post {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java)
            }
    }

    override fun deleteLikeByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/${id}/likes")
            .build()
        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun deleteLikeByIdRetrofit(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.service.deleteLikeById(id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(
                call: retrofit2.Call<Post>,
                response: retrofit2.Response<Post>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                } else {
                    callback.onError(RuntimeException(response.code().toString()))
                }
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                callback.onError(RuntimeException("Не удалось , попробуйте снова"))
            }

        })
    }
}
