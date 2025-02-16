package com.example.respondeai

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.respondeai.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnRegistrar.setOnClickListener {
            lifecycleScope.launch {
                navegarParaPaginaInicialRegistro()
            }
        }
        binding.btnVoltarRegister.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private suspend fun navegarParaPaginaInicialRegistro() {
        if (verificaCampos()) {
            val usuarioRegistrado = registrarUsuario()
            if (usuarioRegistrado) {
                val usuarioAdicionadoAoBanco = adicionaUsuarioAoBanco()
                if (usuarioAdicionadoAoBanco) {
                    startActivity(Intent(this, TelaCarregamento::class.java))
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Erro no preenchimento dos dados", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun registrarUsuario(): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etSenha.text.toString()
            ).await()
            Toast.makeText(this, "Registrado com sucesso!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Registro falhou: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private suspend fun adicionaUsuarioAoBanco(): Boolean {
        return try {
            val dataUsuarios = mapOf(
                "id" to auth.currentUser?.uid,
                "nome" to binding.etNome.text.toString(),
                "email" to binding.etEmail.text.toString(),
                "pontuacao" to "0"
            )

            firestore.collection("usuarios").document(auth.currentUser?.uid.toString()).set(dataUsuarios).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun verificaCampos(): Boolean {
        val nome = binding.etNome.text.toString()
        val email = binding.etEmail.text.toString()
        val senha = binding.etSenha.text.toString()
        val confirmarSenha = binding.etConfirmarSenha.text.toString()

        val verificaNome = nome.length > 6
        val verificaEmail = isValidEmail(email)
        val verificaSenha = isValidPassword(senha)
        val senhaIgual = senha == confirmarSenha

        if (verificaNome && verificaEmail && verificaSenha && senhaIgual) {
            return true
        } else {
            if (!verificaNome) {
                binding.etNome.error = "Nome deve ter mais de 6 caracteres"
            }
            if (!verificaEmail) {
                binding.etEmail.error = "Email inválido"
            }
            if (!verificaSenha) {
                binding.etSenha.error = "Senha inválida: deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula e uma minúscula."
            }
            if (!senhaIgual) {
                binding.etConfirmarSenha.error = "As senhas não coincidem"
            }
            return false
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        return Pattern.compile(passwordPattern).matcher(password).matches()
    }
}
