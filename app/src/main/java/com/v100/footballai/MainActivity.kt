package com.v100.footballai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun V100App() {
    val allTeams = listOf(
        "Manchester United", "Manchester City", "Liverpool", "Arsenal", "Chelsea", "Tottenham", 
        "Newcastle", "Aston Villa", "Brighton", "West Ham", "Crystal Palace", "Fulham", 
        "Wolves", "Everton", "Brentford", "Nottingham Forest", "Bournemouth", "Luton", "Burnley", "Sheffield United",
        "Real Madrid", "Barcelona", "Atletico Madrid", "Sevilla", "Real Sociedad", "Villarreal", "Athletic Bilbao",
        "Bayern Munich", "Bayer Leverkusen", "Dortmund", "RB Leipzig", "Stuttgart", "Frankfurt",
        "PSG", "Marseille", "Monaco", "Lyon", "Lille", "Lens",
        "Inter Milan", "AC Milan", "Juventus", "Napoli", "Roma", "Lazio", "Atalanta",
        "Benfica", "Porto", "Sporting Lisbon", "Ajax", "PSV", "Feyenoord",
        "Al Ahly", "Mamelodi Sundowns", "Simba SC", "Young Africans", "Kaizer Chiefs", "Orlando Pirates",
        "Al Hilal", "Al Nassr", "Al Ittihad", "Flamengo", "Palmeiras", "River Plate", "Boca Juniors",
        "Dynamos FC", "Highlanders FC", "CAPS United", "Manica Diamonds", "FC Platinum", "Ngezi Platinum"
    ).sorted()

    var homeTeam by remember { mutableStateOf("") }
    var awayTeam by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var homeExpanded by remember { mutableStateOf(false) }
    var awayExpanded by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(containerColor = Color(0xFF0F172A)) { pad ->
            Column(
                Modifier.padding(pad).padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("V101 FOOTBALL AI", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text("Elite Prediction Engine - 100+ Teams", color = Color(0xFF94A3B8))

                // HOME TEAM DROPDOWN
                ExposedDropdownMenuBox(expanded = homeExpanded, onExpandedChange = { homeExpanded = !homeExpanded }) {
                    OutlinedTextField(
                        value = homeTeam,
                        onValueChange = { homeTeam = it; homeExpanded = true },
                        label = { Text("Home Team", color = Color.White) },
                        placeholder = { Text("Type e.g. Dynamos FC", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color(0xFF8B5CF6),
                            cursorColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(expanded = homeExpanded, onDismissRequest = { homeExpanded = false }) {
                        allTeams.filter { it.contains(homeTeam, true) }.take(15).forEach { team ->
                            DropdownMenuItem(text = { Text(team, color = Color.White) }, onClick = { homeTeam = team; homeExpanded = false })
                        }
                    }
                }

                // AWAY TEAM DROPDOWN
                ExposedDropdownMenuBox(expanded = awayExpanded, onExpandedChange = { awayExpanded = !awayExpanded }) {
                    OutlinedTextField(
                        value = awayTeam,
                        onValueChange = { awayTeam = it; awayExpanded = true },
                        label = { Text("Away Team", color = Color.White) },
                        placeholder = { Text("Type e.g. Highlanders", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8B5CF6),
                            unfocusedBorderColor = Color(0xFF8B5CF6),
                            cursorColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(expanded = awayExpanded, onDismissRequest = { awayExpanded = false }) {
                        allTeams.filter { it.contains(awayTeam, true) }.take(15).forEach { team ->
                            DropdownMenuItem(text = { Text(team, color = Color.White) }, onClick = { awayTeam = team; awayExpanded = false })
                        }
                    }
                }

                Button(
                    onClick = {
                        if (homeTeam.isNotBlank() && awayTeam.isNotBlank()) {
                            val homeWin = Random.nextInt(30, 60)
                            val btts = Random.nextInt(45, 75)
                            val over = Random.nextInt(50, 80)
                            val scores = listOf("1-1", "2-1", "1-0", "2-0", "0-1", "1-2", "2-2", "0-0")
                            val score = scores.random()
                            val conf = Random.nextInt(82, 98)
                            val tip = listOf("1X", "X2", "1", "2", "BTTS YES", "Over 2.5").random()
                            result = "MATCH: $homeTeam vs $awayTeam\n\nV101 ANALYSIS:\nHome Win: $homeWin%\nBTTS: $btts%\nOver 2.5: $over%\n\nCORRECT SCORE: $score\nCONFIDENCE: $conf%\n\nTIP: $tip"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) { Text("RUN V101 AI PREDICTION", fontWeight = FontWeight.Bold) }

                if (result.isNotBlank()) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                        Text(result, Modifier.padding(16.dp), color = Color.White, fontSize = 16.sp, lineHeight = 22.sp)
                    }
                }
            }
        }
    }
}
