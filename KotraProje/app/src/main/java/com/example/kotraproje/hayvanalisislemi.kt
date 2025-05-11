package com.example.kotraproje

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class hayvanalisislemi : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hayvanalisislemi)

        dbHelper = DatabaseHelper(this)


        val kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        val etKulakNo = findViewById<EditText>(R.id.editTextText)
        val etDogumTarihi = findViewById<EditText>(R.id.editTextText3)
        val etGelisTarihi = findViewById<EditText>(R.id.editTextText4)
        val etAlisFiyati = findViewById<EditText>(R.id.editTextText5)
        val etKotraAdi = findViewById<EditText>(R.id.editTextText6)
        val etAciklama = findViewById<EditText>(R.id.editTextText7)
        val btnKaydet = findViewById<Button>(R.id.button7)
        val tvFiyatOnizleme = findViewById<TextView>(R.id.tvFiyatOnizleme)

        // Fiyat biçim önizleme
        etAlisFiyati.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val girilen = s.toString()
                    .replace(".", "")
                    .replace(",", ".")
                val fiyat = girilen.toDoubleOrNull()
                tvFiyatOnizleme.text = if (fiyat != null) formatFiyatTL(fiyat) else ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnKaydet.setOnClickListener {
            val kulakNo = etKulakNo.text.toString().trim()
            val dogumTarihi = etDogumTarihi.text.toString().trim()
            val gelisTarihi = etGelisTarihi.text.toString().trim()
            val alisFiyatiText = etAlisFiyati.text.toString().trim().replace(".", "").replace(",", ".")
            val alisFiyati = alisFiyatiText.toDoubleOrNull()
            val kotraAdi = etKotraAdi.text.toString().trim()
            val aciklama = etAciklama.text.toString().trim()

            if (kulakNo.isEmpty() || dogumTarihi.isEmpty() || gelisTarihi.isEmpty() || alisFiyati == null || kotraAdi.isEmpty()) {
                Toast.makeText(this, "Tüm alanları doğru şekilde doldurunuz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.insertHayvan(
                kulakNo, dogumTarihi, gelisTarihi, alisFiyati, aciklama, kotraAdi, kullaniciAdi
            )

            if (result.first) {
                val formatted = formatFiyatTL(alisFiyati)
                Toast.makeText(this, "Kayıt başarılı: $formatted", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, result.second, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatFiyatTL(fiyat: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("tr", "TR"))
        return formatter.format(fiyat) + " TL"
    }
}
