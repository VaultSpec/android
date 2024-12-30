package com.vaultspec

import java.util.Date
import java.util.UUID

sealed class VaultItem {
    abstract val id: String
    abstract val title: String
    abstract val createdAt: Date
    abstract val lastModified: Date
    abstract val favorite: Boolean

    data class Login(
        override val id: String = UUID.randomUUID().toString(),
        override val title: String,
        override val createdAt: Date = Date(),
        override val lastModified: Date = Date(),
        override val favorite: Boolean = false,
        val username: String,
        val password: String,
        val website: String? = null,
        val notes: String? = null
    ) : VaultItem()

    data class Card(
        override val id: String = UUID.randomUUID().toString(),
        override val title: String,
        override val createdAt: Date = Date(),
        override val lastModified: Date = Date(),
        override val favorite: Boolean = false,
        val cardHolder: String,
        val cardNumber: String,
        val expiryMonth: Int,
        val expiryYear: Int,
        val cvv: String,
        val type: CardType = CardType.OTHER
    ) : VaultItem()

    data class Note(
        override val id: String = UUID.randomUUID().toString(),
        override val title: String,
        override val createdAt: Date = Date(),
        override val lastModified: Date = Date(),
        override val favorite: Boolean = false,
        val content: String
    ) : VaultItem()

    data class Identity(
        override val id: String = UUID.randomUUID().toString(),
        override val title: String,
        override val createdAt: Date = Date(),
        override val lastModified: Date = Date(),
        override val favorite: Boolean = false,
        val firstName: String,
        val lastName: String,
        val email: String,
        val phone: String? = null,
        val address: String? = null
    ) : VaultItem()
}

enum class CardType {
    VISA,
    MASTERCARD,
    AMEX,
    OTHER
}