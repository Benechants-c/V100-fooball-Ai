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

data class MatchItem(val league: String, val country: String, val home: String, val away: String, val score: String, val minute: String, val live: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StableFotMob() }
    }
}

@Composable
fun StableFotMob() {
    var matches by remember { mutableStateOf<List<MatchItem>>(emptyList()) }
    var dayTab by remember { mutableIntStateOf(1) }

    LaunchedEffect(dayTab) {
        try {
            val data = withContext(Dispatchers.IO) { fetchReal(dayTab) }
            if(data.isNotEmpty()) matches = data
        } catch(_: Exception) {}
    }

    val fallback = listOf(
        MatchItem("Premier League", "England", "Arsenal", "Man City", "2 - 1", "68'", true),
        MatchItem("Premier League", "England", "Man Utd", "Liverpool", "0 - 0", "32'", true),
        MatchItem("La Liga", "Spain", "Barcelona", "Real Madrid", "1 - 1", "45+2'", true),
        MatchItem("Zimbabwe PSL", "Zimbabwe", "Dynamos", "Highlanders", "1 - 0", "62'", true),
        MatchItem("Serie A", "Italy", "Inter", "AC Milan", "0 - 0", "FT", false),
        MatchItem("Bundesliga", "Germany", "Bayern", "Dortmund", "3 - 2", "FT", false),
    )
    val display = if(matches.isNotEmpty()) matches else fallback

    MaterialTheme {
        Column(Modifier.fillMaxSize().background(Color(0xFF121212))) {
            // TOP BAR - FIXED NO TRANSPARENCY
            Column(Modifier.fillMaxWidth().background(Color(0xFF121212)).padding(16.dp)) {
                Text("FOTMOB", color=Color.White, fontSize=22.sp, fontWeight=FontWeight.Black)
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    listOf("YESTERDAY","TODAY","TOMORROW").forEachIndexed { idx, t ->
                        val sel = idx==dayTab
                        Box(Modifier.clip(RoundedCornerShape(20.dp)).background(if(sel) Color.White else Color(0xFF2A2A2A)).padding(horizontal=16.dp, vertical=8.dp)) {
                            Text(t, color=if(sel) Color.Black else Color.Gray, fontSize=11.sp, fontWeight=FontWeight.Bold)
                        }
                    }
                }
            }
            // MATCHES LIST
            LazyColumn(Modifier.fillMaxSize().background(Color(0xFF121212)).padding(12.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
                val grouped = display.groupBy { "${it.country} • ${it.league}" }
                grouped.forEach { (leagueName, list) ->
                    item {
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF1E1E1E)).padding(12.dp), verticalAlignment=Alignment.CenterVertically) {
                            Box(Modifier.size(22.dp).clip(CircleShape).background(Color.White), contentAlignment=Alignment.Center){ Text(leagueName.take(1), color=Color.Black, fontSize=10.sp, fontWeight=FontWeight.Bold) }
                            Spacer(Modifier.width(8.dp))
                            Text(leagueName, color=Color.White, fontSize=13.sp, fontWeight=FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            if(list.any{it.live}) Box(Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFF3A1A1A)).padding(horizontal=6.dp, vertical=2.dp)){ Text("LIVE", color=Color.Red, fontSize=9.sp, fontWeight=FontWeight.Bold) }
                        }
                    }
                    items(list) { m ->
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF1E1E1E)).padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                                Column(Modifier.weight(1f), horizontalAlignment=Alignment.CenterHorizontally) {
                                    Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF2A2A2A)), contentAlignment=Alignment.Center){ Text(m.home.take(3).uppercase(), color=Color.White, fontSize=11.sp, fontWeight=FontWeight.Bold) }
                                    Spacer(Modifier.height(6.dp))
                                    Text(m.home, color=Color.White, fontSize=13.sp, textAlign=TextAlign.Center, maxLines=1)
                                }
                                Column(Modifier.width(100.dp), horizontalAlignment=Alignment.CenterHorizontally) {
                                    if(m.live) Box(Modifier.clip(RoundedCornerShape(10.dp)).background(Color.Red).padding(horizontal=8.dp, vertical=2.dp)){ Text("LIVE", color=Color.White, fontSize=9.sp, fontWeight=FontWeight.Bold) }
                                    Spacer(Modifier.height(4.dp))
                                    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFF2A2A2A)).padding(horizontal=14.dp, vertical=6.dp)){ Text(m.score, color=Color.White, fontSize=16.sp, fontWeight=FontWeight.Bold) }
                                    Spacer(Modifier.height(4.dp))
                                    Text(m.minute, color=Color.Gray, fontSize=11.sp)
                                }
                                Column(Modifier.weight(1f), horizontalAlignment=Alignment.CenterHorizontally) {
                                    Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF2A2A2A)), contentAlignment=Alignment.Center){ Text(m.away.take(3).uppercase(), color=Color.White, fontSize=11.sp, fontWeight=FontWeight.Bold) }
                                    Spacer(Modifier.height(6.dp))
                                    Text(m.away, color=Color.White, fontSize=13.sp, textAlign=TextAlign.Center, maxLines=1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun fetchReal(dayOffset: Int): List<MatchItem> {
    val out = mutableListOf<MatchItem>()
    try {
        val cal = Calendar.getInstance(); cal.add(Calendar.DAY_OF_YEAR, dayOffset-1)
        val date = SimpleDateFormat("yyyyMMdd", Locale.US).format(cal.time)
        val url = URL("https://www.fotmob.com/api/matches?date=$date")
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")
        conn.connectTimeout=8000; conn.readTimeout=8000
        val txt = conn.inputStream.bufferedReader().readText(); conn.disconnect()
        val leagues = JSONObject(txt).getJSONArray("leagues")
        for(i in 0 until leagues.length()){
            val lo = leagues.getJSONObject(i)
            val lname = lo.optString("name","League"); val ccode = lo.optString("ccode","")
            val ms = lo.getJSONArray("matches")
            for(j in 0 until ms.length()){
                val m = ms.getJSONObject(j)
                val home = m.optJSONObject("home")?.optString("name","Home") ?: "Home"
                val away = m.optJSONObject("away")?.optString("name","Away") ?: "Away"
                val hs = m.optJSONObject("home")?.optInt("score",0) ?: 0
                val aw = m.optJSONObject("away")?.optInt("score",0) ?: 0
                val st = m.optJSONObject("status")
                val liveTime = st?.optString("liveTime","") ?: ""
                val short = st?.optString("short","FT") ?: "FT"
                val started = st?.optBoolean("started",false) ?: false
                val finished = st?.optBoolean("finished",false) ?: false
                val isLive = liveTime.contains("'") || (started && !finished)
                val minute = if(liveTime.isNotEmpty()) liveTime else short
                out.add(MatchItem(lname, ccode, home, away, "$hs - $aw", minute, isLive))
                if(out.size>60) break
            }
        }
    } catch(_: Exception) {}
    return out
}
