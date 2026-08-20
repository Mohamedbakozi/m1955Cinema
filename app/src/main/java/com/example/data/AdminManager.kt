package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdminManager {
    // Master PINs that unlock Admin Mode
    private val validAdminPins = setOf("Nma1955", "B1955", "admin2000", "adminA", "2002", "2025")

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _adminName = MutableStateFlow("Admin M1955")
    val adminName: StateFlow<String> = _adminName.asStateFlow()

    fun verifyAndLogin(enteredPin: String, adminName: String = "Admin M1955"): Boolean {
        val trimmed = enteredPin.trim()
        if (validAdminPins.contains(trimmed)) {
            _isAdmin.value = true
            _adminName.value = if (adminName.isNotBlank()) adminName else "Admin M1955"
            return true
        }
        return false
    }

    fun logout() {
        _isAdmin.value = false
    }

    fun setAdminStatus(admin: Boolean) {
        _isAdmin.value = admin
    }
}
