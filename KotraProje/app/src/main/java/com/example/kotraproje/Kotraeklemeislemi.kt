package com.example.kotraproje

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Kotraeklemeislemi : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etKotraAdi: EditText
    private lateinit var etHayvanSayisi: EditText
    private lateinit var spHayvanTuru: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kotraeklemeislemi)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""


        etKotraAdi = findViewById(R.id.et1)
        etHayvanSayisi = findViewById(R.id.et2)
        spHayvanTuru = findViewById(R.id.sp1)

        val turler = listOf("İnek", "Düve", "Dana", "Buzağı")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, turler)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHayvanTuru.adapter = adapter

        dbHelper = DatabaseHelper(this)

        findViewById<Button>(R.id.btnkKotraKaydet).setOnClickListener {
            val kotraAdi = etKotraAdi.text.toString().trim()
            val hayvanSayisi = etHayvanSayisi.text.toString().toIntOrNull()
            val hayvanTuru = spHayvanTuru.selectedItem.toString()


            if (kotraAdi.isEmpty() || hayvanSayisi == null || hayvanSayisi <= 0) {
                Toast.makeText(this, "Lütfen tüm alanları doğru doldurun.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val (basarili, mesaj) = dbHelper.insertKotra(kotraAdi, hayvanTuru, hayvanSayisi, kullaniciAdi)
            Toast.makeText(this, mesaj, Toast.LENGTH_SHORT).show()

            if (basarili) {
                finish()
            }
        }
    }
}
