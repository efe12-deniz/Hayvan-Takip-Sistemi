package com.example.kotraproje

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class KayitOl : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kayit_ol)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGiriseDon = findViewById<Button>(R.id.btnGiriseDonKayitOl)
        btnGiriseDon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        dbHelper = DatabaseHelper(this)

        val etKullaniciAdi = findViewById<EditText>(R.id.etKullaniciAdiKayitOl)
        val etParola = findViewById<EditText>(R.id.etParolaKayitOl)
        val btnKaydet = findViewById<Button>(R.id.btnKaydetKayitOl)

        btnKaydet.setOnClickListener {
            val username = etKullaniciAdi.text.toString().trim()
            val password = etParola.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val isInserted = dbHelper.insertUser(username, password)
                if (isInserted) {
                    Toast.makeText(this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Kayıt Başarısız!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Boş alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}