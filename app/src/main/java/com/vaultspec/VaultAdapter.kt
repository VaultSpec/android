package com.vaultspec

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VaultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<VaultItem>()

    class LoginViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val usernameText: TextView = view.findViewById(R.id.usernameText)
        val passwordText: TextView = view.findViewById(R.id.passwordText)
    }

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val cardNumberText: TextView = view.findViewById(R.id.cardNumberText)
        val expiryText: TextView = view.findViewById(R.id.expiryText)
        val cvvText: TextView = view.findViewById(R.id.cvvText)
    }

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val contentText: TextView = view.findViewById(R.id.contentText)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is VaultItem.Login -> VIEW_TYPE_LOGIN
            is VaultItem.CreditCard -> VIEW_TYPE_CARD
            is VaultItem.Note -> VIEW_TYPE_NOTE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOGIN -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_login, parent, false)
                LoginViewHolder(view)
            }
            VIEW_TYPE_CARD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card, parent, false)
                CardViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_note, parent, false)
                NoteViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is VaultItem.Login -> {
                val loginHolder = holder as LoginViewHolder
                loginHolder.titleText.text = item.title
                loginHolder.usernameText.text = item.username
                loginHolder.passwordText.text = item.password
            }
            is VaultItem.CreditCard -> {
                val cardHolder = holder as CardViewHolder
                cardHolder.titleText.text = item.title
                cardHolder.cardNumberText.text = item.cardNumber
                cardHolder.expiryText.text = item.expiryDate
                cardHolder.cvvText.text = item.cvv
            }
            is VaultItem.Note -> {
                val noteHolder = holder as NoteViewHolder
                noteHolder.titleText.text = item.title
                noteHolder.contentText.text = item.content
            }
        }
    }

    override fun getItemCount() = items.size

    fun addItem(item: VaultItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    companion object {
        private const val VIEW_TYPE_LOGIN = 0
        private const val VIEW_TYPE_CARD = 1
        private const val VIEW_TYPE_NOTE = 2
    }
}