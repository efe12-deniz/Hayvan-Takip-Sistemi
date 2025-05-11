package com.example.kotraproje

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class NotlarActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var kullaniciAdi: String
    private lateinit var notlar: MutableList<DatabaseHelper.Not>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var btnSil: Button
    private lateinit var btnDuzenle: Button
    private var seciliPozisyon: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notlist)

        dbHelper = DatabaseHelper(this)
        kullaniciAdi = intent.getStringExtra("KULLANICI_ADI") ?: ""

        listView = findViewById(R.id.notListView)
        val btnNotEkle = findViewById<Button>(R.id.btnEkle)
        btnSil = findViewById(R.id.btnSil)
        btnDuzenle = findViewById(R.id.btnDuzenle)


        btnNotEkle.setOnClickListener {
            val intent = Intent(this, NotDetayActivity::class.java)
            intent.putExtra("KULLANICI_ADI", kullaniciAdi)
            startActivity(intent)
        }


        listView.setOnItemClickListener { _, _, position, _ ->
            seciliPozisyon = position
            Toast.makeText(this, "Seçilen not: ${notlar[position].baslik}", Toast.LENGTH_SHORT).show()
        }


        btnSil.setOnClickListener {
            val pos = seciliPozisyon
            if (pos != null) {
                val not = notlar[pos]
                AlertDialog.Builder(this)
                    .setTitle("Notu Sil")
                    .setMessage("Bu notu silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Evet") { _, _ ->
                        dbHelper.notSil(not.id)
                        listeyiYenile()
                        seciliPozisyon = null
                    }
                    .setNegativeButton("Hayır", null)
                    .show()
            } else {
                Toast.makeText(this, "Lütfen önce bir not seçin", Toast.LENGTH_SHORT).show()
            }
        }


        btnDuzenle.setOnClickListener {
            val pos = seciliPozisyon
            if (pos != null) {
                val not = notlar[pos]
                val intent = Intent(this, NotDetayActivity::class.java)
                intent.putExtra("NOT_ID", not.id)
                intent.putExtra("KULLANICI_ADI", kullaniciAdi)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lütfen önce bir not seçin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        listeyiYenile()
    }

    private fun listeyiYenile() {
        notlar = dbHelper.notlariGetir(kullaniciAdi).map {
            DatabaseHelper.Not(it.first, it.second, "")
        }.toMutableList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notlar.map { it.baslik })
        listView.adapter = adapter
    }
}
