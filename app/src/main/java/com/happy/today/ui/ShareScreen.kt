package com.happy.today.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun ShareScreen() {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { if (it.length <= 100) text = it },
        label = { Text("记录今天开心的事情") }
    )
}
