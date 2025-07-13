package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAllAsyncRetrofit()
    suspend fun likeByIdRetrofit(id: Long)
    suspend fun saveRetrofit(post: Post)
    suspend fun removeByIdRetrofit(id: Long)
    suspend fun deleteLikeByIdRetrofit(id: Long)

}
