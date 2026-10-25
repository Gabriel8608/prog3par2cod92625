package com.example.p2prog3.data.repository

import com.example.p2prog3.data.remote.jsonplaceholder.JsonPlaceholderApi
import com.example.p2prog3.data.remote.jsonplaceholder.PostDto
import com.example.p2prog3.domain.Post

class PostRepository(
    private val api: JsonPlaceholderApi
) {
    suspend fun getPosts(): Result<List<Post>> {
        return try {
            val response = api.getPosts()
            if (response.isSuccessful && response.body() != null) {
                val posts = response.body()!!.map { it.toDomain() }
                Result.success(posts)
            } else {
                Result.failure(Exception("Error al cargar publicaciones (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun PostDto.toDomain() = Post(
        id = id,
        userId = userId,
        title = title,
        body = body
    )
}
