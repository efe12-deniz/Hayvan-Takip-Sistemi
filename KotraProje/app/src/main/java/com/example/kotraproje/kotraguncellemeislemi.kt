package com.example.kotraproje

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class kotraguncellemeislemi : AppCompatActivity() {

    private lateinit var etKotraAdi: EditText
    private lateinit var etHayvanSayisi: EditText
    private lateinit var spHayvanTuru: Spinner
    private lateinit var btnGuncelle: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotraguncellemeislemi)

        dbHelper = DatabaseHelper(this)


        val kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        etKotraAdi = findViewById(R.id.etGuncelleme1)
        etHayvanSayisi = findViewById(R.id.etGuncelleme2)
        spHayvanTuru = findViewById<Spinner>(R.id.spGuncelleme1)
        btnGuncelle = findViewById(R.id.btnkKotraKaydet)

        val hayvanTuruList = listOf("İnek", "Düve", "Dana", "Buzağı")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hayvanTuruList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHayvanTuru.adapter = adapter

        btnGuncelle.setOnClickListener {
            val kotraAdi = etKotraAdi.text.toString().trim()
            val hayvanSayisi = etHayvanSayisi.text.toString().toIntOrNull()
            val hayvanTuru = spHayvanTuru.selectedItem.toString()

            if (kotraAdi.isEmpty() || hayvanSayisi == null || hayvanSayisi <= 0) {
                Toast.makeText(this, "Tüm alanları doğru giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val (basarili, mesaj) = dbHelper.updateKotraByName(kotraAdi, hayvanTuru, hayvanSayisi, kullaniciAdi)
            Toast.makeText(this, mesaj, Toast.LENGTH_SHORT).show()

            if (basarili) {
                finish()
            }
        }
    }
}
