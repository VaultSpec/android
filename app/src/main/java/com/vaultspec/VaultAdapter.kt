package com.vaultspec

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VaultAdapter(
    private var items: List<VaultItem> = emptyList(),
    private val onItemClick: (VaultItem) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_LOGIN = 0
        private const val VIEW_TYPE_CARD = 1
        private const val VIEW_TYPE_NOTE = 2
        private const val VIEW_TYPE_IDENTITY = 3
    }

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

    class IdentityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val fullNameText: TextView = view.findViewById(R.id.fullNameText)
        val emailText: TextView = view.findViewById(R.id.emailText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOGIN -> LoginViewHolder(inflater.inflate(R.layout.item_login, parent, false))
            VIEW_TYPE_CARD -> CardViewHolder(inflater.inflate(R.layout.item_card, parent, false))
            VIEW_TYPE_NOTE -> NoteViewHolder(inflater.inflate(R.layout.item_note, parent, false))
            VIEW_TYPE_IDENTITY -> IdentityViewHolder(inflater.inflate(R.layout.item_identity, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { onItemClick(item) }
        
        when (holder) {
            is LoginViewHolder -> bindLoginViewHolder(holder, item as VaultItem.Login)
            is CardViewHolder -> bindCardViewHolder(holder, item as VaultItem.Card)
            is NoteViewHolder -> bindNoteViewHolder(holder, item as VaultItem.Note)
            is IdentityViewHolder -> bindIdentityViewHolder(holder, item as VaultItem.Identity)
        }
    }

    private fun bindLoginViewHolder(holder: LoginViewHolder, item: VaultItem.Login) {
        holder.titleText.text = item.title
        holder.usernameText.text = item.username
        holder.passwordText.text = "••••••••"
    }

    private fun bindCardViewHolder(holder: CardViewHolder, item: VaultItem.Card) {
        holder.titleText.text = item.title
        holder.cardNumberText.text = "****${item.cardNumber.takeLast(4)}"
        holder.expiryText.text = "${item.expiryMonth}/${item.expiryYear}"
        holder.cvvText.text = "***"
    }

    private fun bindNoteViewHolder(holder: NoteViewHolder, item: VaultItem.Note) {
        holder.titleText.text = item.title
        holder.contentText.text = item.content
    }

    private fun bindIdentityViewHolder(holder: IdentityViewHolder, item: VaultItem.Identity) {
        holder.titleText.text = item.title
        holder.fullNameText.text = "${item.firstName} ${item.lastName}"
        holder.emailText.text = item.email
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when (items[position]) {
        is VaultItem.Login -> VIEW_TYPE_LOGIN
        is VaultItem.Card -> VIEW_TYPE_CARD
        is VaultItem.Note -> VIEW_TYPE_NOTE
        is VaultItem.Identity -> VIEW_TYPE_IDENTITY
    }

    fun updateItems(newItems: List<VaultItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}