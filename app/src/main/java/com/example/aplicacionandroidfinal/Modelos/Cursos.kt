package com.example.aplicacionandroidfinal.Modelos

import java.io.Serializable

class Cursos:Serializable {
    var nombreCurso: String=""
    var descripcion: String=""
    var uidUsuario: String=""
    var imagenCurso: String=""


    constructor(nombreCurso: String,descripcion: String,uidUsuario: String,imagenCurso: String  ) {
        this.nombreCurso = nombreCurso
        this.descripcion=descripcion
        this.uidUsuario=uidUsuario
        this.imagenCurso=imagenCurso

    }
    constructor()
}