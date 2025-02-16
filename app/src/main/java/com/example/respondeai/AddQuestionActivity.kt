package com.example.respondeai

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.respondeai.databinding.ActivityAddQuestionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddQuestionActivity : AppCompatActivity() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }

    val binding by lazy {
        ActivityAddQuestionBinding.inflate(layoutInflater)
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
        binding.btnAdicionarPergunta.setOnClickListener {
            verificarCamposParaPergunta()
        }
        binding.btnVoltarAddQt.setOnClickListener {
            startActivity(Intent(this, ChoiceModeActivity::class.java))
            finish()
        }
    }
    fun validarPerguntaERespostas(pergunta: String, resposta1: String, resposta2: String, resposta3: String): Boolean {
        return pergunta.isNotBlank() && resposta1.isNotBlank() && resposta2.isNotBlank() && resposta3.isNotBlank()
    }
fun verificarCamposParaPergunta(){
    if (validarPerguntaERespostas( binding.Question.text.toString(), binding.qtCerta.text.toString(), binding.qtErrada.text.toString(),binding.qtErrada2.text.toString())) {

        lifecycleScope.launch {
            adicionarPergunta()
            startActivity(Intent(this@AddQuestionActivity, ChoiceModeActivity::class.java))
            finish()
        }
    } else {
        Toast.makeText(this,"Erro: Nenhum campo pode estar vazio.", Toast.LENGTH_SHORT).show()
    }
}


    suspend fun adicionarPergunta(): Boolean {
        return try {

            var pergunta = mapOf(
                "pergunta" to binding.Question.text.toString(),
                "item1" to binding.qtCerta.text.toString(),
                "item2" to binding.qtErrada.text.toString(),
                "item3" to binding.qtErrada2.text.toString(),
                "respCerta" to binding.qtCerta.text.toString(),

            )
            firestore.collection("perguntas").add(pergunta).await()
        true
        } catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
}