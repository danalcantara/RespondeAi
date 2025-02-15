package com.example.respondeai

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

class RankActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityRankBinding.inflate(layoutInflater)
    }
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
    }

    private fun inicializarRank() {
        val usuarios = mutableListOf(
            Usuario("1", "Alice Silva", "alice@email.com", 150),
            Usuario("2", "Bruno Souza", "bruno@email.com", 200),
            Usuario("3", "Carlos Mendes", "carlos@email.com", 180),
            Usuario("4", "Daniela Lima", "daniela@email.com", 220),
            Usuario("5", "Eduardo Castro", "eduardo@email.com", 170),
            Usuario("6", "Fernanda Rocha", "fernanda@email.com", 250),
            Usuario("7", "Gabriel Alves", "gabriel@email.com", 190),
            Usuario("8", "Helena Martins", "helena@email.com", 210),
            Usuario("9", "Igor Ferreira", "igor@email.com", 160),
            Usuario("10", "Juliana Costa", "juliana@email.com", 230)
        )

        binding.rvRank.apply {
            adapter = RankAdapter(usuarios.sortedByDescending { it.Pontuacao })
            layoutManager = LinearLayoutManager(context)
        }
    }
}