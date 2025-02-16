package com.example.respondeai

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.respondeai.Adapter.RankAdapter
import com.example.respondeai.Model.Usuario
import com.example.respondeai.databinding.ActivityAddQuestionBinding
import com.example.respondeai.databinding.ActivityRankBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RankActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityRankBinding.inflate(layoutInflater)
    }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializarRank()
        binding.btnVoltar.setOnClickListener {
            startActivity(Intent(this, ChoiceModeActivity::class.java))
            finish()
        }
    }

    private fun inicializarRank() {
        val listaUsuarios = intent.getSerializableExtra("listaRank") as? ArrayList<Usuario>


        binding.rvRank.apply {
            adapter = RankAdapter(listaUsuarios?.sortedByDescending { it.pontuacao.toInt() }!!)
            layoutManager = LinearLayoutManager(context)
        }
    }
}