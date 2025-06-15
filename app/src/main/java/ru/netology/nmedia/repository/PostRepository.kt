package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post
import java.lang.Exception

interface PostRepository {
    fun getAll(): List<Post>
    fun getAllAsync(callback: Callback<List<Post>>)
    fun getAllAsyncRetrofit(callback: Callback<List<Post>>)
    fun likeById(id: Long): Post
    fun likeByIdAsync(id: Long, callback: Callback<Post>)

    fun save(post: Post)
    fun saveAsync(post: Post, callback: Callback<Unit>)
    fun removeById(id: Long)
    fun removeByIdAsync(id: Long, callback: Callback<Unit>)
    fun deleteLikeById(id: Long): Post
    fun deleteLikeByIdAsync(id: Long, callback: Callback<Post>)


    interface Callback<T> {
        fun onSuccess(data: T)
        fun onError(exception: Exception)
    }
}
