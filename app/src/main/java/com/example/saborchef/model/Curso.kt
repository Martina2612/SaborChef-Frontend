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
    val idCronograma:Long,
    val fechaInicio: String,
    val fechaFin: String,
    val vacantesDisponibles: Int,
    val sede: Sede
)

data class Sede(
    val idSede: Long,
    val nombreSede: String,
    val direccionSede: String,
    val telefonoSede: String,
    val mailSede: String,
    val whatsapp: String,
    val tipoBonificacion: String,
    val bonificaCursos: Boolean,
    val tipoPromocion: String,
    val promocionCursos: String,
    val imagenUrl: String
)

data class CursoInscripto(
    val idCurso: Long,
    val nombreCurso: String,
    val descripcion: String,
    val modalidad: String,
    val imagenUrl: String,
    val nivel: String,
    val precio: Double,
    val duracion: String,
    val chef: String,
    val fechaInicio: String,
    val fechaFin: String,
    val sede: Sede,
    val progreso: Float,
    val finalizado:Boolean
)


