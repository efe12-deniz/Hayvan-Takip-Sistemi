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

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        dbHelper = DatabaseHelper(this)

        val etKullaniciAdi = findViewById<EditText>(R.id.etKullaniciAdi)
        val etParola = findViewById<EditText>(R.id.etParola)
        val btnGirisYap = findViewById<Button>(R.id.btnGirisYap)
        val btnKayitOl = findViewById<Button>(R.id.btnKayitOl)

        btnGirisYap.setOnClickListener {
            val username = etKullaniciAdi.text.toString().trim()
            val password = etParola.text.toString().trim()


            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Kullanıcı adı veya şifre boş olamaz!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Veritabanında kullanıcıyı kontrol et
            if (dbHelper.checkUser(username, password)) {
                Toast.makeText(this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                // Yeni sayfaya yönlendir
                val intent = Intent(this, AnaSayfa::class.java)
                intent.putExtra("KULLANICI_ADI", username) // Kullanıcı adını gönderiyoruz
                startActivity(intent)
            } else {
                Toast.makeText(this, "Hatalı kullanıcı adı veya şifre!", Toast.LENGTH_SHORT).show()
            }
        }


        btnKayitOl.setOnClickListener {
            val intent = Intent(this, KayitOl::class.java)
            startActivity(intent)
        }




    }
}