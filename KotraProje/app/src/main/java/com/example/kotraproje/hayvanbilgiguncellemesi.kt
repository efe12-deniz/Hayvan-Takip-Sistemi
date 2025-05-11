package com.example.kotraproje

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.*

class hayvanbilgiguncellemesi : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var kullaniciAdi: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hayvanbilgiguncellemesi)

        dbHelper = DatabaseHelper(this)
        kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        val etKulakNo = findViewById<EditText>(R.id.editTextText14)
        val etDogumTarihi = findViewById<EditText>(R.id.editTextText15)
        val etGelisTarihi = findViewById<EditText>(R.id.editTextText16)
        val etAlisFiyati = findViewById<EditText>(R.id.editTextText17)
        val etKotraAdi = findViewById<EditText>(R.id.editTextText18)
        val etAciklama = findViewById<EditText>(R.id.editTextText19)
        val tvAlisOnizleme = findViewById<TextView>(R.id.tvAlisOnizleme)
        val btnKaydet = findViewById<Button>(R.id.button10)

        // TL formatlı canlı önizleme
        etAlisFiyati.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val girilen = s.toString().replace(".", "").replace(",", ".")
                val fiyat = girilen.toDoubleOrNull()
                tvAlisOnizleme.text = if (fiyat != null) formatFiyatTL(fiyat) else ""
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnKaydet.setOnClickListener {
            val kulakNo = etKulakNo.text.toString().trim()
            val dogumTarihi = etDogumTarihi.text.toString().trim()
            val gelisTarihi = etGelisTarihi.text.toString().trim()
            val alisFiyati = etAlisFiyati.text.toString().replace(".", "").replace(",", ".").toDoubleOrNull()
            val kotraAdi = etKotraAdi.text.toString().trim()
            val aciklama = etAciklama.text.toString().trim()

            if (kulakNo.isEmpty()) {
                Toast.makeText(this, "Kulak numarası zorunludur.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.updateHayvanBilgisi(
                kulakNo,
                if (dogumTarihi.isEmpty()) null else dogumTarihi,
                if (gelisTarihi.isEmpty()) null else gelisTarihi,
                alisFiyati,
                if (kotraAdi.isEmpty()) null else kotraAdi,
                if (aciklama.isEmpty()) null else aciklama,
                kullaniciAdi
            )

            Toast.makeText(this, result.second, Toast.LENGTH_LONG).show()

            if (result.first) {
                etDogumTarihi.text.clear()
                etGelisTarihi.text.clear()
                etAlisFiyati.text.clear()
                etKotraAdi.text.clear()
                etAciklama.text.clear()
                tvAlisOnizleme.text = ""
            }
        }
    }

    private fun formatFiyatTL(fiyat: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("tr", "TR"))
        return formatter.format(fiyat) + " TL"
    }
}
