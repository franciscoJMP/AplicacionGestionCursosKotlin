package com.example.aplicacionandroidfinal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.aplicacionandroidfinal.Modelos.Cursos
import com.example.aplicacionandroidfinal.Modelos.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_crear_curso.*





class CrearCursoFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val File = 1
    private lateinit var curso: Cursos
    private lateinit var usuarios: Usuarios
    private var imagenCurso:String="https://firebasestorage.googleapis.com/v0/b/fir-kotlin-f25f2.appspot.com/o/fotos%2Fdefaultuser.jpg?alt=media&token=47c1d55c-ade7-41f5-bb1d-40da38a43990"
    private lateinit var vistaGeneral:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        curso = Cursos()
        usuarios = Usuarios()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_crear_curso, container, false)
        vistaGeneral=view


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(activity, usuarios.nombre, Toast.LENGTH_SHORT).show()
        setup()

    }

    private fun setup() {
        bt_crearCurso.setOnClickListener {
            var uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                database.child("Usuarios").child(uid).child("listacursos")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val listaCursos: ArrayList<Cursos> = ArrayList<Cursos>()
                            listaCursos.clear()
                            for (xCurso in snapshot.children) {
                                curso = xCurso.getValue(Cursos::class.java)!!

                                listaCursos.add(curso)


                            }
                            val cursosc: Cursos= Cursos(nombreCurso.text.toString(),descCurso.text.toString(),uid,imagenCurso)
                            listaCursos.add(cursosc)
                            database.child("Cursos").push().setValue(cursosc)
                            database.child("Usuarios").child(uid).child("listacursos").setValue(listaCursos)
                            Toast.makeText(activity,"Curso creado",Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(it).navigate(R.id.action_crearCursoFragment_to_navigation_dashboard)


                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

            }


        }
        botonImagenCurso.setOnClickListener {
            uploadImg()

        }

    }
    private fun uploadImg() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type="*/*"
        startActivityForResult(intent,File)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==File && resultCode== Activity.RESULT_OK){
            val fileUri= data!!.data
            val folder: StorageReference = FirebaseStorage.getInstance().getReference().child("fotos")
            val file_name: StorageReference = folder.child("imagen"+fileUri!!.lastPathSegment)
            file_name.putFile(fileUri).addOnCompleteListener { taskSnapshot->
                file_name.downloadUrl.addOnSuccessListener { uri->
                    imagenCurso=uri.toString()
                    Glide.with(vistaGeneral).load(imagenCurso).into(imageCur)

                }

            }
        }
    }


}