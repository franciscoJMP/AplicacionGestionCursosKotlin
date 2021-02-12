package com.example.aplicacionandroidfinal

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.aplicacionandroidfinal.Modelos.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.alert_dialgo_personalizado.*
import kotlinx.android.synthetic.main.alert_dialgo_personalizado.view.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.lang.Exception


class AjustesFragment : Fragment() {
    private val File = 1
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    var user:Usuarios? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val uid=firebaseAuth.currentUser?.uid
        val postListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                user = snapshot.getValue(Usuarios::class.java)
                infoUsuario.text=user!!.nombre+" "+user!!.apellidos


                    Glide.with(root).load(user!!.imagen).into(imageUser)
                }catch (e: Exception){

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        if(uid!=null){
            database.child("Usuarios").child(uid).addValueEventListener(postListener)
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }
    private fun setup(){
        cerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            val prefs: SharedPreferences.Editor? = this.requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            if (prefs != null) {
                prefs.clear()
                prefs.apply()
            }
            val i = Intent(activity,LoginActivity::class.java)
            startActivity(i)

        }
        subirImagen.setOnClickListener{
            uploadImg()
        }
        crearCurso.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_navigation_notifications_to_crearCursoFragment)
        }
        editInfoUsu.setOnClickListener{
            editarInfoUsuario()
        }
    }

    private fun editarInfoUsuario() {
        val miDialogo = LayoutInflater.from(context).inflate(R.layout.alert_dialgo_personalizado,null)
        val builder = activity?.let {
            AlertDialog.Builder(it).setView(miDialogo)

                .setPositiveButton("Modificar", DialogInterface.OnClickListener { dialog, id ->
                        var nombre = miDialogo.usernombre.text.toString()
                        var apellidos = miDialogo.userapellidos.text.toString()
                        modificarUsuario(nombre,apellidos)
                })
                .setNegativeButton("Cancelar",DialogInterface.OnClickListener{ dialog, id ->
                    dialog.cancel()

                })
        }

        val dialog: AlertDialog = builder!!.create()
        dialog.show()

    }

    private fun modificarUsuario(nombre: String, apellidos: String) {
        if(!nombre.equals("") && !apellidos.equals("")){
            val uid=firebaseAuth.currentUser?.uid
            if(uid!=null){
                database.child("Usuarios").child(uid).child("nombre").setValue(nombre)
                database.child("Usuarios").child(uid).child("apellidos").setValue(apellidos)
            }
        }


    }

    private fun uploadImg() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type="*/*"
        startActivityForResult(intent,File)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==File && resultCode==RESULT_OK){
            val fileUri= data!!.data
            val folder: StorageReference = FirebaseStorage.getInstance().getReference().child("fotos")
            val file_name: StorageReference = folder.child("imagen"+fileUri!!.lastPathSegment)
            file_name.putFile(fileUri).addOnCompleteListener { taskSnapshot->
                file_name.downloadUrl.addOnSuccessListener { uri->
                    val uid=firebaseAuth.currentUser?.uid
                    if(uid!=null){
                        database.child("Usuarios").child(uid).child("imagen").setValue(uri.toString())
                    }

                }

            }
        }
    }


}


