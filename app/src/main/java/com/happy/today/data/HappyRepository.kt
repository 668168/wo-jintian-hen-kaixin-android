package com.happy.today.data

import com.happy.today.model.HappyPost

class HappyRepository {
    private val posts = mutableListOf<HappyPost>()

    fun getPosts(): List<HappyPost> = posts

    fun addPost(content: String) {
        posts.add(
            HappyPost(
                id = System.currentTimeMillis(),
                content = content.take(100)
            )
        )
    }
}
