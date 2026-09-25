package com.predictor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

data class MatchItem(val league: String, val home: String, val away: String, val score: String, val time: String, val live: Boolean, val country: String)
data class LeagueItem(val name: String, val country: String, val flag: String, val type: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    var tab by remember { mutableIntStateOf(2) } // Open Leagues to see

    val allLeagues = remember {
        listOf(
            // --- UEFA COMPETITIONS ---
            LeagueItem("Champions League", "Europe", "🇪🇺", "Cup"),
            LeagueItem("Europa League", "Europe", "🇪🇺", "Cup"),
            LeagueItem("Conference League", "Europe", "🇪🇺", "Cup"),
            LeagueItem("UEFA Super Cup", "Europe", "🇪🇺", "Cup"),
            LeagueItem("Euro Championship", "Europe", "🇪🇺", "Cup"),
            LeagueItem("Euro Qualifiers", "Europe", "🇪🇺", "Cup"),
            LeagueItem("Nations League", "Europe", "🇪🇺", "Cup"),
            LeagueItem("U21 Euro", "Europe", "🇪🇺", "Cup"),
            LeagueItem("Youth League", "Europe", "🇪🇺", "Cup"),

            // --- BIG 5 ---
            LeagueItem("Premier League", "England", "🏴󐁧󐁢󐁥󐁮󐁧󐁿", "League"),
            LeagueItem("Championship", "England", "🏴󐁧󐁢󐁥󐁮󐁧󐁿", "League"),
            LeagueItem("FA Cup", "England", "🏴󐁧󐁢󐁥󐁮󐁧󐁿", "Cup"),
            LeagueItem("EFL Cup", "England", "🏴󐁧󐁢󐁥󐁮󐁧󐁿", "Cup"),

            LeagueItem("LaLiga", "Spain", "🇪🇸", "League"),
            LeagueItem("LaLiga2", "Spain", "🇪🇸", "League"),
            LeagueItem("Copa del Rey", "Spain", "🇪🇸", "Cup"),

            LeagueItem("Serie A", "Italy", "🇮🇹", "League"),
            LeagueItem("Serie B", "Italy", "🇮🇹", "League"),
            LeagueItem("Coppa Italia", "Italy", "🇮🇹", "Cup"),

            LeagueItem("Bundesliga", "Germany", "🇩🇪", "League"),
            LeagueItem("2. Bundesliga", "Germany", "🇩🇪", "League"),
            LeagueItem("3. Liga", "Germany", "🇩🇪", "League"),
            LeagueItem("DFB-Pokal", "Germany", "🇩🇪", "Cup"),

            LeagueItem("Ligue 1", "France", "🇫🇷", "League"),
            LeagueItem("Ligue 2", "France", "🇫🇷", "League"),
            LeagueItem("Coupe de France", "France", "🇫🇷", "Cup"),

            // --- REST OF EUROPE ---
            LeagueItem("Eredivisie", "Netherlands", "🇳🇱", "League"),
            LeagueItem("Eerste Divisie", "Netherlands", "🇳🇱", "League"),
            LeagueItem("KNVB Cup", "Netherlands", "🇳🇱", "Cup"),

            LeagueItem("Primeira Liga", "Portugal", "🇵🇹", "League"),
            LeagueItem("Liga Portugal 2", "Portugal", "🇵🇹", "League"),
            LeagueItem("Taça de Portugal", "Portugal", "🇵🇹", "Cup"),

            LeagueItem("Belgian Pro League", "Belgium", "🇧🇪", "League"),
            LeagueItem("Belgian Cup", "Belgium", "🇧🇪", "Cup"),
            LeagueItem("Jupiler Pro League", "Belgium", "🇧🇪", "League"),

            LeagueItem("Super Lig", "Turkey", "🇹🇷", "League"),
            LeagueItem("1. Lig", "Turkey", "🇹🇷", "League"),
            LeagueItem("Turkish Cup", "Turkey", "🇹🇷", "Cup"),

            LeagueItem("Premiership", "Scotland", "🏴󐁧󐁢󐁳󐁣󐁴󐁿", "League"),
            LeagueItem("Scottish Championship", "Scotland", "🏴󐁧󐁢󐁳󐁣󐁴󐁿", "League"),
            LeagueItem("Scottish Cup", "Scotland", "🏴󐁧󐁢󐁳󐁣󐁴󐁿", "Cup"),

            LeagueItem("Austrian Bundesliga", "Austria", "🇦🇹", "League"),
            LeagueItem("Austrian Cup", "Austria", "🇦🇹", "Cup"),
            LeagueItem("Swiss Super League", "Switzerland", "🇨🇭", "League"),
            LeagueItem("Super League", "Greece", "🇬🇷", "League"),
            LeagueItem("Greek Cup", "Greece", "🇬🇷", "Cup"),

            // Scandinavia
            LeagueItem("Superliga", "Denmark", "🇩🇰", "League"),
            LeagueItem("Allsvenskan", "Sweden", "🇸🇪", "League"),
            LeagueItem("Eliteserien", "Norway", "🇳🇴", "League"),
            LeagueItem("Veikkausliiga", "Finland", "🇫🇮", "League"),
            LeagueItem("1. Deild", "Faroe Islands", "🇫🇴", "League"),
            LeagueItem("Besta Deild", "Iceland", "🇮🇸", "League"),

            // Eastern Europe
            LeagueItem("Ekstraklasa", "Poland", "🇵🇱", "League"),
            LeagueItem("Czech Liga", "Czech Republic", "🇨🇿", "League"),
            LeagueItem("Slovak Super Liga", "Slovakia", "🇸🇰", "League"),
            LeagueItem("Fortuna Liga", "Slovakia", "🇸🇰", "League"),
            LeagueItem("Prva Liga", "Slovenia", "🇸🇮", "League"),
            LeagueItem("HNL", "Croatia", "🇭🇷", "League"),
            LeagueItem("Super Liga", "Serbia", "🇷🇸", "League"),
            LeagueItem("NB I", "Hungary", "🇭🇺", "League"),
            LeagueItem("Liga I", "Romania", "🇷🇴", "League"),
            LeagueItem("Parva Liga", "Bulgaria", "🇧🇬", "League"),
            LeagueItem("Premier League", "Ukraine", "🇺🇦", "League"),
            LeagueItem("Premier League", "Russia", "🇷🇺", "League"),
            LeagueItem("Belarus Premier", "Belarus", "🇧🇾", "League"),

            // Small / Western
            LeagueItem("Premier Division", "Ireland", "🇮🇪", "League"),
            LeagueItem("FAI Cup", "Ireland", "🇮🇪", "Cup"),
            LeagueItem("Premiership", "Northern Ireland", "🇬🇧", "League"),
            LeagueItem("Cymru Premier", "Wales", "🏴󐁧󐁢󐁷󐁬󐁳󐁿", "League"),
            LeagueItem("First Division", "Cyprus", "🇨🇾", "League"),
            LeagueItem("First League", "Malta", "🇲🇹", "League"),
            LeagueItem("Premier League", "Armenia", "🇦🇲", "League"),
            LeagueItem("Erovnuli Liga", "Georgia", "🇬🇪", "League"),
            LeagueItem("Superliga", "Albania", "🇦🇱", "League"),
            LeagueItem("Super League", "Kosovo", "🇽🇰", "League"),
            LeagueItem("Premier League", "Bosnia", "🇧🇦", "League"),
            LeagueItem("First League", "North Macedonia", "🇲🇰", "League"),
            LeagueItem("Super League", "Moldova", "🇲🇩", "League"),
            LeagueItem("A Lyga", "Lithuania", "🇱🇹", "League"),
            LeagueItem("Virsliga", "Latvia", "🇱🇻", "League"),
            LeagueItem("Meistriliiga", "Estonia", "🇪🇪", "League"),
            LeagueItem("Premier League", "Luxembourg", "🇱🇺", "League"),

            // --- AFRICA (Keep your base) ---
            LeagueItem("Zimbabwe PSL", "Zimbabwe", "🇿🇼", "League"),
            LeagueItem("Chibuku Super Cup", "Zimbabwe", "🇿🇼", "Cup"),
            LeagueItem("PSL Premiership", "South Africa", "🇿🇦", "League"),
            LeagueItem("Nedbank Cup", "South Africa", "🇿🇦", "Cup"),
            LeagueItem("Botswana Premier", "Botswana", "🇧🇼", "League"),
            LeagueItem("Zambia Super League", "Zambia", "🇿🇲", "League"),
            LeagueItem("Egypt Premier", "Egypt", "🇪🇬", "League"),
            LeagueItem("Morocco Botola", "Morocco", "🇲🇦", "League"),
            LeagueItem("Nigeria NPFL", "Nigeria", "🇳🇬", "League"),
            LeagueItem("AFCON", "Africa", "🌍", "Cup"),
            LeagueItem("CAF Champions League", "Africa", "🌍", "Cup"),

            // --- WORLD ---
            LeagueItem("World Cup", "World", "🌍", "Cup"),
            LeagueItem("MLS", "USA", "🇺🇸", "League"),
            LeagueItem("Brasileirão", "Brazil", "🇧🇷", "League"),
            LeagueItem("Saudi Pro League", "Saudi Arabia", "🇸🇦", "League"),
        )
    }

    val matches = listOf(
        MatchItem("Premier League", "Arsenal", "Chelsea", "2-1", "FT", false, "🏴󐁧󐁢󐁥󐁮󐁧󐁿"),
        MatchItem("Zimbabwe PSL", "Dynamos", "Highlanders", "1-1", "62'", true, "🇿🇼"),
        MatchItem("LaLiga", "Barcelona", "Real Madrid", "3-2", "FT", false, "🇪🇸"),
        MatchItem("Bundesliga", "Bayern", "Dortmund", "4-2", "FT", false, "🇩🇪"),
        MatchItem("Champions League", "Man City", "Real Madrid", "2-2", "LIVE", true, "🇪🇺"),
    )

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            containerColor = Color(0xFF121212),
            topBar = {
                Column(Modifier.background(Color(0xFF121212)).padding(12.dp)) {
                    Text("fotmob", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Row {
                        listOf("YESTERDAY","TODAY","TOMORROW","THU","FRI").forEach {
                            Box(Modifier.padding(end=8.dp).background(if(it=="TODAY") Color.White else Color(0xFF2A2A2A), RoundedCornerShape(16.dp)).padding(horizontal=12.dp, vertical=6.dp)){
                                Text(it, color = if(it=="TODAY") Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF1E1E1E)) {
                    NavigationBarItem(selected = tab==0, onClick = {tab=0}, icon = {Icon(Icons.Default.Home, null)}, label = {Text("Matches")})
                    NavigationBarItem(selected = tab==1, onClick = {tab=1}, icon = {Icon(Icons.Default.Star, null)}, label = {Text("Predict")})
                    NavigationBarItem(selected = tab==2, onClick = {tab=2}, icon = {Icon(Icons.Default.List, null)}, label = {Text("Leagues")})
                    NavigationBarItem(selected = tab==3, onClick = {tab=3}, icon = {Icon(Icons.Default.Favorite, null)}, label = {Text("Following")})
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad).background(Color(0xFF121212)).fillMaxSize()) {
                when(tab) {
                    0 -> MatchesTab(matches)
                    1 -> PredictTab()
                    2 -> LeaguesTab(allLeagues)
                    3 -> FollowingTab()
                }
            }
        }
    }
}

@Composable
fun MatchesTab(matches: List<MatchItem>) {
    LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
        items(matches.groupBy { it.league }.toList()) { (league, ms) ->
            Column(Modifier.padding(vertical=6.dp).background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp)).fillMaxWidth().padding(12.dp)) {
                Text("${ms[0].country} $league", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
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
}

@Composable
fun PredictTab() {
    var home by remember { mutableStateOf("") }
    var away by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = home, onValueChange = {home=it}, label = {Text("Home Team", color = Color.Gray)}, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = away, onValueChange = {away=it}, label = {Text("Away Team", color = Color.Gray)}, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
        Spacer(Modifier.height(16.dp))
        Button(onClick = { result = if(home.isNotBlank() && away.isNotBlank()) "Prediction: $home 2-1 $away\nConfidence: 87%\nBTTS: Yes" else null }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
            Text("GET PREDICTION", fontWeight = FontWeight.Bold)
        }
        result?.let {
            Spacer(Modifier.height(16.dp))
            Box(Modifier.background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp)).fillMaxWidth().padding(16.dp)){
                Text(it, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LeaguesTab(leagues: List<LeagueItem>) {
    var query by remember { mutableStateOf("") }
    val filtered = leagues.filter { it.name.contains(query, true) || it.country.contains(query, true) }
    val grouped = filtered.groupBy { it.country }
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(value = query, onValueChange = {query=it}, placeholder = {Text("Search leagues", color = Color.Gray)}, leadingIcon = {Icon(Icons.Default.Search, null, tint = Color.Gray)}, modifier = Modifier.fillMaxWidth().padding(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF1E1E1E), unfocusedContainerColor = Color(0xFF1E1E1E)))
        LazyColumn(Modifier.fillMaxSize()) {
            grouped.forEach { (country, list) ->
                item {
                    Row(Modifier.fillMaxWidth().background(Color(0xFF181818)).padding(horizontal=16.dp, vertical=8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(list[0].flag, fontSize = 16.sp, modifier = Modifier.width(28.dp))
                        Text(country.uppercase(), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                items(list) { lg ->
                    Row(Modifier.fillMaxWidth().clickable{}.padding(horizontal=16.dp, vertical=14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(lg.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(lg.type, color = Color.Gray, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                    Divider(color = Color(0xFF2A2A2A), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun FollowingTab() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Follow teams to see them here", color = Color.Gray)
    }
}
