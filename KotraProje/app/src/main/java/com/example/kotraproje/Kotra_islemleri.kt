package com.example.kotraproje

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Kotra_islemleri : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kotra_islemleri)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val kullaniciAdi = intent.getStringExtra("KULLANICI_ADI")

        val btnKotraEkleme = findViewById<Button>(R.id.btnKotraEkleme)
        btnKotraEkleme.setOnClickListener {
            val intent = Intent(this,Kotraeklemeislemi::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }
        val btnKotraSilme = findViewById<Button>(R.id.btnKotrasilme)
        btnKotraSilme.setOnClickListener {
            val intent = Intent(this, kotrasilmeislemi::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }
        val btnKotraGuncelleme = findViewById<Button>(R.id.btnKotraGuncelleme)
        btnKotraGuncelleme.setOnClickListener {
            val intent = Intent(this, kotraguncellemeislemi::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }
    }
}