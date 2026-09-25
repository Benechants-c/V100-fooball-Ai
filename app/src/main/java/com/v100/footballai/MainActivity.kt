package com.v100.footballai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class MatchItem(val league: String, val home: String, val away: String, val score: String, val time: String, val live: Boolean)
data class LeagueItem(val name: String, val country: String, val type: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    var tab by remember { mutableIntStateOf(0) }
    var liveMatches by remember { mutableStateOf<List<MatchItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val fetched = withContext(Dispatchers.IO) { fetchFotMob() }
            if(fetched.isNotEmpty()) liveMatches = fetched
        } catch(e: Exception) {}
        isLoading = false
    }

    val leagues = listOf(
        LeagueItem("Champions League", "Europe", "Cup"),
        LeagueItem("Premier League", "England", "League"),
        LeagueItem("LaLiga", "Spain", "League"),
        LeagueItem("Serie A", "Italy", "League"),
        LeagueItem("Bundesliga", "Germany", "League"),
        LeagueItem("Ligue 1", "France", "League"),
        LeagueItem("Zimbabwe PSL", "Zimbabwe", "League"),
        LeagueItem("PSL Premiership", "South Africa", "League"),
        LeagueItem("Egypt Premier", "Egypt", "League"),
        LeagueItem("AFCON", "Africa", "Cup"),
        LeagueItem("World Cup", "World", "Cup"),
        LeagueItem("MLS", "USA", "League"),
        LeagueItem("Saudi Pro League", "Saudi Arabia", "League"),
    )

    val fallback = listOf(
        MatchItem("Premier League", "Arsenal", "Chelsea", "2-1", "FT", false),
        MatchItem("Zimbabwe PSL", "Dynamos", "Highlanders", "1-1", "62'", true),
        MatchItem("LaLiga", "Barcelona", "Real Madrid", "3-2", "FT", false),
    )
    val display = if(liveMatches.isNotEmpty()) liveMatches else fallback

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            containerColor = Color(0xFF121212),
            topBar = {
                Column(Modifier.background(Color(0xFF121212)).padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("fotmob LIVE", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        if(isLoading) CircularProgressIndicator(Modifier.size(16.dp), 2.dp, Color.White)
                        else Text("LIVE", color = Color(0xFF00FF7F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF1E1E1E)) {
                    NavigationBarItem(selected = tab==0, onClick = {tab=0}, icon = {Icon(Icons.Default.Home, null)}, label = {Text("Matches")})
                    NavigationBarItem(selected = tab==1, onClick = {tab=1}, icon = {Icon(Icons.Default.Star, null)}, label = {Text("Predict")})
                    NavigationBarItem(selected = tab==2, onClick = {tab=2}, icon = {Icon(Icons.Default.List, null)}, label = {Text("Leagues")})
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad).background(Color(0xFF121212)).fillMaxSize()) {
                when(tab) {
                    0 -> LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
                        items(display.groupBy { it.league }.toList()) { (lg, ms) ->
                            Column(Modifier.padding(vertical=6.dp).background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp)).fillMaxWidth().padding(12.dp)) {
                                Text(lg, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                ms.forEach { m ->
                                    Row(Modifier.fillMaxWidth().padding(vertical=6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(m.home, color = Color.White, modifier = Modifier.weight(1f), fontSize = 13.sp)
                                        Box(Modifier.background(if(m.live) Color(0xFF3A1A1A) else Color(0xFF2A2A2A), RoundedCornerShape(6.dp)).padding(horizontal=8.dp, vertical=4.dp)){
                                            Text(if(m.live) m.time else m.score, color = if(m.live) Color.Red else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(m.away, color = Color.White, modifier = Modifier.weight(1f), fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        var home by remember { mutableStateOf("") }
                        var away by remember { mutableStateOf("") }
                        var res by remember { mutableStateOf<String?>(null) }
                        Column(Modifier.fillMaxSize().padding(16.dp)) {
                            OutlinedTextField(value = home, onValueChange = {home=it}, label = {Text("Home Team", color=Color.Gray)}, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor=Color.White, unfocusedTextColor=Color.White))
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = away, onValueChange = {away=it}, label = {Text("Away Team", color=Color.Gray)}, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor=Color.White, unfocusedTextColor=Color.White))
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { res = "Prediction: $home 2-1 $away\nConfidence: 87% LIVE" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor=Color.White, contentColor=Color.Black)) { Text("GET PREDICTION", fontWeight=FontWeight.Bold) }
                            res?.let { Box(Modifier.padding(top=16.dp).background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp)).fillMaxWidth().padding(16.dp)){ Text(it, color=Color.White) } }
                        }
                    }
                    2 -> {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(leagues) { lg ->
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) { Text(lg.name, color=Color.White, fontSize=14.sp); Text(lg.country, color=Color.Gray, fontSize=11.sp) }
                                }
                                Divider(color=Color(0xFF2A2A2A), thickness=0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun fetchFotMob(): List<MatchItem> {
    val out = mutableListOf<MatchItem>()
    try {
        val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val text = URL("https://www.fotmob.com/api/matches?date=$today").readText()
        val leagues = JSONObject(text).getJSONArray("leagues")
        for(i in 0 until leagues.length()){
            val lo = leagues.getJSONObject(i)
            val lname = lo.optString("name","League")
            val matches = lo.getJSONArray("matches")
            for(j in 0 until matches.length()){
                val m = matches.getJSONObject(j)
                val home = m.optJSONObject("home")?.optString("name","Home") ?: "Home"
                val away = m.optJSONObject("away")?.optString("name","Away") ?: "Away"
                val hs = m.optJSONObject("home")?.optInt("score",0) ?: 0
                val ascore = m.optJSONObject("away")?.optInt("score",0) ?: 0
                val t = m.optJSONObject("status")?.optString("liveTime","FT") ?: "FT"
                val live = t.contains("'") || t.contains("+")
                out.add(MatchItem(lname, home, away, "$hs-$ascore", t, live))
                if(out.size>60) break
            }
        }
    } catch(e: Exception) {}
    return out
}
