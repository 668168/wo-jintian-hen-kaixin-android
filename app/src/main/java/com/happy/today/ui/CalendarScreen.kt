package com.happy.today.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    recordsByDate: Map<String, List<String>> = emptyMap(),
    onAdd: () -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now().toString()) }
    val month = YearMonth.now()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("今日概览", fontSize = 24.sp)
            Button(onClick = onAdd) { Text("+") }
        }

        Spacer(Modifier.height(16.dp))
        Text("${month.year}年${month.monthValue}月")

        Column {
            (1..month.lengthOfMonth()).chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        val date = month.atDay(day).toString()
                        val records = recordsByDate[date] ?: emptyList()
                        Box(
                            Modifier.weight(1f).height(55.dp)
                                .clickable { selectedDate = date }
                                .padding(4.dp)
                                .background(Color(0xFFFFF3B0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(day.toString())
                                if (records.isNotEmpty()) {
                                    Text("✓", color = Color.Yellow)
                                    Text(
                                        "●".repeat(records.size.coerceAtMost(5)),
                                        color = Color.Red,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("${selectedDate} 的开心记录", fontSize = 18.sp)
        recordsByDate[selectedDate]?.forEach {
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(it, Modifier.padding(12.dp))
            }
        }
    }
}
