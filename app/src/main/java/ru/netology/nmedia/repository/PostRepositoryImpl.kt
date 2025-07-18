package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.service.ApiError


class PostRepositoryImpl
    (private val dao: PostDao) : PostRepository {

    override val data: LiveData<List<Post>> = dao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAllAsyncRetrofit() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.insert(response.body()!!.map { PostEntity.fromDto(it) })

        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun likeByIdRetrofit(id: Long) {

        try {
            val response = PostsApi.service.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.likeById(id)

        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun saveRetrofit(post: Post) {

        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))

        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun removeByIdRetrofit(id: Long) {
        try {
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun deleteLikeByIdRetrofit(id: Long) {
        dao.likeById(id)
        try {
            val response = PostsApi.service.deleteLikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
                dao.likeById(id)
            }

        } catch (ex: Exception) {
            throw ex
        }
    }

}
