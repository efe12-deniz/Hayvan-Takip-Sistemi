package com.example.kotraproje

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.*

class hayvankesimislemi : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var kullaniciAdi: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hayvankesimislemi)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""


        val etKulakNo = findViewById<EditText>(R.id.editTextText2)
        val etKesimBedeli = findViewById<EditText>(R.id.editTextText11)
        val etKesimTarihi = findViewById<EditText>(R.id.editTextText12)
        val etKarkasKg = findViewById<EditText>(R.id.editTextText13)
        val tvKesimOnizleme = findViewById<TextView>(R.id.tvKesimOnizleme)
        val btnKaydet = findViewById<Button>(R.id.button9)

        // TL formatlı önizleme
        etKesimBedeli.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val girilen = s.toString().replace(".", "").replace(",", ".")
                val fiyat = girilen.toDoubleOrNull()
                tvKesimOnizleme.text = if (fiyat != null) formatFiyatTL(fiyat) else ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnKaydet.setOnClickListener {
            val kulakNo = etKulakNo.text.toString().trim()
            val kesimTarihi = etKesimTarihi.text.toString().trim()
            val kesimBedeli = etKesimBedeli.text.toString().replace(".", "").replace(",", ".").toDoubleOrNull()
            val karkasKg = etKarkasKg.text.toString().toDoubleOrNull()

            if (kulakNo.isEmpty() || kesimTarihi.isEmpty() || kesimBedeli == null || karkasKg == null) {
                Toast.makeText(this, "Tüm alanları doğru şekilde doldurun.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.updateHayvanKesimBilgisi(kulakNo, kesimBedeli, kesimTarihi, karkasKg, kullaniciAdi)

            if (result.first) {
                Toast.makeText(this, "Hayvan başarıyla kesildi: ${formatFiyatTL(kesimBedeli)}", Toast.LENGTH_LONG).show()
                etKulakNo.text.clear()
                etKesimBedeli.text.clear()
                etKesimTarihi.text.clear()
                etKarkasKg.text.clear()
                tvKesimOnizleme.text = ""
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
