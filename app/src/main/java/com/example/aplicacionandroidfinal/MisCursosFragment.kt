package com.example.aplicacionandroidfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionandroidfinal.Modelos.Cursos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception


class MisCursosFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_miscursos.layoutManager= LinearLayoutManager(context)
        rv_miscursos.addItemDecoration((DividerItemDecoration(context, DividerItemDecoration.VERTICAL)))
        var uid = firebaseAuth.currentUser?.uid
        database.child("Usuarios").child(uid!!).child("listacursos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaCursos: ArrayList<Cursos> = ArrayList<Cursos>()
                listaCursos.clear()
                for (xCurso in snapshot.children) {
                    var curso = xCurso.getValue(Cursos::class.java)!!
                        listaCursos.add(curso)
                }

                try{
                    rv_miscursos.adapter = AdaptadorCursos(context!!, listaCursos,0)
                }catch (e: Exception){

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}