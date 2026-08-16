package com.happy.today.model

data class HappyPost(
    val id: Long,
    val content: String,
    val category: String = "生活",
    val likes: Int = 0,
    val comments: List<String> = emptyList(),
    val isPublic: Boolean = true,
    val mediaUri: String? = null,
    val mediaType: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
