package com.example.respondeai

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.respondeai.databinding.ActivityChoiceModeBinding
import com.example.respondeai.databinding.ActivityMainBinding

class ChoiceModeActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityChoiceModeBinding.inflate(layoutInflater)
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

    }

    fun NavegarParaPlay(view:View){
        startActivity(Intent(this, PlayQuestionsActivity::class.java))
    }
    fun NavegarParaAdicionarPergunta(view:View){
        startActivity(Intent(this, AddQuestionActivity::class.java))

    }
    fun NavegarParaRank(view:View){
        startActivity(Intent(this, RankActivity::class.java))
    }

}