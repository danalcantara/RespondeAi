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
import com.example.respondeai.ViewModels.VmAplicacao
import com.example.respondeai.ViewModels.VmUsuarios
import com.example.respondeai.databinding.ActivityTelaCarregamentoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TelaCarregamento : AppCompatActivity() {
    private lateinit var viewModel: VmUsuarios
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    val binding by lazy {
        ActivityTelaCarregamentoBinding.inflate(layoutInflater)
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

        iniciarCarregamento()
    }
    private fun iniciarCarregamento() {
        val barra = binding.telaDeCarregamentoBar
        barra.visibility = View.VISIBLE // Exibir a barra de carregamento

        viewModel = (application as VmAplicacao).viewModel

        // Chama a função para carregar os dados do usuário
        viewModel.carregarUsuario(auth.currentUser?.uid.toString()) { carregado ->
            barra.visibility = View.GONE // Esconde a barra de carregamento

            if (carregado) {
                // Aqui, observe o LiveData para garantir que os dados foram carregados
                viewModel.usuario.observe(this, Observer { usuario ->
                    if (usuario != null) {
                        // Sucesso: navegar para a próxima tela
                        startActivity(Intent(this, ChoiceModeActivity::class.java))
                        finish() // Finaliza a Tela de Carregamento
                    } else {
                        // Caso o usuário seja null, exibe um erro
                        Toast.makeText(this, "Falha ao carregar os dados do usuário", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // Falha no carregamento: exibe erro
                Toast.makeText(this, "Falha ao carregar os dados", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
