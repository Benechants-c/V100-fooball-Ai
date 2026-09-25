package com.v100.footballai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class MatchItem(val league: String, val country: String, val home: String, val away: String, val score: String, val time: String, val live: Boolean, val minute: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FotMobClone() }
    }
}

@Composable
fun FotMobClone() {
    var matches by remember { mutableStateOf<List<MatchItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var dayTab by remember { mutableIntStateOf(1) }
    var bottomTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(dayTab) {
        loading = true
        try {
            val data = withContext(Dispatchers.IO) { fetchReal(dayTab) }
            if(data.isNotEmpty()) matches = data
        } catch(_: Exception) {}
        loading = false
    }

    val fallback = listOf(
        MatchItem("Premier League", "England", "Arsenal", "Man City", "2-1", "2-1", true, "68'"),
        MatchItem("La Liga", "Spain", "Barcelona", "Real Madrid", "1-1", "1-1", true, "45+2'"),
        MatchItem("Serie A", "Italy", "Inter Milan", "AC Milan", "0-0", "0-0", true, "32'"),
        MatchItem("Zimbabwe PSL", "Zimbabwe", "Dynamos", "Highlanders", "1-0", "1-0", true, "62'"),
        MatchItem("Bundesliga", "Germany", "Bayern", "Dortmund", "3-2", "3-2", false, "FT"),
        MatchItem("Ligue 1", "France", "PSG", "Marseille", "2-0", "2-0", false, "FT"),
    )
    val display = if(matches.isNotEmpty()) matches else fallback
    val grouped = display.groupBy { "${it.country} - ${it.league}" }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            containerColor = Color(0xFF000000),
            topBar = {
                Column(Modifier.background(Color(0xFF000000)).padding(horizontal=16.dp, vertical=12.dp)) {
                    Row(verticalAlignment=Alignment.CenterVertically, modifier=Modifier.fillMaxWidth()) {
                        Text("FOTMOB", color=Color.White, fontSize=22.sp, fontWeight=FontWeight.Black, letterSpacing=1.sp, modifier=Modifier.weight(1f))
                        Text("O", color=Color(0xFF2A2A2A), modifier=Modifier.padding(horizontal=12.dp))
                        Text("S", color=Color(0xFF2A2A2A))
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        listOf("YESTERDAY", "TODAY", "TOMORROW").forEachIndexed { idx, title ->
                            val sel = idx==dayTab
                            Box(Modifier.clip(RoundedCornerShape(20.dp)).background(if(sel) Color.White else Color(0xFF2A2A2A)).padding(horizontal=18.dp, vertical=8.dp)){
                                Text(title, color=if(sel) Color.Black else Color(0xFF888888), fontSize=12.sp, fontWeight=FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor=Color(0xFF000000)) {
                    listOf("Matches", "News", "Leagues", "Following", "More").forEachIndexed { i, t ->
                        NavigationBarItem(
                            selected = i==bottomTab, onClick={bottomTab=i},
                            icon={ Box(Modifier.size(24.dp).background(if(i==0) Color(0xFF3DD598) else Color(0xFF444444), CircleShape)) },
                            label={ Text(t, fontSize=10.sp, color=if(i==0) Color(0xFF3DD598) else Color.Gray) }
                        )
                    }
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad).background(Color(0xFF000000)).fillMaxSize()) {
                if(loading) LinearProgressIndicator(modifier=Modifier.fillMaxWidth(), color=Color.White, trackColor=Color(0xFF1A1A1A))
                LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    grouped.forEach { (leagueName, leagueMatches) ->
                        item {
                            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart=12.dp, topEnd=12.dp)).background(Color(0xFF1A1A1A)).padding(12.dp), verticalAlignment=Alignment.CenterVertically) {
                                Box(Modifier.size(20.dp).clip(CircleShape).background(Color.White), contentAlignment=Alignment.Center){ Text(leagueName.first().toString(), fontSize=10.sp, fontWeight=FontWeight.Bold, color=Color.Black) }
                                Spacer(Modifier.width(8.dp))
                                Text(leagueName, color=Color.White, fontSize=13.sp, fontWeight=FontWeight.Bold, modifier=Modifier.weight(1f))
                            }
                        }
                        items(leagueMatches) { m ->
                            Column(Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp), horizontalAlignment=Alignment.CenterHorizontally) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f), horizontalAlignment=Alignment.CenterHorizontally) {
                                        Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF2A2A2A)), contentAlignment=Alignment.Center){ Text(m.home.take(3).uppercase(), color=Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold) }
                                        Spacer(Modifier.height(8.dp))
                                        Text(m.home, color=Color.White, fontSize=13.sp, fontWeight=FontWeight.Medium, textAlign=TextAlign.Center)
                                    }
                                    Column(horizontalAlignment=Alignment.CenterHorizontally, modifier=Modifier.width(110.dp)) {
                                        if(m.live) Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFFD32F2F)).padding(horizontal=10.dp, vertical=3.dp)){ Text("LIVE", color=Color.White, fontSize=10.sp, fontWeight=FontWeight.Bold) }
                                        Spacer(Modifier.height(6.dp))
                                        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF2A2A2A)).padding(horizontal=20.dp, vertical=8.dp)){ Text(m.score, color=Color.White, fontSize=18.sp, fontWeight=FontWeight.Bold) }
                                        Spacer(Modifier.height(6.dp))
                                        Text(m.minute, color=if(m.live) Color.White else Color.Gray, fontSize=12.sp)
                                    }
                                    Column(Modifier.weight(1f), horizontalAlignment=Alignment.CenterHorizontally) {
                                        Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF2A2A2A)), contentAlignment=Alignment.Center){ Text(m.away.take(3).uppercase(), color=Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold) }
                                        Spacer(Modifier.height(8.dp))
                                        Text(m.away, color=Color.White, fontSize=13.sp, fontWeight=FontWeight.Medium, textAlign=TextAlign.Center)
                                    }
                                }
                            }
                            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF000000)))
                        }
                        item { Spacer(Modifier.height(4.dp)) }
                    }
                }
            }
        }
    }
}

fun fetchReal(dayOffset: Int): List<MatchItem> {
    val out = mutableListOf<MatchItem>()
    try {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, dayOffset-1)
        val date = SimpleDateFormat("yyyyMMdd", Locale.US).format(cal.time)
        val url = URL("https://www.fotmob.com/api/matches?date=$date")
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10)")
        conn.setRequestProperty("Accept", "application/json")
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        val txt = conn.inputStream.bufferedReader().readText()
        conn.disconnect()
        val leagues = JSONObject(txt).getJSONArray("leagues")
        for(i in 0 until leagues.length()){
            val lo = leagues.getJSONObject(i)
            val lname = lo.optString("name", "League")
            val ccode = lo.optString("ccode", "World")
            val ms = lo.getJSONArray("matches")
            for(j in 0 until ms.length()){
                val m = ms.getJSONObject(j)
                val home = m.optJSONObject("home")?.optString("name","Home") ?: "Home"
                val away = m.optJSONObject("away")?.optString("name","Away") ?: "Away"
                val hs = m.optJSONObject("home")?.optInt("score",0) ?: 0
                val aws = m.optJSONObject("away")?.optInt("score",0) ?: 0
                val status = m.optJSONObject("status")
                val liveTime = status?.optString("liveTime","") ?: ""
                val short = status?.optString("short","FT") ?: "FT"
                val started = status?.optBoolean("started",false) ?: false
                val finished = status?.optBoolean("finished",false) ?: false
                val isLive = liveTime.contains("'") || (started && !finished)
                val minute = if(liveTime.isNotEmpty()) liveTime else short
                out.add(MatchItem(lname, ccode, home, away, "$hs - $aws", "$hs - $aws", isLive, minute))
                if(out.size>60) break
            }
        }
    } catch(e: Exception) {}
    return out
}
