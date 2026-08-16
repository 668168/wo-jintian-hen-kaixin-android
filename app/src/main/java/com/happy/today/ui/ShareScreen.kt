package com.happy.today.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
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
                Icon(Icons.Default.CameraAlt, contentDescription = "图片")
            }
            IconButton(onClick = { videoSelected = true }) {
                Icon(Icons.Default.Videocam, contentDescription = "视频")
            }
            IconButton(onClick = { voiceRecorded = true }) {
                Icon(Icons.Default.Mic, contentDescription = "语音")
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
