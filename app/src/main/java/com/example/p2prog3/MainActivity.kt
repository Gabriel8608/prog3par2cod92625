package com.example.p2prog3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.p2prog3.ui.screens.HomeScreen
import com.example.p2prog3.ui.theme.P2prog3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            P2prog3Theme {
                HomeScreen()
            }
        }
    }
}
