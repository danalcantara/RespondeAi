package com.example.respondeai

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.respondeai.Model.Perguntas
import com.example.respondeai.ViewModels.VmAplicacao
import com.example.respondeai.ViewModels.VmUsuarios
import com.example.respondeai.databinding.ActivityPlayQuestionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.collection.LLRBNode.Color
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.random.Random

class PlayQuestionsActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityPlayQuestionsBinding.inflate(layoutInflater)
    }
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    lateinit var vmModel: VmUsuarios
    lateinit var respostaCerta:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnDesistir.setOnClickListener {
            desabilitarBotoes(binding.btnDesistir)
            startActivity(Intent(this, ChoiceModeActivity::class.java))
            finish()
        }
        vmModel = (application as VmAplicacao).viewModel
        vmModel.usuario.observe(this, Observer { usuario ->
            if (usuario != null) {
                // Exibir os dados do usuário na interface
                binding.pontuacao.text = usuario.pontuacao

            } else {
                // Exibir erro caso o usuário seja null
                binding.pontuacao.text = "Usuário não encontrado"
            }
        })
        val pergunta = intent.getSerializableExtra("pergunta") as? Perguntas
        binding.pergunta.text = pergunta?.pergunta
        respostaCerta = pergunta?.respCerta!!
        var respRandom = mutableListOf(pergunta?.item1, pergunta?.item2, pergunta?.item3).shuffled()

        binding.Item1.text = respRandom[0]
        binding.Item2.text = respRandom[1]
        binding.Item3.text = respRandom[2]
    }

    fun usuarioAcertou(){
        vmModel.atualizarPontuacao(auth.currentUser?.uid!!, "10") { ponto ->
                if (ponto) {
                    Toast.makeText(this, "Ganhou 10 pontos por acertar :D", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Erro ao carregar", Toast.LENGTH_SHORT).show()

                }
    }
        }
    fun TrocarPergunta() {
        // Exibir progress bar enquanto as perguntas estão sendo carregadas

        carregarPerguntasESortear { pergunta ->
            binding.playProgress.visibility = View.VISIBLE
            if (pergunta != null) {
                val intent = Intent(this, PlayQuestionsActivity::class.java).apply {
                    putExtra("pergunta", pergunta)
                }
                lifecycleScope.launch {
                    delay(4000)
                    binding.playProgress.visibility = View.GONE
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Nenhuma Pergunta Disponivel", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun carregarPerguntasESortear(callback: (Perguntas?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("perguntas").get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val listaPerguntas =
                    result.documents.mapNotNull { it.toObject(Perguntas::class.java) }
                if (listaPerguntas.isNotEmpty()) {
                    val perguntaSorteada = listaPerguntas[Random.nextInt(listaPerguntas.size)]
                    Log.d("Pergunta", "Pergunta sorteada: ${perguntaSorteada.pergunta}")
                    callback(perguntaSorteada)
                } else {
                    callback(null) // Nenhuma pergunta encontrada
                }
            }
            .addOnFailureListener {
                callback(null) // Erro ao buscar perguntas
            }
    }
    fun clickItem1(view: View) {
        if (binding.Item1.text.toString() == respostaCerta) {
            binding.Item1.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
            binding.Item1.text = "Resposta Certa"
            desabilitarBotoes(binding.Item1)
            usuarioAcertou()
            Toast.makeText(this, "Resposta Certa", Toast.LENGTH_SHORT).show()
            TrocarPergunta()

        } else {
            binding.Item1.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
            binding.Item1.text = "Resposta Errada"
            desabilitarBotoes(binding.Item1)

            Toast.makeText(this, "Resposta Errada", Toast.LENGTH_SHORT).show()
            TrocarPergunta()

        }
    }

    fun clickItem2(view: View) {
        if (binding.Item2.text.toString() == respostaCerta) {
            binding.Item2.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
            binding.Item2.text = "Resposta Certa"
            desabilitarBotoes(binding.Item2)
            usuarioAcertou()
            Toast.makeText(this, "Resposta Certa", Toast.LENGTH_SHORT).show()
            TrocarPergunta()

        } else {
            binding.Item2.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
            binding.Item2.text = "Resposta Errada"
            desabilitarBotoes(binding.Item2)

            Toast.makeText(this, "Resposta Errada", Toast.LENGTH_SHORT).show()
            TrocarPergunta()

        }
    }

    fun clickItem3(view: View) {
        if (binding.Item3.text.toString() == respostaCerta) {
            binding.Item3.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
            desabilitarBotoes(binding.Item3)
            usuarioAcertou()
            binding.Item3.text = "Resposta Certa"
            Toast.makeText(this, "Resposta Certa", Toast.LENGTH_SHORT).show()
            TrocarPergunta()

        } else {
            binding.Item3.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
            binding.Item3.text = "Resposta Errada"
            desabilitarBotoes(binding.Item3)
            Toast.makeText(this, "Resposta Errada", Toast.LENGTH_SHORT).show()
            TrocarPergunta()

        }
    }
    private fun desabilitarBotoes(btn: View) {
        val botoes = listOf(binding.Item1, binding.Item2, binding.Item3, binding.btnDesistir)

        botoes.forEach { if (it != btn) {it.isEnabled = false}}
    }
}