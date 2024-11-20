package c14220127.paba_b.recylerviewrevisi

import android.app.ProgressDialog.show
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private var _nama: MutableList<String> = emptyList<String>().toMutableList()
    private var _karakter: MutableList<String> = emptyList<String>().toMutableList()
    private var _deskripsi: MutableList<String> = emptyList<String>().toMutableList()
    private var _gambar: MutableList<String> = emptyList<String>().toMutableList()
    lateinit var sp: SharedPreferences
    private var arWayang = arrayListOf<wayang>()
    private lateinit var _rvWayang: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        sp = getSharedPreferences("data", MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _rvWayang = findViewById<RecyclerView>(R.id.rvWayang)
        val gson = Gson()
        val isiSP = sp.getString("spWayang", null)
        val type = object : TypeToken<List<wayang>>() {}.type
        if (isiSP != null) {
            arWayang = gson.fromJson(isiSP, type)
        }
        if (arWayang.size == 0) {
            SiapkanData()
        }
        else{
            arWayang.forEach{
                _nama.add(it.nama)
                _gambar.add(it.foto)
                _deskripsi.add(it.deskripsi)
                _karakter.add(it.karakter)
            }
            arWayang.clear()
        }
        TambahData()
        TampilkanData()
    }

    fun SiapkanData() {
        _nama = resources.getStringArray(R.array.namaWayang).toMutableList()
        _deskripsi = resources.getStringArray(R.array.deskripsiWayang).toMutableList()
        _karakter = resources.getStringArray(R.array.karakterUtamaWayang).toMutableList()
        _gambar = resources.getStringArray(R.array.gambarWayang).toMutableList()
    }

    fun TambahData() {
        val gson = Gson()
        val editor = sp.edit()
        arWayang.clear()
        for (position in _nama.indices) {
            val data = wayang(
                _gambar[position],
                _nama[position],
                _karakter[position],
                _deskripsi[position]
            )
            arWayang.add(data)
        }
        val json = gson.toJson(arWayang)
        editor.putString("spWayang", json)
        editor.apply()
    }

    fun TampilkanData() {
        _rvWayang.layoutManager = LinearLayoutManager(this)
        val adapterWayang = AdapterRecReview(arWayang)
        _rvWayang.adapter = adapterWayang

        adapterWayang.setOnItemClickCallback(object : AdapterRecReview.OnItemClickCallback {
            override fun onItemClicked(data: wayang) {
                val intent = Intent(
                    this@MainActivity, detWayang::class
                        .java
                )
                intent.putExtra("kirimData", data)
                startActivity(intent)
            }

            override fun delData(pos: Int) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("HAPUS DATA")
                    .setMessage("Apakah benar data " + _nama[pos] + " ingin dihapus?")
                    .setPositiveButton(
                        "HAPUS",
                        DialogInterface.OnClickListener { dialog, which ->
                            _gambar.removeAt(pos)
                            _nama.removeAt(pos)
                            _karakter.removeAt(pos)
                            _deskripsi.removeAt(pos)
                            TambahData()
                            TampilkanData()
                        }
                    )
                    .setNegativeButton(
                        "BATAL",
                        DialogInterface.OnClickListener { dialog, which ->
                            Toast.makeText(
                                this@MainActivity,
                                "Batal Menghapus Data",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ).show()
            }
        })
    }
}


