package com.example.aplicacionandroidfinal

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.aplicacionandroidfinal.Modelos.Cursos
import com.example.aplicacionandroidfinal.Modelos.Usuarios
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    val SINGINGOOGLE: Int = 1


    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        configurarAccesoGoogle()
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sesion()
        setup()
    }

    private fun sesion() {
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        val email = prefs.getString("email", null)
        if (email != null) {
            showHome()
        }

    }



    private fun configurarAccesoGoogle() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setup() {
        registrar.setOnClickListener {
            val i = Intent(this, RegistroActivity::class.java)
            startActivity(i)

        }
        acceder.setOnClickListener {
            if (et_email.text.isNotEmpty() && et_password.text.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(
                   et_email.text.toString(),et_password.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showOkMessage("Acceso", "Acceso correcto")
                        persistencia()
                        sesion()
                        showHome()
                    } else {
                        showFailMessage("Error", "Error al acceder, compruebe que este usuario este registrado y que la contraseña sea correcta")
                    }
                }
            }
        }
        accederGoogle.setOnClickListener {
            mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
            mGoogleSignInClient.signOut()
            val intentGoogle: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(intentGoogle, SINGINGOOGLE)
        }
    }




    private fun firebaseAutConGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {

            if (it.isSuccessful) {
                persistenciaGoogle()
                comprobarUsuarioDB()
            } else {
                showFailMessage("Error", "Error de acceso")
            }
        }
    }

    private fun comprobarUsuarioDB() {
        var uid = firebaseAuth.currentUser?.uid
        if(uid!=null){
            database.child("Usuarios").child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        showOkMessage("Acceso", "Acceso con usuario de google correcto")
                    }else{
                        crearUsuario()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


    }
    private fun persistencia() {
        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", et_email.text.toString())
        prefs.apply()
    }
    private fun persistenciaGoogle() {
        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", firebaseAuth.currentUser?.email)
        prefs.apply()
    }

    private fun crearUsuario() {
        val nombre = firebaseAuth.currentUser?.displayName
        val email = firebaseAuth.currentUser?.email
        val uid=firebaseAuth.currentUser?.uid
        val user = Usuarios(nombre,email,"","Alumno",firebaseAuth.currentUser?.uid,firebaseAuth.currentUser?.photoUrl.toString())
        user.listaCursos=ArrayList<Cursos>()
        if(uid!=null){
            database.child("Usuarios").child(uid).setValue(user)
            showHome()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SINGINGOOGLE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                firebaseAutConGoogle(cuenta)

            } catch (e: ApiException) {
                showFailMessage("Error", "Error al conectar con su usuario de Google")
            }
        }
    }

    private fun showFailMessage(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }
    private fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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


    override fun onBackPressed() {

    }
}