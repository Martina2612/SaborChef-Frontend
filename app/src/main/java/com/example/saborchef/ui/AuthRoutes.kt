package com.example.saborchef.ui

sealed class AuthRoutes(val route: String) {
    object Login : AuthRoutes("login")
    object PasswordRecovery : AuthRoutes("password_recovery")
    object PasswordCode : AuthRoutes("password_code")
    object PasswordNew : AuthRoutes("password_new")
    object PasswordSuccess : AuthRoutes("password_success")
}
