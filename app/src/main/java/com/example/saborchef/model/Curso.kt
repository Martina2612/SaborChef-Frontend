package com.example.saborchef.model

data class Curso(
    val idCurso: Long,
    val nombre: String,
    val descripcion: String,
    val contenidos: String,
    val requerimientos: String,
    val duracion: String,
    val precio: Double,
    val modalidad: String,
    val imagenUrl:String,
    val nivel:Nivel,
    val chef:String,
    val cronogramas: List<Cronograma>
)

data class Cronograma(
    val fechaInicio: String,
    val fechaFin: String,
    val vacantesDisponibles: Int
)

