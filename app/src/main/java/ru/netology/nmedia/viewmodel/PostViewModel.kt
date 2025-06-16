package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.lang.Exception

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsyncRetrofit(
            object : PostRepository.Callback<List<Post>> {
                override fun onSuccess(data: List<Post>) {
                    _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
                }

                override fun onError(exception: Exception) {
                    _data.postValue(
                        FeedModel(
                            error = true,
                            errorText = exception.message.toString()
                        )
                    )
                }

            }

        )
    }

    fun save() {
        edited.value?.let {
            _data.value = FeedModel(loading = true)
            repository.saveRetrofit(it, object : PostRepository.Callback<Unit> {
                override fun onSuccess(data: Unit) {
                    _postCreated.postValue(Unit)

                }

                override fun onError(exception: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })

        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        // <--- Сохраняем результат if-else
        if ((_data.value?.posts.orEmpty().first { it.id == id }).likedByMe) {
            repository.deleteLikeByIdRetrofit(id, object : PostRepository.Callback<Post> {

                override fun onSuccess(data: Post) {
                    _data.postValue( // <--- Обновляем LiveData
                        _data.value?.copy(
                            posts = _data.value?.posts.orEmpty()
                                .map { if (it.id == data.id) data else it } // Обновляем пост в списке
                        )
                    )
                }

                override fun onError(exception: Exception) {
                    FeedModel(error = true)
                }

            })
        } else {

            // <--- Добавили else
            repository.likeByIdRetrofit(id, object : PostRepository.Callback<Post> {
                override fun onSuccess(data: Post) {
                    _data.postValue( // <--- Обновляем LiveData
                        _data.value?.copy(
                            posts = _data.value?.posts.orEmpty()
                                .map { if (it.id == data.id) data else it } // Обновляем пост в списке
                        )
                    )
                }

                override fun onError(exception: Exception) {
                    FeedModel(error = true)
                }

            })
        }

    }

    fun removeById(id: Long) {
        repository.removeByIdRetrofit(id, object : PostRepository.Callback<Unit> {
            val old = _data.value?.posts.orEmpty()
            override fun onSuccess(data: Unit) {
                _data.postValue(
                    _data.value?.copy(
                        posts = _data.value?.posts.orEmpty()
                            .filter { it.id != id }
                    )
                )
            }

            override fun onError(exception: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }

        })
    }
}
