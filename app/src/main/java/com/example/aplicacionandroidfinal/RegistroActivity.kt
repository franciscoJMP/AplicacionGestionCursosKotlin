package com.example.aplicacionandroidfinal

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.example.aplicacionandroidfinal.Modelos.Cursos
import com.example.aplicacionandroidfinal.Modelos.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registro.*

class RegistroActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        sesion()
        setup()
    }

    private fun sesion() {
        val prefs: SharedPreferences =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email != null) {
            showHome()
        }

    }

    private fun setup() {
        reguser.setOnClickListener {
            if (et_regemail.text.isNotEmpty() && et_regpass.text.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(
                    et_regemail.text.toString(),
                    et_regpass.text.toString()
                )
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            crearUsuario()
                            showOkMessage("Registro", "Usuario registrado con exito")
                            persistencia()
                            sesion()
                            showHome()
                        } else {
                            showFailMessage("Error", "Error al registrar este usuario")
                        }
                    }
            }
        }
    }
    private fun persistencia() {
        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", et_regemail.text.toString())
        prefs.apply()
    }

    private fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showFailMessage(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun showOkMessage(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
            .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            })

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }
    private fun crearUsuario() {
        val nombre = et_regnombre.text.toString()
        val email = et_regemail.text.toString()
        val uid=firebaseAuth.currentUser?.uid
        val imagen = "https://firebasestorage.googleapis.com/v0/b/fir-kotlin-f25f2.appspot.com/o/fotos%2Fdefaultuser.jpg?alt=media&token=47c1d55c-ade7-41f5-bb1d-40da38a43990"
        val user = Usuarios(nombre,email,et_regapellidos.text.toString(),"Alumno",firebaseAuth.currentUser?.uid,imagen)
        user.listaCursos=ArrayList<Cursos>()
        if(uid!=null){
            database.child("Usuarios").child(uid).setValue(user)

        }


    }
}