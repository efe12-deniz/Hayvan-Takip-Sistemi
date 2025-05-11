package com.example.kotraproje

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class hayvan_islemleri : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hayvan_islemleri)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnHayvanEkleme = findViewById<Button>(R.id.btnHayvanEkleme)
        val btnHayvanSatis = findViewById<Button>(R.id.btnHayvansilme)
        val btnHayvanSilme2 = findViewById<Button>(R.id.btnHayvansilme2)
        val btnHayvanGuncelleme = findViewById<Button>(R.id.btnHayvanGuncelleme)


        val kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        btnHayvanEkleme.setOnClickListener {
            val intent = Intent(this, hayvanalisislemi::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)  // Kullanıcı adıyla geçiş yap
            startActivity(intent)
        }


        btnHayvanSatis.setOnClickListener {
            val intent = Intent(this, hayvansatisislemi::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }

        btnHayvanSilme2.setOnClickListener {
            val intent = Intent(this, hayvankesimislemi::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }

        btnHayvanGuncelleme.setOnClickListener {
            val intent = Intent(this, hayvanbilgiguncellemesi::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }

    }
}
