package com.vaultspec

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var vaultList: RecyclerView
    private lateinit var addItemFab: ExtendedFloatingActionButton
    private lateinit var adapter: VaultAdapter
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        
        vaultList = findViewById(R.id.vaultList)
        addItemFab = findViewById(R.id.addItemFab)
        
        setupRecyclerView()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = VaultAdapter()
        vaultList.layoutManager = LinearLayoutManager(this)
        vaultList.adapter = adapter
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        vaultList.layoutAnimation = animation
    }

    private fun setupFab() {
        addItemFab.setOnClickListener {
            showItemTypeDialog()
        }
    }

    private fun showItemTypeDialog() {
        val items = arrayOf("Login", "Credit Card", "Note")
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Item")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showLoginDialog()
                    1 -> showCreditCardDialog()
                    2 -> showNoteDialog()
                }
            }
            .show()
    }

    private fun showLoginDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_login, null)
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Login")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogView.findViewById<TextInputEditText>(R.id.titleInput).text.toString()
                val username = dialogView.findViewById<TextInputEditText>(R.id.usernameInput).text.toString()
                val password = dialogView.findViewById<TextInputEditText>(R.id.passwordInput).text.toString()
                adapter.addItem(VaultItem.Login(title, username, password))
                vaultList.scheduleLayoutAnimation()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreditCardDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_card, null)
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Credit Card")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogView.findViewById<TextInputEditText>(R.id.titleInput).text.toString()
                val cardNumber = dialogView.findViewById<TextInputEditText>(R.id.cardNumberInput).text.toString()
                val expiryDate = dialogView.findViewById<TextInputEditText>(R.id.expiryInput).text.toString()
                val cvv = dialogView.findViewById<TextInputEditText>(R.id.cvvInput).text.toString()
                adapter.addItem(VaultItem.CreditCard(title, cardNumber, expiryDate, cvv))
                vaultList.scheduleLayoutAnimation()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNoteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null)
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Note")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogView.findViewById<TextInputEditText>(R.id.titleInput).text.toString()
                val content = dialogView.findViewById<TextInputEditText>(R.id.contentInput).text.toString()
                adapter.addItem(VaultItem.Note(title, content))
                vaultList.scheduleLayoutAnimation()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                // TODO: Implement search
                true
            }
            R.id.settings -> {
                // TODO: Implement settings
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}