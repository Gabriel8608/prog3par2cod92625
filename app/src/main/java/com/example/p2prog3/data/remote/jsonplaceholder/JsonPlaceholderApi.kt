package com.example.p2prog3.data.remote.jsonplaceholder

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface JsonPlaceholderApi {
    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Response<PostDto>
}
