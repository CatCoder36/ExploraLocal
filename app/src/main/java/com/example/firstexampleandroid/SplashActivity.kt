package com.example.firstexampleandroid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Espera 2.5 segundos y lanza el MapsActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            finish() // Cerramos la splash para que no vuelva atrás
        }, 3000)
    }
}