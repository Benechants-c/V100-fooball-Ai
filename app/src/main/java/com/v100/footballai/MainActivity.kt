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

data class M(val league:String, val home:String, val away:String, val score:String, val min:String, val live:Boolean)

class MainActivity : ComponentActivity(){
    override fun onCreate(b:Bundle?){
        super.onCreate(b)
        setContent{ App() }
    }
}

@Composable
fun App(){
    var list by remember{ mutableStateOf<List<M>>(emptyList()) }
    var status by remember{ mutableStateOf("Loading LIVE...") }
    var day by remember{ mutableStateOf(1) }

    LaunchedEffect(day){
        status = "Fetching online..."
        try{
            val d = withContext(Dispatchers.IO){ fetch(day) }
            if(d.isNotEmpty()){ list = d; status = "LIVE ONLINE ${d.size} matches ${SimpleDateFormat("HH:mm",Locale.US).format(Date())}" }
            else status = "No games today - demo showing"
        }catch(e:Exception){ status = "Offline demo - ${e.message?.take(30)}" }
    }

    val demo = listOf(
        M("England - Premier League","Arsenal","Man City","2 - 1","68'",true),
        M("Spain - La Liga","Barcelona","Real Madrid","1 - 1","45+2'",true),
        M("Zimbabwe - PSL","Dynamos","Highlanders","1 - 0","62'",true),
        M("Italy - Serie A","Inter","AC Milan","0 - 0","FT",false),
        M("Germany - Bundesliga","Bayern","Dortmund","3 - 2","FT",false),
        M("France - Ligue 1","PSG","Marseille","2 - 0","FT",false)
    )
    val show = if(list.isNotEmpty()) list else demo

    MaterialTheme{
        Column(Modifier.fillMaxSize().background(Color(0xFF101010))){
            Column(Modifier.fillMaxWidth().background(Color.Black).padding(14.dp)){
                Text("FOTMOB", color=Color.White, fontSize=20.sp, fontWeight=FontWeight.Black)
                Text(status, color=if(list.isNotEmpty()) Color.Green else Color.Red, fontSize=11.sp, fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    listOf("YESTERDAY","TODAY","TOMORROW").forEachIndexed{ i,t ->
                        Button(onClick={day=i}, colors=ButtonDefaults.buttonColors(containerColor=if(i==day) Color.White else Color.DarkGray), contentPadding=PaddingValues(horizontal=12.dp, vertical=4.dp)){
                            Text(t, color=if(i==day) Color.Black else Color.White, fontSize=10.sp, fontWeight=FontWeight.Bold)
                        }
                    }
                }
            }
            LazyColumn(Modifier.fillMaxSize().background(Color(0xFF101010)).padding(8.dp), verticalArrangement=Arrangement.spacedBy(6.dp)){
                val groups = show.groupBy{ it.league }
                groups.forEach{ (lg,ms) ->
                    item{
                        Text(lg, color=Color.White, fontSize=13.sp, fontWeight=FontWeight.Bold, modifier=Modifier.background(Color(0xFF202020)).fillMaxWidth().padding(10.dp))
                    }
                    items(ms){ m ->
                        Row(Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(12.dp)){
                            Text(m.home, color=Color.White, fontSize=13.sp, modifier=Modifier.weight(1f), fontWeight=FontWeight.Medium)
                            Column(horizontalAlignment=androidx.compose.ui.Alignment.CenterHorizontally){
                                if(m.live) Text("LIVE", color=Color.Red, fontSize=10.sp, fontWeight=FontWeight.Bold)
                                Text(m.score, color=Color.White, fontSize=15.sp, fontWeight=FontWeight.Bold)
                                Text(m.min, color=Color.Gray, fontSize=11.sp)
                            }
                            Text(m.away, color=Color.White, fontSize=13.sp, modifier=Modifier.weight(1f), textAlign=androidx.compose.ui.text.style.TextAlign.End, fontWeight=FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

fun fetch(dayOff:Int):List<M>{
    val out = mutableListOf<M>()
    try{
        val cal = Calendar.getInstance(); cal.add(Calendar.DAY_OF_YEAR, dayOff-1)
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
        val url = URL("https://www.thesportsdb.com/api/v1/json/3/eventsday.php?d=$d&s=Soccer")
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("User-Agent","Mozilla/5.0")
        conn.connectTimeout=12000; conn.readTimeout=12000
        val txt = conn.inputStream.bufferedReader().readText(); conn.disconnect()
        val arr = JSONObject(txt).optJSONArray("events")
        if(arr!=null){
            for(i in 0 until arr.length()){
                val e = arr.getJSONObject(i)
                val lg = e.optString("strLeague","League")
                val h = e.optString("strHomeTeam","Home")
                val a = e.optString("strAwayTeam","Away")
                val hs = e.optString("intHomeScore","0")
                val ascore = e.optString("intAwayScore","0")
                if(hs=="null" || hs.isEmpty()) continue
                val st = e.optString("strStatus","FT")
                val live = st.contains("1H")||st.contains("2H")||st.contains("HT")
                out.add(M(hs+" - "+ascore, h, a, hs+" - "+ascore, st, live).let{ M(lg,h,a,hs+" - "+ascore,st,live) })
                if(out.size>60) break
            }
        }
    }catch(_:Exception){}
    return out
}
