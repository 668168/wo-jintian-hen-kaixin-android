package com.happy.today.data

import android.content.Context
import com.happy.today.model.HappyPost
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class HappyRepository(context: Context) {
    private val preferences = context.getSharedPreferences("happy_today", Context.MODE_PRIVATE)

    fun getPosts(): List<HappyPost> {
        val raw = preferences.getString(KEY_POSTS, null) ?: return samplePosts()
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index -> array.getJSONObject(index).toPost() }
        }.getOrElse { samplePosts() }
    }

    fun savePosts(posts: List<HappyPost>) {
        val array = JSONArray()
        posts.forEach { array.put(it.toJson()) }
        preferences.edit().putString(KEY_POSTS, array.toString()).apply()
    }

    fun getCheckInDates(): Set<String> =
        preferences.getStringSet(KEY_CHECK_INS, emptySet())?.toSet().orEmpty()

    fun checkInToday(): Boolean {
        val dates = getCheckInDates().toMutableSet()
        if (!dates.add(LocalDate.now().toString())) return false
        preferences.edit().putStringSet(KEY_CHECK_INS, dates).apply()
        return true
    }

    fun currentStreak(): Int {
        val dates = getCheckInDates()
        var cursor = LocalDate.now()
        var streak = 0
        while (cursor.toString() in dates) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    fun totalRewardYuan(): Int = getCheckInDates().size

    private fun samplePosts() = listOf(
        HappyPost(
            id = 1,
            content = "今天完成了一个小目标，给认真生活的自己点个赞！",
            category = "工作",
            likes = 12,
            comments = listOf("太棒了！", "继续加油 😊")
        ),
        HappyPost(
            id = 2,
            content = "晚饭后和家人一起散步，晚风很舒服。",
            category = "家庭",
            likes = 8
        )
    )

    private fun HappyPost.toJson() = JSONObject().apply {
        put("id", id)
        put("content", content)
        put("category", category)
        put("likes", likes)
        put("comments", JSONArray(comments))
        put("isPublic", isPublic)
        put("mediaUri", mediaUri)
        put("mediaType", mediaType)
        put("createdAt", createdAt)
    }

    private fun JSONObject.toPost(): HappyPost {
        val commentsJson = optJSONArray("comments") ?: JSONArray()
        return HappyPost(
            id = optLong("id"),
            content = optString("content"),
            category = optString("category", "生活"),
            likes = optInt("likes"),
            comments = List(commentsJson.length()) { commentsJson.optString(it) },
            isPublic = optBoolean("isPublic", true),
            mediaUri = optString("mediaUri").takeIf { it.isNotBlank() && it != "null" },
            mediaType = optString("mediaType").takeIf { it.isNotBlank() && it != "null" },
            createdAt = optLong("createdAt", System.currentTimeMillis())
        )
    }

    private companion object {
        const val KEY_POSTS = "posts"
        const val KEY_CHECK_INS = "check_ins"
    }
}
