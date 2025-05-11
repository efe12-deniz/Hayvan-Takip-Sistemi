package com.example.kotraproje
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getDoubleOrNull
import java.text.NumberFormat
import java.util.Locale
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 7  // Sürüm yükseltildi

        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_KOTRA = "kotra"
        private const val COLUMN_KOTRA_ID = "id"
        private const val COLUMN_KOTRA_ADI = "kotra_adi"
        private const val COLUMN_HAYVAN_TURU = "hayvan_turu"
        private const val COLUMN_HAYVAN_SAYISI = "hayvan_sayisi"
        private const val COLUMN_KULLANICI_ADI = "kullanici_adi"

        // Yeni notlar tablosu
        private const val TABLE_NOTLAR = "notlar"
        private const val COLUMN_NOTE_ID = "id"
        private const val COLUMN_NOTE_TITLE = "baslik"
        private const val COLUMN_NOTE_TEXT = "icerik"
        private const val COLUMN_NOTE_USER = "kullanici_adi"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("""
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent())

        db?.execSQL("""
            CREATE TABLE $TABLE_KOTRA (
                $COLUMN_KOTRA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_KOTRA_ADI TEXT NOT NULL,
                $COLUMN_HAYVAN_TURU TEXT NOT NULL,
                $COLUMN_HAYVAN_SAYISI INTEGER NOT NULL,
                $COLUMN_KULLANICI_ADI TEXT NOT NULL
            )
        """.trimIndent())

        db?.execSQL("""
            CREATE TABLE hayvanlar (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kulak_no TEXT NOT NULL,
                dogum_tarihi TEXT NOT NULL,
                gelis_tarihi TEXT NOT NULL,
                alis_fiyati REAL NOT NULL,
                aciklama TEXT,
                kotra_adi TEXT NOT NULL,
                satis_bedeli REAL,
                satis_tarihi TEXT,
                kesim_bedeli REAL,
                kesim_tarihi TEXT,
                karkas_kg REAL,
                icilen_sut_kg REAL DEFAULT 0,
                yedigi_yem_kg REAL DEFAULT 0,
                kullanici_adi TEXT NOT NULL,
                satis_durumu TEXT DEFAULT 'aktif',
                FOREIGN KEY (kotra_adi) REFERENCES kotra(kotra_adi)
            )
        """.trimIndent())

        db?.execSQL("""
            CREATE TABLE $TABLE_NOTLAR (
                $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTE_TITLE TEXT NOT NULL,
                $COLUMN_NOTE_TEXT TEXT,
                $COLUMN_NOTE_USER TEXT NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_KOTRA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS hayvanlar")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTLAR")
        onCreate(db)
    }


    fun notEkle(baslik: String, icerik: String, kullaniciAdi: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, baslik)
            put(COLUMN_NOTE_TEXT, icerik)
            put(COLUMN_NOTE_USER, kullaniciAdi)
        }
        val result = db.insert(TABLE_NOTLAR, null, values)
        db.close()
        return result != -1L
    }

    fun notlariGetir(kullaniciAdi: String): List<Pair<Int, String>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_NOTE_ID, $COLUMN_NOTE_TITLE FROM $TABLE_NOTLAR WHERE $COLUMN_NOTE_USER = ?", arrayOf(kullaniciAdi))
        val list = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val baslik = cursor.getString(1)
            list.add(Pair(id, baslik))
        }
        cursor.close()
        db.close()
        return list
    }

    fun notDetayGetir(id: Int): Pair<String, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_NOTE_TITLE, $COLUMN_NOTE_TEXT FROM $TABLE_NOTLAR WHERE $COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
        var sonuc: Pair<String, String>? = null
        if (cursor.moveToFirst()) {
            val baslik = cursor.getString(0)
            val icerik = cursor.getString(1)
            sonuc = Pair(baslik, icerik)
        }
        cursor.close()
        db.close()
        return sonuc
    }

    fun notSil(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NOTLAR, "$COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun notGuncelle(id: Int, yeniBaslik: String, yeniIcerik: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, yeniBaslik)
            put(COLUMN_NOTE_TEXT, yeniIcerik)
        }
        val result = db.update(TABLE_NOTLAR, values, "$COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
    fun insertUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun insertKotra(kotraAdi: String, hayvanTuru: String, hayvanSayisi: Int, kullaniciAdi: String): Pair<Boolean, String> {
        if (kotraAdi.isBlank()) return Pair(false, "Kotra adı boş olamaz.")
        if (hayvanSayisi <= 0) return Pair(false, "Hayvan sayısı 0'dan büyük olmalı.")

        val db = this.readableDatabase
        val checkQuery = "SELECT * FROM $TABLE_KOTRA WHERE $COLUMN_KOTRA_ADI = ? AND $COLUMN_KULLANICI_ADI = ?"
        val cursor = db.rawQuery(checkQuery, arrayOf(kotraAdi, kullaniciAdi))

        if (cursor.count > 0) {
            cursor.close()
            db.close()
            return Pair(false, "Aynı isimde bir kotra zaten mevcut.")
        }
        cursor.close()

        val writableDb = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KOTRA_ADI, kotraAdi)
            put(COLUMN_HAYVAN_TURU, hayvanTuru)
            put(COLUMN_HAYVAN_SAYISI, hayvanSayisi)
            put(COLUMN_KULLANICI_ADI, kullaniciAdi)
        }

        val result = writableDb.insert(TABLE_KOTRA, null, values)
        writableDb.close()
        return if (result != -1L) Pair(true, "Kotra başarıyla eklendi.") else Pair(false, "Kotra eklenemedi.")
    }
    fun updateKotraByName(
        kotraAdi: String,
        yeniHayvanTuru: String,
        yeniHayvanSayisi: Int,
        kullaniciAdi: String
    ): Pair<Boolean, String> {
        if (kotraAdi.isBlank()) return Pair(false, "Kotra adı boş olamaz.")
        if (yeniHayvanSayisi <= 0) return Pair(false, "Hayvan sayısı 0'dan büyük olmalı.")

        val db = this.readableDatabase
        val checkQuery = "SELECT * FROM $TABLE_KOTRA WHERE $COLUMN_KOTRA_ADI = ? AND $COLUMN_KULLANICI_ADI = ?"
        val cursor = db.rawQuery(checkQuery, arrayOf(kotraAdi, kullaniciAdi))

        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return Pair(false, "Kotra bulunamadı.")
        }
        cursor.close()

        val values = ContentValues().apply {
            put(COLUMN_HAYVAN_TURU, yeniHayvanTuru)
            put(COLUMN_HAYVAN_SAYISI, yeniHayvanSayisi)
        }

        val result = db.update(TABLE_KOTRA, values, "$COLUMN_KOTRA_ADI = ? AND $COLUMN_KULLANICI_ADI = ?", arrayOf(kotraAdi, kullaniciAdi))
        db.close()

        return if (result > 0) Pair(true, "Kotra başarıyla güncellendi.") else Pair(false, "Kotra güncellenemedi.")
    }
    fun deleteKotraByAdi(kotraAdi: String, kullaniciAdi: String): Pair<Boolean, String> {
        val db = this.writableDatabase
        val checkQuery = "SELECT * FROM $TABLE_KOTRA WHERE $COLUMN_KOTRA_ADI = ? AND $COLUMN_KULLANICI_ADI = ?"
        val cursor = db.rawQuery(checkQuery, arrayOf(kotraAdi, kullaniciAdi))

        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return Pair(false, "Bu ada sahip kotra bulunamadı.")
        }
        cursor.close()

        val result = db.delete(TABLE_KOTRA, "$COLUMN_KOTRA_ADI = ? AND $COLUMN_KULLANICI_ADI = ?", arrayOf(kotraAdi, kullaniciAdi))
        db.close()

        return if (result > 0) Pair(true, "Kotra başarıyla silindi.") else Pair(false, "Kotra silinemedi.")
    }



    fun insertHayvan(
        kulakNo: String,
        dogumTarihi: String,
        gelisTarihi: String,
        alisFiyati: Double,
        aciklama: String?,
        kotraAdi: String,
        kullaniciAdi: String
    ): Pair<Boolean, String> {
        val db = this.writableDatabase
        val checkKotra = db.rawQuery(
            "SELECT * FROM $TABLE_KOTRA WHERE $COLUMN_KOTRA_ADI = ? AND $COLUMN_KULLANICI_ADI = ?",
            arrayOf(kotraAdi, kullaniciAdi)
        )
        if (checkKotra.count == 0) {
            checkKotra.close()
            db.close()
            return Pair(false, "Girilen kotra adı bu kullanıcıya ait değil veya bulunamadı.")
        }
        checkKotra.close()

        val values = ContentValues().apply {
            put("kulak_no", kulakNo)
            put("dogum_tarihi", dogumTarihi)
            put("gelis_tarihi", gelisTarihi)
            put("alis_fiyati", alisFiyati)
            put("aciklama", aciklama)
            put("kotra_adi", kotraAdi)
            put("kullanici_adi", kullaniciAdi)
            put("satis_durumu", "aktif")
        }

        val result = db.insert("hayvanlar", null, values)
        db.close()
        return if (result != -1L) Pair(true, "Hayvan başarıyla kaydedildi.") else Pair(false, "Kayıt başarısız oldu.")
    }


    // Hayvanın satış bilgilerini güncelleme fonksiyonu
    fun updateHayvanSatisBilgisi(
        kulakNo: String,
        satisBedeli: Double?,   // Nullable double
        satisTarihi: String?,   // Nullable string
        kullaniciAdi: String
    ): Pair<Boolean, String> {
        val db = this.writableDatabase
        val checkQuery = "SELECT * FROM hayvanlar WHERE kulak_no = ? AND kullanici_adi = ? AND satis_durumu = 'aktif'"
        val cursor = db.rawQuery(checkQuery, arrayOf(kulakNo, kullaniciAdi))

        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return Pair(false, "Belirtilen kulak numarasına ait aktif hayvan bulunamadı.")
        }
        cursor.close()

        val values = ContentValues().apply {
            if (satisBedeli != null && satisTarihi != null) {
                put("satis_bedeli", satisBedeli)  // Double türünde değer
                put("satis_tarihi", satisTarihi)  // String türünde değer
                put("satis_durumu", "satildi")   // Durum satıldı
            }
        }

        val result = db.update("hayvanlar", values, "kulak_no = ? AND kullanici_adi = ?", arrayOf(kulakNo, kullaniciAdi))
        db.close()

        return if (result > 0) Pair(true, "Satış başarıyla güncellendi.") else Pair(false, "Satış güncellenemedi.")
    }

    // Hayvanın kesim bilgilerini güncelleme fonksiyonu
    fun updateHayvanKesimBilgisi(
        kulakNo: String,
        kesimBedeli: Double,
        kesimTarihi: String,
        karkasKg: Double,
        kullaniciAdi: String
    ): Pair<Boolean, String> {
        val db = this.writableDatabase
        val checkQuery = "SELECT * FROM hayvanlar WHERE kulak_no = ? AND kullanici_adi = ? AND satis_durumu = 'aktif'"
        val cursor = db.rawQuery(checkQuery, arrayOf(kulakNo, kullaniciAdi))

        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return Pair(false, "Belirtilen kulak numarasına ait aktif hayvan bulunamadı.")
        }
        cursor.close()

        val values = ContentValues().apply {
            put("kesim_bedeli", kesimBedeli)
            put("kesim_tarihi", kesimTarihi)
            put("karkas_kg", karkasKg)
            put("satis_durumu", "kesildi")
        }

        val result = db.update("hayvanlar", values, "kulak_no = ? AND kullanici_adi = ?", arrayOf(kulakNo, kullaniciAdi))
        db.close()

        return if (result > 0) Pair(true, "Hayvan başarıyla kesildi.") else Pair(false, "Kesim işlemi başarısız oldu.")
    }
    fun updateHayvanBilgisi(
        kulakNo: String,
        yeniDogumTarihi: String?,
        yeniGelisTarihi: String?,
        yeniAlisFiyati: Double?,
        yeniKotraAdi: String?,
        yeniAciklama: String?,
        kullaniciAdi: String
    ): Pair<Boolean, String> {
        val db = this.writableDatabase

        // Önce kayıt var mı kontrolü
        val cursor = db.rawQuery(
            "SELECT * FROM hayvanlar WHERE kulak_no = ? AND kullanici_adi = ?",
            arrayOf(kulakNo, kullaniciAdi)
        )
        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return Pair(false, "Bu kulak numarasına sahip hayvan bulunamadı.")
        }
        cursor.close()

        val values = ContentValues()
        if (!yeniDogumTarihi.isNullOrBlank()) values.put("dogum_tarihi", yeniDogumTarihi)
        if (!yeniGelisTarihi.isNullOrBlank()) values.put("gelis_tarihi", yeniGelisTarihi)
        if (yeniAlisFiyati != null) values.put("alis_fiyati", yeniAlisFiyati)
        if (!yeniKotraAdi.isNullOrBlank()) values.put("kotra_adi", yeniKotraAdi)
        if (!yeniAciklama.isNullOrBlank()) values.put("aciklama", yeniAciklama)

        if (values.size() == 0) {
            db.close()
            return Pair(false, "Güncellenecek bilgi girilmedi.")
        }

        val result = db.update("hayvanlar", values, "kulak_no = ? AND kullanici_adi = ?", arrayOf(kulakNo, kullaniciAdi))
        db.close()
        return if (result > 0) Pair(true, "Hayvan bilgisi başarıyla güncellendi.") else Pair(false, "Güncelleme başarısız.")
    }
    fun getKotraListesi(kullaniciAdi: String): List<String> {
        val list = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT kotra_adi, hayvan_turu, hayvan_sayisi FROM kotra WHERE kullanici_adi = ?",
            arrayOf(kullaniciAdi)
        )
        while (cursor.moveToNext()) {
            val satir = """
            Kotra Adı     : ${cursor.getString(0)}
            Hayvan Türü   : ${cursor.getString(1)}
            Hayvan Sayısı : ${cursor.getInt(2)}
        """.trimIndent()
            list.add(satir)
        }
        cursor.close()
        db.close()
        return list
    }


    fun getHayvanListesi(kullaniciAdi: String, durum: String): List<String> {
        val list = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT kulak_no, dogum_tarihi, gelis_tarihi, alis_fiyati, kotra_adi,
               satis_bedeli, satis_tarihi, kesim_bedeli, kesim_tarihi, karkas_kg
        FROM hayvanlar 
        WHERE kullanici_adi = ? AND satis_durumu = ?
        """.trimIndent(),
            arrayOf(kullaniciAdi, durum)
        )

        // Format tanımlayıcı
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0.00", symbols)

        while (cursor.moveToNext()) {
            val kulak = cursor.getString(0)
            val dogum = cursor.getString(1)
            val gelis = cursor.getString(2)
            val alisStr = formatter.format(cursor.getDouble(3))
            val kotra = cursor.getString(4)

            val satisBedeliStr = formatter.format(cursor.getDouble(5))
            val satisTarihi = cursor.getString(6)
            val kesimBedeliStr = formatter.format(cursor.getDouble(7))
            val kesimTarihi = cursor.getString(8)
            val karkasKg = cursor.getDouble(9)

            val satir = when (durum) {
                "satildi" -> """
                Kulak No       : $kulak
                Doğum Tarihi   : $dogum
                Geliş Tarihi   : $gelis
                Alış Fiyatı    : $alisStr TL
                Kotra Adı      : $kotra
                Satış Bedeli   : $satisBedeliStr TL
                Satış Tarihi   : $satisTarihi
            """.trimIndent()

                "kesildi" -> """
                Kulak No       : $kulak
                Doğum Tarihi   : $dogum
                Geliş Tarihi   : $gelis
                Alış Fiyatı    : $alisStr TL
                Kotra Adı      : $kotra
                Kesim Bedeli   : $kesimBedeliStr TL
                Kesim Tarihi   : $kesimTarihi
                Karkas Ağırlık : $karkasKg kg
            """.trimIndent()

                else -> """
                Kulak No       : $kulak
                Doğum Tarihi   : $dogum
                Geliş Tarihi   : $gelis
                Alış Fiyatı    : $alisStr TL
                Kotra Adı      : $kotra
            """.trimIndent()
            }

            list.add(satir)
        }

        cursor.close()
        db.close()
        return list
    }









    data class Kotra(
        val id: Int,
        val kotraAdi: String,
        val hayvanTuru: String,
        val hayvanSayisi: Int
    )
    fun getHayvanKarZarar(kullaniciAdi: String): List<String> {
        val list = mutableListOf<String>()
        val db = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT kulak_no, dogum_tarihi, gelis_tarihi, satis_bedeli, kesim_bedeli, satis_durumu " +
                    "FROM hayvanlar WHERE kullanici_adi = ? AND satis_durumu != 'aktif'",
            arrayOf(kullaniciAdi)
        )

        val df1 = java.text.SimpleDateFormat("dd.MM.yyyy")
        val df2 = java.text.SimpleDateFormat("dd-MM-yyyy")

        while (cursor.moveToNext()) {
            val kulakNo = cursor.getString(0)
            val dogumTarihi = cursor.getString(1)
            val gelisTarihi = cursor.getString(2)
            val satisBedeli = cursor.getDoubleOrNull(3)
            val kesimBedeli = cursor.getDoubleOrNull(4)
            val durum = cursor.getString(5)

            val dogum = try {
                df1.parse(dogumTarihi)
            } catch (e: Exception) {
                try { df2.parse(dogumTarihi) } catch (ex: Exception) { null }
            }

            val gelis = try {
                df1.parse(gelisTarihi)
            } catch (e: Exception) {
                try { df2.parse(gelisTarihi) } catch (ex: Exception) { null }
            }

            if (dogum == null || gelis == null) continue // geçersiz tarih varsa atla

            val gunFarki = ((gelis.time - dogum.time) / (1000 * 60 * 60 * 24)).toInt()

            var gider = 0.0
            if (gunFarki > 0) {
                val ilk90 = minOf(gunFarki, 90)
                val sonra = maxOf(gunFarki - 90, 0)
                val sonraMax = minOf(sonra, 300)
                gider += ilk90 * 50
                gider += sonraMax * 500
            }

            val gelir = when (durum) {
                "satildi" -> satisBedeli ?: 0.0
                "kesildi" -> kesimBedeli ?: 0.0
                else -> 0.0
            }

            val fark = gelir - gider
            val nf = NumberFormat.getNumberInstance(Locale("tr", "TR"))
            val satir = if (fark >= 0)
                "$kulakNo → Kar: ${nf.format(fark)} TL"
            else
                "$kulakNo → Zarar: ${nf.format(-fark)} TL"


            list.add(satir)
        }

        cursor.close()
        db.close()
        return list
    }

    fun getIsletmeKarZarar(kullaniciAdi: String): List<String> {
        val hayvanlar = getHayvanKarZarar(kullaniciAdi)

        var toplamKar = 0.0
        var toplamZarar = 0.0

        for (satir in hayvanlar) {
            when {
                satir.contains("Kar:") -> {
                    val kar = satir.substringAfter("Kar:").replace("TL", "").trim().replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
                    toplamKar += kar
                }
                satir.contains("Zarar:") -> {
                    val zarar = satir.substringAfter("Zarar:").replace("TL", "").trim().replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
                    toplamZarar += zarar
                }
            }
        }

        val net = toplamKar - toplamZarar

        // Nokta binlik, virgül ondalık formatı için
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0.00", symbols)

        val netText = if (net >= 0) "Net Kar: ${formatter.format(net)} TL" else "Net Zarar: ${formatter.format(-net)} TL"

        return listOf(
            "Toplam Kar: ${formatter.format(toplamKar)} TL",
            "Toplam Zarar: ${formatter.format(toplamZarar)} TL",
            netText
        )
    }


    data class Not(val id: Int, val baslik: String, val icerik: String)

}
