package com.example.kotraproje

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AnaSayfa : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_ana_sayfa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        val twKarsilama = findViewById<TextView>(R.id.twKarsilama)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        val button5 = findViewById<Button>(R.id.button5)


        // **Intent ile gelen kullanıcı adını al**
        val kullaniciAdi = intent.getStringExtra("KULLANICI_ADI")

        // **TextView'e kullanıcı adını yazdır**
        if (kullaniciAdi != null && kullaniciAdi.isNotEmpty()) {
            twKarsilama.text = "Hoşgeldiniz, $kullaniciAdi !"
        } else {
            twKarsilama.text = "Hoşgeldiniz"
        }

        button.setOnClickListener {
            val intent = Intent(this, Kotra_islemleri::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)

        }
        
        button2.setOnClickListener {
            val intent = Intent(this, hayvan_islemleri::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(this, ListelerActivity::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }
        button4.setOnClickListener {
            val intent = Intent(this, KarGosterActivity::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi) // kullaniciAdi doğru şekilde set edilmiş olmalı
            startActivity(intent)

        }
        button5.setOnClickListener {
            val intent = Intent(this, NotlarActivity::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi) // kullaniciAdi doğru şekilde set edilmiş olmalı
            startActivity(intent)

        }







    }
}