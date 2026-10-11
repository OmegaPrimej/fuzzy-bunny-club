package com.fuzzybunny.club

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val liveStatus = findViewById<TextView>(R.id.liveStatus)
        liveStatus.text = "👻 Ghost QR Active - Fuzzy Bunny Club"
    }
}
