package com.example.saborchef.model

/**
 * Este DTO debe coincidir exactamente con lo que espera el controlador Spring Boot.
 * Ej.: en Java tenemos algo como:
 *   public class RegisterRequest {
 *       private String nombre;
 *       private String apellido;
 *       private String alias;
 *       private String email;
 *       private String password;
 *       private Rol role;
 *       // getters / setters ...
 *   }
 *
 * En Kotlin guardamos:
 */
data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val alias: String,
    val email: String,
    val password: String,
    val role: Rol   // Usa el enum Rol definido más abajo.
)
