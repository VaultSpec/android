package com.vaultspec

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PasswordAdapter : RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {
    private val items = mutableListOf<PasswordItem>()

    class PasswordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val usernameText: TextView = view.findViewById(R.id.usernameText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title
        holder.usernameText.text = item.username
    }

    override fun getItemCount() = items.size

    fun addItem(item: PasswordItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
}