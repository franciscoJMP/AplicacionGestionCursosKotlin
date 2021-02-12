package com.example.aplicacionandroidfinal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplicacionandroidfinal.Modelos.Cursos

import kotlinx.android.synthetic.main.item_cursos.view.*
import java.lang.IllegalArgumentException

class AdaptadorCursos(val context: Context, val listaCursos: List<Cursos>, val numero: Int):RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return CursosViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cursos,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (holder is CursosViewHolder)
            holder.bind(listaCursos[position],position)
        else
            throw IllegalArgumentException("Error viewHolder erroneo")
    }
    override fun getItemCount(): Int =  listaCursos.size
    inner class CursosViewHolder(itemView: View):BaseViewHolder<Cursos>(itemView){

        override fun bind(item: Cursos, position: Int) {
            Glide.with(context).load(item.imagenCurso).into(itemView.img_cursos)
            itemView.item_nombreCurso.text=item.nombreCurso
            itemView.rows.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("curso",item)
                if(numero.equals(1)){
                    Navigation.findNavController(it).navigate(R.id.action_navigation_dashboard_to_datosCursosFragment,bundle)
                }else{
                    Navigation.findNavController(it).navigate(R.id.action_navigation_home_to_infoMisCursos,bundle)
                }

            }





        }
    }
}