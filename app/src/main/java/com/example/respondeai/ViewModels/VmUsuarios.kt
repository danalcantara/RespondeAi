package com.example.respondeai.ViewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.respondeai.Model.Usuario
import com.google.firebase.firestore.FirebaseFirestore

class  VmUsuarios(application: Application) : AndroidViewModel(application)  {

    private val firestore = FirebaseFirestore.getInstance()

    // LiveData para armazenar os dados do usuário
    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> get() = _usuario

    // Carregar dados do usuário do Firestore
    fun carregarUsuario(userId: String, finalize: (Boolean) -> Unit) {
        firestore.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    _usuario.value = usuario
                    finalize(true)
                }

            }
            .addOnFailureListener {
                _usuario.value = null // Indica erro ao buscar os dados
                finalize(false)
            }
    }

    // Atualizar pontuação do usuário no Firestore
    fun atualizarPontuacao(userId: String, novaPontuacao: String, atuPont: (Boolean)-> Unit) {
        // Convertendo pontuação existente e nova pontuação de String para Int
        val pontuacaoAtual = _usuario.value?.pontuacao?.toIntOrNull() ?: 0 // Default para 0 caso não consiga converter
        val novaPontuacaoInt = novaPontuacao.toIntOrNull() ?: 0 // Default para 0 caso a conversão falhe

        if (pontuacaoAtual != null && novaPontuacaoInt != null) {
            val pontuacaoFinal = pontuacaoAtual + novaPontuacaoInt
            Log.i("atualiza", "Atualizando pontuação para: $pontuacaoFinal")

            // Atualizando no Firestore
            firestore.collection("usuarios").document(userId)
                .update("pontuacao", pontuacaoFinal.toString())
                .addOnSuccessListener {
                    // Atualiza o LiveData com a nova pontuação
                    _usuario.value = _usuario.value?.copy(pontuacao = pontuacaoFinal.toString())
                    carregarUsuario(userId){
                            carregado ->
                        if (carregado != null){
                            atuPont(true)
                        }
                    }
                }
                .addOnFailureListener {
                    // Tratar erro ao atualizar a pontuação
                    Log.e("atualiza", "Erro ao atualizar a pontuação", it)
                    atuPont(false)
                }
        }
    }
    fun limparDados() {
        _usuario.value = null
    }
}
