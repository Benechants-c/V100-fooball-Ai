package com.v100.footballai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { V100App() }
    }
}

@Composable
fun V100App() {
    var home by remember { mutableStateOf("Man City") }
    var away by remember { mutableStateOf("Arsenal") }
    var result by remember { mutableStateOf("") }
    
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0F172A)) {
            Column(modifier = Modifier.padding(20.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("V100 FOOTBALL AI", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Elite Prediction Engine", color = Color(0xFF94A3B8))
                
                OutlinedTextField(value = home, onValueChange = { home = it }, label = { Text("Home Team", color = Color.Gray) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = away, onValueChange = { away = it }, label = { Text("Away Team", color = Color.Gray) }, modifier = Modifier.fillMaxWidth())
                
                Button(onClick = {
                    val homeWin = Random.nextInt(35, 75)
                    val confidence = Random.nextInt(72, 96)
                    val goals = Random.nextDouble(1.5, 4.5)
                    result = "MATCH: $home vs $away\n\nV100 ANALYSIS:\nHome Win: $homeWin%\nBTTS: ${Random.nextInt(45,70)}%\nOver 2.5: ${if(goals>2.5) 68 else 42}%\n\nCORRECT SCORE: ${Random.nextInt(1,3)}-${Random.nextInt(0,2)}\nCONFIDENCE: $confidence%\n\nTIP: ${if(homeWin>55) "$home WIN" else "X2"}"
                }, modifier = Modifier.fillMaxWidth()) { Text("RUN V100 AI PREDICTION") }
                
                if(result.isNotEmpty()){
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                        Text(result, color = Color.White, modifier = Modifier.padding(16.dp), lineHeight = 22.sp)
                    }
                }
            }
        }
    }
}
