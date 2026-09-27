package com.v100.footballai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_matches -> { /* show Matches */ true }
                R.id.nav_news -> { /* show News */ true }
                R.id.nav_leagues -> { /* show Leagues */ true }
                R.id.nav_following -> { /* show Following */ true }
                R.id.nav_more -> { /* show More */ true }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_matches
    }
}
