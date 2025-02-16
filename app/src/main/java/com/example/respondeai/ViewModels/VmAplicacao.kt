package com.example.respondeai.ViewModels

import android.app.Application
import androidx.lifecycle.ViewModelProvider

class VmAplicacao: Application() {
    lateinit var viewModel: VmUsuarios
    override fun onCreate() {
        super.onCreate()
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(VmUsuarios::class.java)
    }

}

