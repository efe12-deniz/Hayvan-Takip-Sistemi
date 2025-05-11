package com.example.kotraproje

import android.os.Bundle
import android.text.Editable
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ListelerActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var kullaniciAdi: String
    private lateinit var listeView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listeler)

        dbHelper = DatabaseHelper(this)
        kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        listeView = findViewById(R.id.listeView)

        val rbKotra = findViewById<RadioButton>(R.id.rbKotra)
        val rbMevcut = findViewById<RadioButton>(R.id.rbMevcut)
        val rbSatilan = findViewById<RadioButton>(R.id.rbSatilan)
        val rbKesilen = findViewById<RadioButton>(R.id.rbKesilen)
        val aramaKutusu = findViewById<EditText>(R.id.aramaKutusu)

        val group1 = findViewById<RadioGroup>(R.id.radioGroupRow1)
        val group2 = findViewById<RadioGroup>(R.id.radioGroupRow2)

        // Radyo butonları birbirini dışlasın
        group1.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) group2.clearCheck()
        }

        group2.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) group1.clearCheck()
        }

        rbKotra.setOnClickListener { kotralariListele() }
        rbMevcut.setOnClickListener { hayvanlariListele("aktif") }
        rbSatilan.setOnClickListener { hayvanlariListele("satildi") }
        rbKesilen.setOnClickListener { hayvanlariListele("kesildi") }

        aramaKutusu.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    private fun kotralariListele() {
        val kotralar = dbHelper.getKotraListesi(kullaniciAdi)
        adapter = object : ArrayAdapter<String>(this, R.layout.list_item, R.id.txtItem, kotralar) {}
        listeView.adapter = adapter

        if (kotralar.isEmpty()) {
            Toast.makeText(this, "Kotra bulunamadı.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun hayvanlariListele(durum: String) {
        val hayvanlar = dbHelper.getHayvanListesi(kullaniciAdi, durum)
        adapter = object : ArrayAdapter<String>(this, R.layout.list_item, R.id.txtItem, hayvanlar) {}
        listeView.adapter = adapter

        if (hayvanlar.isEmpty()) {
            Toast.makeText(this, "Kayıt bulunamadı.", Toast.LENGTH_SHORT).show()
        }
    }

}
