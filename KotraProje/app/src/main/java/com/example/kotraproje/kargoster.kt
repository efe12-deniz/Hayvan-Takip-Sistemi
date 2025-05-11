package com.example.kotraproje

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class KarGosterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var kullaniciAdi: String
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var aramaKutusu: EditText

    private var veriListesi = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kargoster) // XML dosya adı burada neyse o


        kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        if (kullaniciAdi.isBlank()) {
            Toast.makeText(this, "Kullanıcı adı alınamadı.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)

        listView = findViewById(R.id.listViewKar)
        aramaKutusu = findViewById(R.id.aramaKutusuKar)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupRow1)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, veriListesi)
        listView.adapter = adapter

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            veriListesi = when (checkedId) {
                R.id.rbIsletmeKar -> dbHelper.getIsletmeKarZarar(kullaniciAdi).toMutableList()
                R.id.rbHayvanKar -> dbHelper.getHayvanKarZarar(kullaniciAdi).toMutableList()
                else -> mutableListOf("Veri bulunamadı.")
            }
            listeyiYenile()
        }

        aramaKutusu.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun listeyiYenile() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, veriListesi)
        listView.adapter = adapter
        adapter.filter.filter(aramaKutusu.text.toString())
    }
}
