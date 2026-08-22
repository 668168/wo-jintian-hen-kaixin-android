package com.happy.today.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShareScreen() {
    var text by remember { mutableStateOf("") }
    var imageSelected by remember { mutableStateOf(false) }
    var videoSelected by remember { mutableStateOf(false) }
    var voiceRecorded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = text,
            onValueChange = { if (it.length <= 100) text = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("记录今天开心的事情") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            IconButton(onClick = { imageSelected = true }) {
                Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = "选择图片")
            }
            IconButton(onClick = { videoSelected = true }) {
                Icon(Icons.Outlined.Videocam, contentDescription = "选择视频")
            }
            IconButton(onClick = { voiceRecorded = true }) {
                Icon(Icons.Outlined.Mic, contentDescription = "语音输入")
            }
        }

        if (imageSelected) {
            Text("图片预览区域")
        }
        if (videoSelected) {
            Text("视频预览区域")
        }
        if (voiceRecorded) {
            Text("语音已录入")
        }
    }
}
