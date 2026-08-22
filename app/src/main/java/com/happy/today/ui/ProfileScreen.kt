package com.happy.today.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.happy.today.model.HappyPost

@Composable
fun ProfileScreen(posts: List<HappyPost>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = "用户头像",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFFFFB800)
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("开心的小太阳", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("本地体验账号", color = Color.Gray)
                    }
                }
            }
        }
        item { Text("我的开心日记", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        if (posts.isEmpty()) item { Text("还没有记录", color = Color.Gray) }
        items(posts, key = { it.id }) { post ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "${post.category} · ${if (post.isPublic) "公开" else "仅自己可见"}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(post.content, Modifier.padding(top = 6.dp))
                }
            }
        }
        item {
            HorizontalDivider()
            Text(
                "登录、云同步与真实多人社区将在云端配置完成后启用。",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}
