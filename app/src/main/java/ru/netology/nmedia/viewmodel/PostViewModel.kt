package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

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
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())
    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> = repository.data.map { FeedModel(it, it.isEmpty()) }
     fun get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _dataState.value = FeedModelState(loading = true)
            try {
                repository.getAllAsyncRetrofit()
                _dataState.value = FeedModelState()

            } catch (ex: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun save() {
        edited.value?.let {
            _dataState.value = FeedModelState(loading = true)
            viewModelScope.launch {
                repository.saveRetrofit(it)
                _postCreated.value = Unit
            }
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
        viewModelScope.launch {
            try {
                repository.likeByIdRetrofit(id)
                val currentFeed = _data.value?.posts
                val updatedPosts = currentFeed!!.map { post ->
                    if (post.id == id) post.copy(likedByMe = !post.likedByMe) else post
                }
                _data.postValue(_data.value!!.copy(posts = updatedPosts))
                _dataState.value = FeedModelState()

            } catch (ex: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeByIdRetrofit(id)
                _data.postValue(
                    _data.value?.copy(
                        posts = _data.value?.posts.orEmpty()
                            .filter { it.id != id }
                    )
                )
                _dataState.value = FeedModelState()

            } catch (ex: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}
