package com.example.kotraproje

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class kotrasilmeislemi : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kotrasilmeislemi)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)

        val etKotraAdi = findViewById<EditText>(R.id.etsime1)
        val btnSil = findViewById<Button>(R.id.btnkKotrasil)


        val kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        btnSil.setOnClickListener {
            val kotraAdi = etKotraAdi.text.toString().trim()

            if (kotraAdi.isNotEmpty()) {
                val (basarili, mesaj) = dbHelper.deleteKotraByAdi(kotraAdi, kullaniciAdi)
                Toast.makeText(this, mesaj, Toast.LENGTH_SHORT).show()

                if (basarili) {
                    etKotraAdi.text.clear()
                    finish()
                }
            } else {
                Toast.makeText(this, "Lütfen bir kotra adı giriniz!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
