package com.v100.footballai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class Match(val league:String, val home:String, val away:String, val score:String, val time:String, val live:Boolean)

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContent{ V100App() }
    }
}

@Composable
fun V100App(){
    var matches by remember{ mutableStateOf<List<Match>>(emptyList()) }
    var status by remember{ mutableStateOf("Connecting to live servers...") }
    var tab by remember{ mutableStateOf(1) }

    LaunchedEffect(tab){
        status = "Fetching live games..."
        try{
            val result = withContext(Dispatchers.IO){ fetchLive(tab) }
            if(result.isNotEmpty()){
                matches = result
                status = "LIVE ONLINE • ${result.size} games • ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
            } else {
                status = "No live games now - showing demo"
            }
        } catch(e:Exception){
            status = "Offline mode"
        }
    }

    val demo = listOf(
        Match("England - Premier League","Arsenal","Man City","2 - 1","68'",true),
        Match("Zimbabwe - PSL","Dynamos","Highlanders","1 - 0","62'",true),
        Match("Spain - La Liga","Barcelona","Real Madrid","1 - 1","HT",true),
        Match("Germany - Bundesliga","Bayern Munich","Dortmund","3 - 2","FT",false),
        Match("Italy - Serie A","Inter","AC Milan","0 - 0","FT",false)
    )
    val display = if(matches.isNotEmpty()) matches else demo
    val online = matches.isNotEmpty()

    MaterialTheme{
        Column(Modifier.fillMaxSize().background(Color(0xFF0F0F0F))){
            Column(Modifier.fillMaxWidth().background(Color.Black).padding(16.dp)){
                Text("V100 FOOTBALL AI", color=Color.White, fontSize=22.sp, fontWeight=FontWeight.Black, letterSpacing=1.sp)
                Spacer(Modifier.height(6.dp))
                Text(status, color=if(online) Color(0xFF00E676) else Color(0xFF9E9E9E), fontSize=12.sp, fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    listOf("YESTERDAY","TODAY","TOMORROW").forEachIndexed{ i,t ->
                        Button(onClick={tab=i}, colors=ButtonDefaults.buttonColors(containerColor=if(i==tab) Color.White else Color(0xFF2A2A2A))){
                            Text(t, color=if(i==tab) Color.Black else Color.White, fontSize=10.sp, fontWeight=FontWeight.Bold)
                        }
                    }
                }
            }
            LazyColumn(Modifier.fillMaxSize().padding(8.dp), verticalArrangement=Arrangement.spacedBy(6.dp)){
                val groups = display.groupBy{ it.league }
                groups.forEach{ (lg, list) ->
                    item{
                        Text(lg, color=Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth().background(Color(0xFF212121)).padding(12.dp))
                    }
                    items(list){ m ->
                        Row(Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(14.dp)){
                            Text(m.home, color=Color.White, fontSize=14.sp, modifier=Modifier.weight(1f), fontWeight=FontWeight.Medium)
                            Column(Modifier.width(90.dp), horizontalAlignment=androidx.compose.ui.Alignment.CenterHorizontally){
                                if(m.live) Text("LIVE", color=Color(0xFFFF5252), fontSize=10.sp, fontWeight=FontWeight.Black)
                                Text(m.score, color=Color.White, fontSize=15.sp, fontWeight=FontWeight.Bold)
                                Text(m.time, color=Color.Gray, fontSize=11.sp)
                            }
                            Text(m.away, color=Color.White, fontSize=14.sp, modifier=Modifier.weight(1f), fontWeight=FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

fun fetchLive(day:Int): List<Match>{
    val out = mutableListOf<Match>()
    try{
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, day-1)
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
        val url = URL("https://www.thesportsdb.com/api/v1/json/3/eventsday.php?d=$date&s=Soccer")
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("User-Agent","Mozilla/5.0")
        conn.connectTimeout=12000
        conn.readTimeout=12000
        val text = conn.inputStream.bufferedReader().readText()
        conn.disconnect()
        val arr = JSONObject(text).optJSONArray("events") ?: return out
        for(i in 0 until arr.length()){
            val e = arr.getJSONObject(i)
            val hs = e.optString("intHomeScore")
            if(hs.isEmpty() || hs=="null") continue
            val ascore = e.optString("intAwayScore","0")
            out.add(Match(e.optString("strLeague","League"), e.optString("strHomeTeam","Home"), e.optString("strAwayTeam","Away"), "$hs - $ascore", e.optString("strStatus","FT"), true))
            if(out.size>=60) break
        }
    }catch(_:Exception){}
    return out
}
