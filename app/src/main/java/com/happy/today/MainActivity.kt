package com.happy.today

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.ImageView
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum as ForumFilled
import androidx.compose.material.icons.filled.Home as HomeFilled
import androidx.compose.material.icons.filled.Person as PersonFilled
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Forum as ForumOutlined
import androidx.compose.material.icons.outlined.Home as HomeOutlined
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Person as PersonOutlined
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.happy.today.data.HappyRepository
import com.happy.today.model.HappyPost
import com.happy.today.ui.MainNavigation
import com.happy.today.ui.ProfileScreen
import com.happy.today.ui.theme.HappyTodayTheme
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainNavigation() }
    }
}

private val Sunshine = Color(0xFFFFB800)
private val WarmBackground = Color(0xFFFFF9EC)
private val Coral = Color(0xFFFF5C73)

private enum class AppTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("首页", HomeFilled, HomeOutlined),
    PLAZA("开心广场", ForumFilled, ForumOutlined),
    PROFILE("我的", PersonFilled, PersonOutlined)
}

private class HappyAppState(private val repository: HappyRepository) {
    var posts by mutableStateOf(repository.getPosts())
        private set
    var checkInDates by mutableStateOf(repository.getCheckInDates())
        private set

    fun publish(content: String, category: String, isPublic: Boolean, mediaUri: String?, mediaType: String?) {
        val post = HappyPost(
            id = System.currentTimeMillis(), content = content.trim().take(100), category = category,
            isPublic = isPublic, mediaUri = mediaUri, mediaType = mediaType
        )
        posts = listOf(post) + posts
        repository.savePosts(posts)
    }

    fun like(postId: Long) = update(postId) { it.copy(likes = it.likes + 1) }

    fun comment(postId: Long, comment: String) {
        if (comment.isBlank()) return
        update(postId) { it.copy(comments = it.comments + comment.trim().take(100)) }
    }

    fun checkIn(): Boolean {
        val created = repository.checkInToday()
        checkInDates = repository.getCheckInDates()
        return created
    }

    fun streak() = repository.currentStreak()
    fun reward() = repository.totalRewardYuan()

    private fun update(postId: Long, transform: (HappyPost) -> HappyPost) {
        posts = posts.map { if (it.id == postId) transform(it) else it }
        repository.savePosts(posts)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HappyTodayApp() {
    val context = LocalContext.current
    val state = remember { HappyAppState(HappyRepository(context.applicationContext)) }
    var selectedTab by remember { mutableStateOf(AppTab.HOME) }
    var showComposer by remember { mutableStateOf(false) }

    HappyTodayTheme {
        Scaffold(
            containerColor = WarmBackground,
            topBar = {
                TopAppBar(
                    title = { Text("我今天很开心", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBackground)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    AppTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == tab) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(tab.label, fontSize = 10.sp) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (selectedTab) {
                    AppTab.HOME -> HomeContent(state, onCompose = { showComposer = true })
                    AppTab.PLAZA -> PlazaContent(state)
                    AppTab.PROFILE -> ProfileScreen(state.posts)
                }
            }
        }

        if (showComposer) {
            ComposerDialog(
                onDismiss = { showComposer = false },
                onPublish = { text, category, isPublic, mediaUri, mediaType ->
                    state.publish(text, category, isPublic, mediaUri, mediaType)
                    showComposer = false
                    selectedTab = if (isPublic) AppTab.PLAZA else AppTab.PROFILE
                }
            )
        }
    }
}

@Composable
private fun HomeContent(state: HappyAppState, onCompose: () -> Unit) {
    val quotes = listOf(
        "笑一笑，生活更美好。", "关注当下，就是送给自己最好的礼物。",
        "微小的快乐，也值得被认真记录。", "愿你今天对自己温柔一点。"
    )
    val quote = quotes[LocalDate.now().dayOfYear % quotes.size]
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE39A)), shape = RoundedCornerShape(28.dp)) {
                Column(
                    Modifier.fillMaxWidth().padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WbSunny,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = Sunshine
                    )
                    Text(quote, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = onCompose) { Text("记录今天的开心事") }
                }
            }
        }
        item { CalendarPanel(posts = state.posts, onCompose = onCompose) }
        item { SectionTitle("今日概览") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("${state.posts.size}", "开心日记", Modifier.weight(1f))
                StatCard("${state.streak()} 天", "连续打卡", Modifier.weight(1f))
                StatCard("¥${state.reward()}", "演示奖励", Modifier.weight(1f))
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Text(
                    "把工作、生活、家庭或其他小确幸记录下来。默认公开，可在发布时改为仅自己可见。",
                    Modifier.padding(18.dp), lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
private fun PlazaContent(state: HappyAppState) {
    val publicPosts = state.posts.filter { it.isPublic }
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("看看大家今天的开心事", color = Color.Gray) }
        if (publicPosts.isEmpty()) item { EmptyMessage("还没有公开动态") }
        items(publicPosts, key = { it.id }) { post -> PostCard(post, state::like, state::comment) }
    }
}

@Composable
private fun PostCard(post: HappyPost, onLike: (Long) -> Unit, onComment: (Long, String) -> Unit) {
    var comment by remember(post.id) { mutableStateOf("") }
    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(38.dp).background(Sunshine, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.SentimentSatisfied,
                        contentDescription = "用户头像",
                        modifier = Modifier.size(25.dp),
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("开心用户", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text(formatPostTime(post.createdAt), color = Color.Gray, fontSize = 11.sp)
                    }
                    Text(post.category, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Text(post.content, fontSize = 17.sp, lineHeight = 25.sp)
            MediaPreview(post.mediaUri, post.mediaType)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { onLike(post.id) }) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "点赞", Modifier.size(18.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(post.likes.toString())
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "评论数",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(post.comments.size.toString(), color = Color.Gray)
                }
            }
            post.comments.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.SentimentSatisfied,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Sunshine
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(it, fontSize = 14.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = comment, onValueChange = { comment = it.take(100) },
                    modifier = Modifier.weight(1f), placeholder = { Text("友善评论…") }, singleLine = true
                )
                TextButton(onClick = { onComment(post.id, comment); comment = "" }) { Text("发布") }
            }
        }
    }
}

@Composable
private fun CalendarPanel(posts: List<HappyPost>, onCompose: () -> Unit) {
    val month = YearMonth.now()
    val firstOffset = month.atDay(1).dayOfWeek.value - 1
    val cells = List(firstOffset) { 0 } + (1..month.lengthOfMonth()).toList()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val postsByDate = posts.groupBy { postDate(it.createdAt) }
    val selectedPosts = postsByDate[selectedDate].orEmpty()

    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("开心日历", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text(month.format(DateTimeFormatter.ofPattern("yyyy 年 M 月")), color = Color.Gray)
                IconButton(onClick = onCompose) {
                    Icon(Icons.Rounded.AddCircle, contentDescription = "添加开心记录", tint = Coral)
                }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("一", "二", "三", "四", "五", "六", "日").forEach {
                    Text(it, Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.Gray)
                }
            }
            cells.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        if (day == 0) {
                            Spacer(Modifier.weight(1f).height(58.dp))
                        } else {
                            val date = month.atDay(day)
                            val count = postsByDate[date].orEmpty().size
                            val isToday = date == LocalDate.now()
                            val isSelected = date == selectedDate
                            Column(
                                Modifier.weight(1f).height(58.dp).padding(2.dp)
                                    .border(if (isSelected) 1.dp else 0.dp, Coral, CircleShape)
                                    .clickable { selectedDate = date },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(day.toString(), fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
                                    if (isToday && count > 0) {
                                        Icon(
                                            Icons.Rounded.CheckCircle,
                                            contentDescription = "今日已记录",
                                            modifier = Modifier.size(14.dp),
                                            tint = Sunshine
                                        )
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(count.coerceAtMost(3)) {
                                        Box(Modifier.size(5.dp).background(Coral, CircleShape))
                                    }
                                }
                            }
                        }
                    }
                    repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            Text(selectedDate.format(DateTimeFormatter.ofPattern("M 月 d 日的开心记录")), fontWeight = FontWeight.Bold)
            if (selectedPosts.isEmpty()) {
                Text("这一天还没有记录", color = Color.Gray)
            } else {
                selectedPosts.forEach { post ->
                    Column(Modifier.fillMaxWidth().background(WarmBackground, RoundedCornerShape(10.dp)).padding(10.dp)) {
                        Text(post.content)
                        MediaPreview(post.mediaUri, post.mediaType, compact = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun ComposerDialog(
    onDismiss: () -> Unit,
    onPublish: (String, String, Boolean, String?, String?) -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("生活") }
    var isPublic by remember { mutableStateOf(true) }
    var mediaUri by remember { mutableStateOf<String?>(null) }
    var mediaType by remember { mutableStateOf<String?>(null) }

    fun persist(uri: Uri?, type: String) {
        uri ?: return
        runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        mediaUri = uri.toString(); mediaType = type
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { persist(it, "image") }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { persist(it, "video") }
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            text = (text + spoken).take(100)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录今天开心的事") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = text, onValueChange = { text = it.take(100) }, modifier = Modifier.fillMaxWidth(),
                    minLines = 3, label = { Text("开心事（最多 100 字）") }, supportingText = { Text("${text.length}/100") }
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    OutlinedButton(onClick = { imagePicker.launch(arrayOf("image/*")) }) {
                        Icon(Icons.Outlined.Image, contentDescription = null, Modifier.size(18.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("图片")
                    }
                    OutlinedButton(onClick = { videoPicker.launch(arrayOf("video/*")) }) {
                        Icon(Icons.Outlined.Videocam, contentDescription = null, Modifier.size(18.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("视频")
                    }
                    OutlinedButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                        }
                        runCatching { speechLauncher.launch(intent) }
                    }) {
                        Icon(Icons.Outlined.Mic, contentDescription = null, Modifier.size(18.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("语音")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("工作", "生活", "家庭", "其他").forEach { item ->
                        FilterChip(selected = category == item, onClick = { category = item }, label = { Text(item) })
                    }
                }
                mediaType?.let { MediaPreview(mediaUri, it, compact = true) }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text("公开发布"); Text("其他用户可见", fontSize = 12.sp, color = Color.Gray) }
                    Switch(checked = isPublic, onCheckedChange = { isPublic = it })
                }
            }
        },
        confirmButton = { Button(onClick = { onPublish(text, category, isPublic, mediaUri, mediaType) }, enabled = text.isNotBlank()) { Text("发布") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun SectionTitle(text: String) = Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold)

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, color = Coral)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun EmptyMessage(text: String) = Box(
    Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center
) { Text(text, color = Color.Gray) }

@Composable
private fun MediaPreview(mediaUri: String?, mediaType: String?, compact: Boolean = false) {
    val uri = mediaUri?.let(Uri::parse) ?: return
    val previewHeight = if (compact) 120.dp else 220.dp
    when (mediaType) {
        "image" -> AndroidView(
            modifier = Modifier.fillMaxWidth().height(previewHeight).background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp)),
            factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    adjustViewBounds = true
                    setImageURI(uri)
                    tag = mediaUri
                }
            },
            update = { view ->
                if (view.tag != mediaUri) {
                    view.setImageURI(uri)
                    view.tag = mediaUri
                }
            }
        )
        "video" -> AndroidView(
            modifier = Modifier.fillMaxWidth().height(previewHeight).background(Color.Black, RoundedCornerShape(12.dp)),
            factory = { context ->
                VideoView(context).apply {
                    setVideoURI(uri)
                    setOnPreparedListener { player -> player.isLooping = true; seekTo(100) }
                    setOnClickListener { if (isPlaying) pause() else start() }
                    tag = mediaUri
                }
            },
            update = { view ->
                if (view.tag != mediaUri) {
                    view.setVideoURI(uri)
                    view.seekTo(100)
                    view.tag = mediaUri
                }
            }
        )
    }
}

private fun postDate(createdAt: Long): LocalDate =
    Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalDate()

private fun formatPostTime(createdAt: Long): String =
    Instant.ofEpochMilli(createdAt)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("M月d日 HH:mm"))
