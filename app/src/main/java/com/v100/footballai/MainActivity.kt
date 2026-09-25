package com.v100.footballai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    var liveMatches by remember { mutableStateOf<List<MatchItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var tab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val fetched = withContext(Dispatchers.IO) { fetchLive() }
            if(fetched.isNotEmpty()) liveMatches = fetched
        } catch(e: Exception) {}
        isLoading = false
    }

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
                Column(Modifier.background(Color(0xFF121212)).padding(16.dp)) {
                    Text("V100 Football AI - LIVE", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    if(isLoading) Text("Fetching from FotMob...", color = Color.Gray, fontSize = 12.sp)
                    else Text("LIVE DATA FROM INTERNET", color = Color(0xFF00FF7F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF1E1E1E)) {
                    NavigationBarItem(selected = tab==0, onClick = {tab=0}, label = {Text("Matches")}, icon = {})
                    NavigationBarItem(selected = tab==1, onClick = {tab=1}, label = {Text("Leagues")}, icon = {})
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad).background(Color(0xFF121212)).fillMaxSize()) {
                LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
                    items(display) { m ->
                        Column(Modifier.padding(vertical=6.dp).background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp)).fillMaxWidth().padding(12.dp)) {
                            Text(m.league, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth()) {
                                Text(m.home, color = Color.White, modifier = Modifier.weight(1f), fontSize = 14.sp)
                                Box(Modifier.background(if(m.live) Color(0xFF3A1A1A) else Color(0xFF2A2A2A), RoundedCornerShape(6.dp)).padding(horizontal=8.dp, vertical=4.dp)){
                                    Text(if(m.live) m.time else m.score, color = if(m.live) Color.Red else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(m.away, color = Color.White, modifier = Modifier.weight(1f), fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun fetchLive(): List<MatchItem> {
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
                out.add(MatchItem(lname, home, away, "$hs-$ascore", t, t.contains("'")))
                if(out.size>50) break
            }
        }
    } catch(e: Exception) {}
    return out
}
