package com.example.p2prog3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2prog3.data.remote.RetrofitInstances
import com.example.p2prog3.data.repository.PostRepository
import com.example.p2prog3.domain.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PostsUiState {
    object Loading : PostsUiState
    data class Success(val posts: List<Post>) : PostsUiState
    data class Error(val message: String) : PostsUiState
}

class PostsViewModel(
    private val repository: PostRepository = PostRepository(RetrofitInstances.jsonPlaceholderApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        _uiState.value = PostsUiState.Loading
        viewModelScope.launch {
            repository.getPosts().fold(
                onSuccess = { posts ->
                    _uiState.value = PostsUiState.Success(posts)
                },
                onFailure = { error ->
                    _uiState.value = PostsUiState.Error(
                        error.message ?: "No se pudieron obtener las publicaciones"
                    )
                }
            )
        }
    }

    fun selectPost(post: Post?) {
        _selectedPost.value = post
    }

    fun reintentar() {
        loadPosts()
    }
}
