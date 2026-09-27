package com.v100.footballai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Load only Matches for now
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MatchesFragment())
            .commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_matches) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MatchesFragment())
                    .commit()
            }
            true
        }
    }
}
