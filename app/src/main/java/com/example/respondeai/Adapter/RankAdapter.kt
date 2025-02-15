package com.example.respondeai.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.respondeai.Model.Usuario
import com.example.respondeai.R
import com.example.respondeai.databinding.ActivityRankBinding
import com.example.respondeai.databinding.ItemRankBinding

class RankAdapter(val listaUsuarios: List<Usuario>): Adapter<RankAdapter.RankAdapterViewHolder>() {

    var numberRank = 0
    inner class RankAdapterViewHolder(val view: ItemRankBinding): ViewHolder(view.root){
        fun bind(usuario: Usuario, position: Int){
            view.nome.text = usuario.nome
            view.Pontuacao.text = usuario.Pontuacao.toString()
            view.rank.text = (position + 1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemInflado = ItemRankBinding.inflate(
            inflater, parent, false
        )
        return RankAdapterViewHolder(itemInflado)
    }


    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: RankAdapterViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        numberRank += 1
        holder.bind(usuario, position)
    }
}