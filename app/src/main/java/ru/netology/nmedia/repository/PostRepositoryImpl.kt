package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity


class PostRepositoryImpl
    (private val dao: PostDao) : PostRepository {

    override val data: LiveData<List<Post>> = dao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAllAsyncRetrofit() {
        val posts = PostsApi.service.getAll()
        dao.insert(posts.map { PostEntity.fromDto(it) })
    }

    override suspend fun likeByIdRetrofit(id: Long) {
        dao.likeById(id)
        PostsApi.service.likeById(id)
    }

    override suspend fun saveRetrofit(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override suspend fun removeByIdRetrofit(id: Long) {
        PostsApi.service.removeById(id)
        dao.removeById(id)
    }

    override suspend fun deleteLikeByIdRetrofit(id: Long) {
        dao.likeById(id)
    }

}
