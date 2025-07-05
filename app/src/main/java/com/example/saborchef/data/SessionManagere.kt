package com.example.saborchef.data

object SessionManager {
    var token: String? = null
    var userId: Long? = null
    var userRole: String? = null
    var userEmail: String? = null
    var userAlias: String? = null

    fun clearSession() {
        token = null
        userId = null
        userRole = null
        userEmail = null
        userAlias = null
    }
}