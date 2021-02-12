package com.example.aplicacionandroidfinal.Modelos

import java.io.Serializable

class Usuarios:Serializable {
    var nombre: String? = ""
    var correo: String? = ""
    var apellidos: String = ""
    var tipo: String? = ""
    var userId: String? = ""
    var imagen: String? = ""
    var listaCursos = arrayListOf<Cursos>()

    constructor(
        nombre: String?,
        correo: String?,
        apellildos: String,
        tipo: String?,
        userId: String?,
        imagen: String?
    ) {
        this.nombre = nombre
        this.correo = correo
        this.apellidos = apellildos
        this.tipo = tipo
        this.userId = userId
        this.imagen = imagen


    }

    constructor()




}