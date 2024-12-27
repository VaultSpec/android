package com.vaultspec

sealed class VaultItem {
    data class Login(
        val title: String,
        val username: String,
        val password: String
    ) : VaultItem()

    data class CreditCard(
        val title: String,
        val cardNumber: String,
        val expiryDate: String,
        val cvv: String
    ) : VaultItem()

    data class Note(
        val title: String,
        val content: String
    ) : VaultItem()
}