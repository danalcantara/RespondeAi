package com.example.respondeai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.example.respondeai.Model.Perguntas
import com.example.respondeai.Model.Usuario
import com.example.respondeai.ViewModels.VmAplicacao
import com.example.respondeai.ViewModels.VmUsuarios
import com.example.respondeai.databinding.ActivityChoiceModeBinding
import com.example.respondeai.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class ChoiceModeActivity : AppCompatActivity() {
    private lateinit var viewModel: VmUsuarios
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }

    val binding by lazy {
        ActivityChoiceModeBinding.inflate(layoutInflater)
    }
    private var isObserverRegistered = false
    override fun onDestroy() {
        super.onDestroy()
        viewModel.limparDados()
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

        viewModel = (application as VmAplicacao).viewModel



        viewModel.usuario.observe(this, Observer { usuario ->
            if (usuario != null) {
                // Exibir os dados do usuário na interface
                binding.pontuacaoUsuario.text = usuario.pontuacao

            } else {
                // Exibir erro caso o usuário seja null
                binding.pontuacaoUsuario.text = "Usuário não encontrado"
            }
        })
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            viewModel.limparDados()
            startActivity(Intent(this, MainActivity::class.java))
            finish()

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

    fun desabilitarCampos() {

        val campos = listOf(
            binding.btnIniciarJogo,
            binding.btnAdicionarQuestoes,
            binding.button3,
            binding.btnLogout
        )

        // Desabilitar os botões
        campos.forEach { it.isEnabled = false }

        // Tornar o ProgressBar visível
        binding.progressBar.visibility = View.VISIBLE
    }

    fun habilitarCampos() {
        // Desabilitar todos os campos interativos
        val campos = listOf(
            binding.btnIniciarJogo,
            binding.btnAdicionarQuestoes,
            binding.button3,
            binding.btnLogout
        )

        // Desabilitar os botões
        campos.forEach { it.isEnabled = true }

        // Tornar o ProgressBar visível
        binding.progressBar.visibility = View.GONE
    }

    fun NavegarParaPlay(view: View) {
        // Exibir progress bar enquanto as perguntas estão sendo carregadas
        desabilitarCampos()
        carregarPerguntasESortear { pergunta ->
            if (pergunta != null) {
                habilitarCampos()
                val intent = Intent(this, PlayQuestionsActivity::class.java).apply {
                    putExtra("pergunta", pergunta)
                }
                startActivity(intent)

            } else {
                Toast.makeText(this, "Nenhuma Pergunta Disponivel", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun NavegarParaAdicionarPergunta(view: View) {
        startActivity(Intent(this, AddQuestionActivity::class.java))

    }

    fun NavegarParaRank(view: View){
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            NavegarParaRankProcesso()
        }
    }
    suspend fun NavegarParaRankProcesso() {
        var usuarios = firestore.collection("usuarios").get().await()
        var listaUsuarios = mutableListOf<Usuario>()
if (usuarios!= null){
        usuarios.documents.forEach{
            it.toObject(Usuario::class.java)?.let { it1 -> listaUsuarios.add(it1) }
        }
        var intent =Intent(this, RankActivity::class.java).apply{
            putExtra("listaRank", ArrayList(listaUsuarios))
        }
    binding.progressBar.visibility = View.GONE
        startActivity(intent)
    } else {
        Toast.makeText(this, "Não há usuarios no aplicativo", Toast.LENGTH_SHORT).show()
    }
        }

}