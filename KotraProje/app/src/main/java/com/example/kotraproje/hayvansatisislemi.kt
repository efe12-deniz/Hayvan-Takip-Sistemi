package com.example.kotraproje

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.*

class hayvansatisislemi : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var kullaniciAdi: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hayvansatisislemi)

        dbHelper = DatabaseHelper(this)
        kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        val etKulakNo = findViewById<EditText>(R.id.editTextText8)
        val etSatisBedeli = findViewById<EditText>(R.id.editTextText10)
        val etSatisTarihi = findViewById<EditText>(R.id.editTextText9)
        val btnKaydet = findViewById<Button>(R.id.button8)
        val tvSatisOnizleme = findViewById<TextView>(R.id.tvSatisOnizleme)

        // TL biçimli fiyat önizleme
        etSatisBedeli.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val girilen = s.toString()
                    .replace(".", "")
                    .replace(",", ".")
                val fiyat = girilen.toDoubleOrNull()
                tvSatisOnizleme.text = if (fiyat != null) formatFiyatTL(fiyat) else ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnKaydet.setOnClickListener {
            val kulakNo = etKulakNo.text.toString().trim()
            val satisTarihi = etSatisTarihi.text.toString().trim()
            val satisText = etSatisBedeli.text.toString().replace(".", "").replace(",", ".")
            val satisBedeli = satisText.toDoubleOrNull()

            if (kulakNo.isEmpty() || satisTarihi.isEmpty() || satisBedeli == null) {
                Toast.makeText(this, "Tüm alanları doğru şekilde doldurun.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.updateHayvanSatisBilgisi(kulakNo, satisBedeli, satisTarihi, kullaniciAdi)

            if (result.first) {
                Toast.makeText(this, "Hayvan başarıyla satıldı: ${formatFiyatTL(satisBedeli)}", Toast.LENGTH_LONG).show()
                etKulakNo.text.clear()
                etSatisBedeli.text.clear()
                etSatisTarihi.text.clear()
                tvSatisOnizleme.text = ""
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
