package com.example.aplicacionandroidfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.aplicacionandroidfinal.Modelos.Cursos
import com.example.aplicacionandroidfinal.Modelos.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_infocursos.*


class DatosCursosFragment : Fragment() {
    private lateinit var cursos: Cursos
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        cursos = Cursos()
        val bundle = this.arguments
        if (bundle != null) {
            cursos = bundle.getSerializable("curso") as Cursos
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_infocursos, container, false)
        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(view).load(cursos.imagenCurso).into(imginfocursos)
        infocurname.setText(cursos.nombreCurso)
        infocurdesc.setText(cursos.descripcion)

        if (cursos.uidUsuario.equals(firebaseAuth.currentUser?.uid)) {
            bt_matricular.setText("Volver")
        }
        infouserCurso()
        matricular()

    }

    private fun infouserCurso() {
        val postListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue(Usuarios::class.java)
                infousercurso.text = "Creado por: " + user!!.nombre + " " + user!!.apellidos

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        database.child("Usuarios").child(cursos.uidUsuario).addValueEventListener(postListener)

    }

    private fun matricular() {
        var texto = bt_matricular.text.toString()
        bt_matricular.setOnClickListener {
            if (!texto.equals("Volver")) {
                var uid = firebaseAuth.currentUser?.uid
                if (uid != null) {
                    database.child("Usuarios").child(uid).child("listacursos")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val listaCursos: ArrayList<Cursos> = ArrayList<Cursos>()
                                listaCursos.clear()
                                for (xCurso in snapshot.children) {
                                    val curso = xCurso.getValue(Cursos::class.java)!!
                                    listaCursos.add(curso)
                                }
                                val cursosc: Cursos = cursos
                                listaCursos.add(cursosc)

                                database.child("Usuarios").child(uid).child("listacursos")
                                    .setValue(listaCursos)
                                Toast.makeText(
                                    activity,
                                    "Matriculado en el curso " + cursosc.nombreCurso,
                                    Toast.LENGTH_SHORT
                                ).show()
                               Navigation.findNavController(it).navigate(R.id.action_datosCursosFragment_to_navigation_dashboard)


                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })

                }
            } else {
                Navigation.findNavController(it).navigate(R.id.action_datosCursosFragment_to_navigation_dashboard)

            }

        }

    }
}


