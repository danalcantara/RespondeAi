package com.example.respondeai

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.respondeai.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    val auth_instance by lazy {
        FirebaseAuth.getInstance()
    }
   val binding by lazy {
       ActivityMainBinding.inflate(layoutInflater)
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

    override fun onResume() {
        super.onResume()
        if (auth_instance.currentUser?.uid != null){
            startActivity(Intent(this, TelaCarregamento::class.java))
        }
    }
    fun navegarParaRegistro(view:View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }
    fun navegarParaPaginaInicialLogin(view:View){
        loginUser()
    }
    fun loginUser() {
        if (verificaCampos()) {
            auth_instance.signInWithEmailAndPassword(
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString()
            ).addOnSuccessListener {
                Toast.makeText(this, "Logado com Sucesso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, TelaCarregamento::class.java))
            }.addOnFailureListener {
                Toast.makeText(this, "Login Falhou", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Erro no Preenchimento dos Dados", Toast.LENGTH_SHORT).show()
            
        }
    }

    private fun verificaCampos(): Boolean {
        var verificaEmail = isValidEmail(binding.editTextEmail.text.toString())
        var verificaSenha = isValidPassword(binding.editTextPassword.text.toString())
        if ( verificaEmail && verificaSenha) {
            Toast.makeText(this, "E-mail e senha válidos!", Toast.LENGTH_SHORT).show()
            return true

        } else {
            if (!verificaEmail){
                binding.editTextEmail.error = "Email Invalido"
            }
            if (!verificaSenha){
                    binding.editTextPassword.error = """
                        Senha Invalida
                        
                        Senha deve ter mais que 8 digitos
                        senha deve ter uma Letra maiuscula e uma minuscula
                        
                        """.trimIndent()
                }
            }
            return false
        }
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        return Pattern.compile(passwordPattern).matcher(password).matches()
    }

}

