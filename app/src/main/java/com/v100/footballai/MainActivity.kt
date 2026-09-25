package com.v100.footballai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FotMobCleanApp() }
    }
}

data class Match(val league: String, val home: String, val away: String, val time: String, val isLive: Boolean, val score: String)

@Composable
fun FotMobCleanApp() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf(1) }
    val dates = listOf("YESTERDAY", "TODAY", "TOMORROW", "THU", "FRI", "SAT", "SUN")

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            containerColor = Color(0xFF121212),
            topBar = {
                Column(Modifier.background(Color(0xFF1A1A1A)).padding(top = 8.dp)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("fotmob", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                        }
                    }
                    Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        dates.forEachIndexed { i, d ->
                            val isSel = i == selectedDate
                            Box(Modifier.clip(RoundedCornerShape(20.dp)).background(if (isSel) Color.White else Color(0xFF2A2A2A)).padding(horizontal = 18.dp, vertical = 8.dp)) {
                                Text(d, color = if (isSel) Color.Black else Color.Gray, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                    Divider(color = Color(0xFF2A2A2A), thickness = 0.5.dp)
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF1A1A1A)) {
                    NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Matches", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, indicatorColor = Color(0xFF2A2A2A)))
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.Star, null) }, label = { Text("Predict", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, indicatorColor = Color(0xFF2A2A2A)))
                    NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Default.List, null) }, label = { Text("Leagues", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, indicatorColor = Color(0xFF2A2A2A)))
                    NavigationBarItem(selected = selectedTab == 3, onClick = { selectedTab = 3 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Following", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, indicatorColor = Color(0xFF2A2A2A)))
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad).background(Color(0xFF121212)).fillMaxSize()) {
                when (selectedTab) {
                    0 -> MatchesScreen()
                    1 -> PredictScreenFotMob()
                    2 -> LeaguesScreenFotMob()
                    3 -> ProfileScreenFotMob()
                }
            }
        }
    }
}

@Composable
fun MatchesScreen() {
    val matchesByLeague = mapOf(
        "Premier League" to listOf(
            Match("PL", "Manchester City", "Arsenal", "FT", false, "2-1"),
            Match("PL", "Liverpool", "Chelsea", "78'", true, "1-1"),
            Match("PL", "Man United", "Tottenham", "15:00", false, "-")
        ),
        "Zimbabwe PSL" to listOf(
            Match("ZIM", "Dynamos FC", "Highlanders FC", "15:00", false, "-"),
            Match("ZIM", "CAPS United", "FC Platinum", "LIVE 62'", true, "0-1")
        ),
        "LaLiga" to listOf(
            Match("LIGA", "Real Madrid", "Barcelona", "21:00", false, "-")
        ),
        "CAF Champions League" to listOf(
            Match("CAF", "Simba SC", "Al Ahly", "18:00", false, "-"),
            Match("CAF", "Sundowns", "Young Africans", "18:00", false, "-")
        )
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
        matchesByLeague.forEach { (league, matches) ->
            item {
                Row(Modifier.fillMaxWidth().background(Color(0xFF1E1E1E)).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(20.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) { Text(league.take(1), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
                    Text(league, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            items(matches) { m ->
                Card(colors = CardDefaults.cardColors(Color(0xFF1A1A1A)), shape = RoundedCornerShape(0.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(m.time, color = if (m.isLive) Color(0xFFFF3B30) else Color.Gray, fontSize = 11.sp, fontWeight = if (m.isLive) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.width(60.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF2A2A2A)), contentAlignment = Alignment.Center) { Text(m.home.take(2).uppercase(), fontSize = 8.sp, color = Color.White) }
                                Text(m.home, color = Color.White, fontSize = 13.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF2A2A2A)), contentAlignment = Alignment.Center) { Text(m.away.take(2).uppercase(), fontSize = 8.sp, color = Color.White) }
                                Text(m.away, color = Color.White, fontSize = 13.sp)
                            }
                        }
                        if (m.score != "-") {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(m.score, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            if (m.isLive) Box(Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                        }
                    }
                    Divider(color = Color(0xFF2A2A2A), thickness = 0.5.dp, modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictScreenFotMob() {
    val teams = listOf("Dynamos FC", "Highlanders FC", "Man City", "Arsenal", "Real Madrid", "Barcelona", "Simba SC", "Mamelodi Sundowns", "CAPS United", "Liverpool", "Man United", "Chelsea").sorted()
    var home by remember { mutableStateOf("") }
    var away by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var homeExp by remember { mutableStateOf(false) }
    var awayExp by remember { mutableStateOf(false) }

    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("AI Prediction Lab", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item {
            Card(colors = CardDefaults.cardColors(Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(expanded = homeExp, onExpandedChange = { homeExp = !homeExp }) {
                        OutlinedTextField(value = home, onValueChange = { home = it; homeExp = true }, label = { Text("Home Team - WHITE TEXT FIXED", color = Color.Gray) }, modifier = Modifier.fillMaxWidth().menuAnchor(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.Gray, cursorColor = Color.White))
                        ExposedDropdownMenu(expanded = homeExp, onDismissRequest = { homeExp = false }) { teams.filter { it.contains(home, true) }.forEach { t -> DropdownMenuItem(text = { Text(t, color = Color.White) }, onClick = { home = t; homeExp = false }) } }
                    }
                    ExposedDropdownMenuBox(expanded = awayExp, onExpandedChange = { awayExp = !awayExp }) {
                        OutlinedTextField(value = away, onValueChange = { away = it; awayExp = true }, label = { Text("Away Team - WHITE TEXT FIXED", color = Color.Gray) }, modifier = Modifier.fillMaxWidth().menuAnchor(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.Gray, cursorColor = Color.White))
                        ExposedDropdownMenu(expanded = awayExp, onDismissRequest = { awayExp = false }) { teams.filter { it.contains(away, true) }.forEach { t -> DropdownMenuItem(text = { Text(t, color = Color.White) }, onClick = { away = t; awayExp = false }) } }
                    }
                    Button(onClick = { result = "MATCH: $home vs $away\n\nFORM: $home WWWDL (Last 5)\nFORM: $away LWDWL\nH2H: 2-1-2\n\nPREDICTION: 2-1\nConfidence: 87%\nTip: 1X + Over 1.5\nBTTS: YES 64%" }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(Color.White)) { Text("GET PREDICTION", color = Color.Black, fontWeight = FontWeight.Bold) }
                }
            }
        }
        if (result.isNotBlank()) {
            item {
                Card(colors = CardDefaults.cardColors(Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                    Text(result, Modifier.padding(16.dp), color = Color.White, lineHeight = 20.sp, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun LeaguesScreenFotMob() {
    val leagues = listOf("Premier League - England" to "20 Teams", "LaLiga - Spain" to "20 Teams", "Bundesliga - Germany" to "18 Teams", "Serie A - Italy" to "20 Teams", "Zim PSL - Zimbabwe" to "18 Teams", "SA Prem - South Africa" to "16 Teams", "Tanzania PL" to "16 Teams", "CAF Champions League" to "16 Teams")
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
        item { Text("All Leagues", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp)) }
        items(leagues) { (name, info) ->
            Row(Modifier.fillMaxWidth().background(Color(0xFF1E1E1E)).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(name, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text(info, color = Color.Gray, fontSize = 12.sp)
            }
            Divider(color = Color(0xFF121212), thickness = 1.dp)
        }
    }
}

@Composable
fun ProfileScreenFotMob() {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(80.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) { Text("V100", color = Color.Black, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(12.dp))
        Text("V100 Football AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("FotMob Design - Clean Version", color = Color.Gray, fontSize = 13.sp)
        Spacer(Modifier.height(20.dp))
        Card(colors = CardDefaults.cardColors(Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("V105 CLEAN BUILD", color = Color.White, fontWeight = FontWeight.Bold)
                Text("No hidden unicode\nWhite text fixed\n4 Pages\nFotMob style UI", color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}
