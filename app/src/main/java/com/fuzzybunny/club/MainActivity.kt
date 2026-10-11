package com.fuzzybunny.club

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.security.MessageDigest
import java.util.*

class MainActivity : AppCompatActivity() {

    private var ghostCode = ""
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var qrView: ImageView
    private lateinit var qrLabel: TextView
    private lateinit var liveStatus: TextView
    private lateinit var ringCircle: TextView
    private lateinit var keyboardWidget: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qrView = findViewById(R.id.qrView)
        qrLabel = findViewById(R.id.qrLabel)
        liveStatus = findViewById(R.id.liveStatus)
        ringCircle = findViewById(R.id.ringCircle)
        keyboardWidget = findViewById(R.id.fireKeyboardWidget)

        // 1. Neon skull looping BG video
        val bgVideo = findViewById<VideoView>(R.id.bgVideo)
        try {
            bgVideo.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.neon_skull_bg}"))
            bgVideo.setOnPreparedListener { it.isLooping = true }
            bgVideo.start()
        } catch (e: Exception) {
            // If no video yet, show black with neon text
            liveStatus.text = "👻 Add neon_skull_bg.mp4 to res/raw/"
        }

        // 2. Ghost QR rotating every 15s - BTC style
        startGhostQRRotation()

        // 3. Fire Ring Circle - nearby devices
        startRingCircleScan()

        // 4. Amazon Fire TV Keyboard widget - your link
        setupFireKeyboardWidget()

        // 5. Go Live button
        findViewById<Button>(R.id.btnGoLive).setOnClickListener {
            startLiveBroadcast()
        }

        // 6. Bunny Hole 404 button
        findViewById<Button>(R.id.btnBunnyHole).setOnClickListener {
            openBunnyHole("404")
        }
    }

    private fun startGhostQRRotation() {
        handler.post(object : Runnable {
            override fun run() {
                ghostCode = "fuzzybunny://live?g=${UUID.randomUUID().toString().take(8)}&k=${generateQuantumKey()}&app=NEON_STRAY"
                val qrBitmap = generateQR(ghostCode)
                qrView.setImageBitmap(qrBitmap)
                qrLabel.text = "👻 GHOST CODE\nExpires in 15s\n\n$ghostCode"
                handler.postDelayed(this, 15000)
            }
        })
    }

    private fun generateQuantumKey(): String {
        val input = "${System.currentTimeMillis()}_${UUID.randomUUID()}_${Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)}"
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray()).joinToString("") { "%02x".format(it) }.take(16).uppercase()
    }

    private fun generateQR(text: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.WHITE else Color.TRANSPARENT)
            }
        }
        return bmp
    }

    private fun startRingCircleScan() {
        // Simulated - in real app use NSD + Advertising ID + Socket.io
        val mockDevices = """
            🔥 FIRE RING CIRCLE - NEARBY
            
            📺 Fire TV Stick [Living Room] - 2m
            📱 Sidekick - Jesse's Phone - 1m
            👻 Ghost_Ω - Encrypted - 5m [LIVE 🔴]
            👻 Ghost_∞ - Encrypted - 12m
            🕳️ Bunny Hole 404 - Hidden Gateway
            
            Ad ID: ${UUID.randomUUID().toString().take(8)}...
        """.trimIndent()
        ringCircle.text = mockDevices
    }

    private fun setupFireKeyboardWidget() {
        keyboardWidget.settings.javaScriptEnabled = true
        keyboardWidget.loadUrl("https://keyboard.amazonfiretvapp.com/?t=db0e319b-df27-52ba-a07e-c781765ec0b9&tn=f19bb093-b051-5365-9818-b5c0a14ec0e5&c=64f47653-0164-55de-9fdb-5fb9e019fb53&r=NA")
        
        keyboardWidget.addJavascriptInterface(object {
            @JavascriptInterface
            fun onTextReceived(text: String) {
                runOnUiThread {
                    liveStatus.text = "⌨️ Sidekick typed: $text"
                    if (text.contains("bunny://") || text.contains("404")) {
                        openBunnyHole(text)
                    }
                }
            }
        }, "Android")
    }

    private fun startLiveBroadcast() {
        liveStatus.text = "🔴 LIVE: Room $ghostCode\nWaiting for scanners in Ring Circle...\nVideo feed active"
        // Here init WebRTC: CameraX + google-webrtc
        // webRTCClient.startBroadcast(ghostCode)
    }

    private fun openBunnyHole(code: String) {
        when {
            code.contains("404") -> {
                liveStatus.text = "🐰 YOU FELL DOWN THE BUNNY HOLE!\n🕳️ 404 Gateway\n\nTry: bunny://hole1 (Ω), bunny://hole2 (∞), bunny://hole3 (π)\nType 'bunnybunnybunny' for Easter egg!"
                // Load WebView with bunny_hole_404.html
            }
            code.contains("bunny://hole1") -> liveStatus.text = "Ω OMEGA GATE OPENED - Quantum Portal"
            code.contains("bunny://hole2") -> liveStatus.text = "∞ INFINITY GATE OPENED - Events Listing"
            code.contains("bunny://hole3") -> liveStatus.text = "π PI GATE OPENED - Noise Listing"
            code.contains("bunnybunnybunny") -> liveStatus.text = "💃 CUTE FUZZY BUNNY GIRL DANCE MODE ACTIVATED! 🐰✨"
        }
    }
}
