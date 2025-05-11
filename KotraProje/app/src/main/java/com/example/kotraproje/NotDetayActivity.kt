package com.example.kotraproje

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class NotDetayActivity : AppCompatActivity() {

    private lateinit var etBaslik: EditText
    private lateinit var etIcerik: EditText
    private lateinit var btnKaydet: Button
    private lateinit var dbHelper: DatabaseHelper

    private var notId: Int? = null
    private lateinit var kullaniciAdi: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_detay)

        etBaslik = findViewById(R.id.etBaslik)
        etIcerik = findViewById(R.id.etIcerik)
        btnKaydet = findViewById(R.id.btnKaydet)

        dbHelper = DatabaseHelper(this)
        kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""
        notId = intent.getIntExtra("NOT_ID", -1).takeIf { it != -1 }

        notId?.let {
            val not = dbHelper.notDetayGetir(it)
            not?.let {
                etBaslik.setText(it.first)
                etIcerik.setText(it.second)
            }
        }

        btnKaydet.setOnClickListener {
            val baslik = etBaslik.text.toString().trim()
            val icerik = etIcerik.text.toString().trim()

            if (baslik.isEmpty()) {
                Toast.makeText(this, "Başlık boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val basarili = if (notId != null) {
                dbHelper.notGuncelle(notId!!, baslik, icerik)
            } else {
                dbHelper.notEkle(baslik, icerik, kullaniciAdi)
            }

            if (basarili) {
                Toast.makeText(this, "Not kaydedildi", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Kayıt başarısız oldu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
