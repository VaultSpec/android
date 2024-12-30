package com.vaultspec

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.LayoutInflater
import androidx.core.view.GravityCompat
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import java.util.Date
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var vaultList: RecyclerView
    private lateinit var addItemFab: ExtendedFloatingActionButton
    private lateinit var adapter: VaultAdapter
    private lateinit var toolbar: MaterialToolbar
    
    private val allItems = mutableListOf<VaultItem>()
    private val filteredItems = mutableListOf<VaultItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        setupViews()
        setupNavigation()
        setupRecyclerView()
        setupFab()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return true
    }

    private fun setupViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        drawerLayout = findViewById(R.id.drawer)
        navigationView = findViewById(R.id.navView)
        vaultList = findViewById(R.id.recyclerView)
        addItemFab = findViewById(R.id.fab)
    }

   private fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_all -> filterVaultItems("all")
                R.id.nav_passwords -> filterVaultItems("passwords")
                R.id.nav_cards -> filterVaultItems("cards")
                R.id.nav_notes -> filterVaultItems("notes")
                R.id.nav_identities -> filterVaultItems("identities")
                R.id.nav_settings -> openSettings()
                R.id.nav_about -> openAbout()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupRecyclerView() {
        adapter = VaultAdapter(filteredItems) { item ->
            showItemDetails(item)
        }
        
        vaultList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

   private fun showItemDetails(item: VaultItem) {
        MaterialAlertDialogBuilder(this)
            .setTitle(item.title)
            .setMessage(when(item) {
                is VaultItem.Login -> "Username: ${item.username}\nPassword: ••••••••"
                is VaultItem.Card -> "Card ending in: ****${item.cardNumber.takeLast(4)}\nExpiry: ${item.expiryMonth}/${item.expiryYear}"
                is VaultItem.Note -> item.content
                is VaultItem.Identity -> "${item.firstName} ${item.lastName}\nEmail: ${item.email}"
            })
            .setPositiveButton(R.string.edit) { _, _ ->
                when(item) {
                    is VaultItem.Login -> showLoginDialog(item)
                    is VaultItem.Card -> showCardDialog(item)
                    is VaultItem.Note -> showNoteDialog(item)
                    is VaultItem.Identity -> showIdentityDialog(item)
                }
            }
            .setNeutralButton(R.string.copy) { _, _ ->
                val clipboardText = when(item) {
                    is VaultItem.Login -> item.password
                    is VaultItem.Card -> item.cardNumber
                    is VaultItem.Note -> item.content
                    is VaultItem.Identity -> "${item.firstName} ${item.lastName}"
                }
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Vault Item", clipboardText)
                clipboard.setPrimaryClip(clip)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun setupFab() {
        addItemFab.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun showAddItemDialog() {
        val items = arrayOf("Login", "Card", "Note", "Identity")
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_add_title)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showLoginDialog()
                    1 -> showCardDialog()
                    2 -> showNoteDialog()
                    3 -> showIdentityDialog()
                }
            }
            .show()
    }

    private fun showLoginDialog() {
    showLoginDialog(null)
}

private fun showLoginDialog(existingItem: VaultItem.Login?) {
    val dialogView = LayoutInflater.from(this)
        .inflate(R.layout.dialog_add_login, null)
        
    val titleInput = dialogView.findViewById<TextInputEditText>(R.id.titleInput)
    val usernameInput = dialogView.findViewById<TextInputEditText>(R.id.usernameInput)
    val passwordInput = dialogView.findViewById<TextInputEditText>(R.id.passwordInput)
    
    // Pre-populate fields if editing
    existingItem?.let {
        titleInput.setText(it.title)
        usernameInput.setText(it.username)
        passwordInput.setText(it.password)
    }

    MaterialAlertDialogBuilder(this)
        .setTitle(if (existingItem == null) R.string.dialog_add_title else R.string.dialog_edit_title)
        .setView(dialogView)
        .setPositiveButton(if (existingItem == null) R.string.add_item else R.string.save) { _, _ ->
            val newItem = VaultItem.Login(
                id = existingItem?.id ?: UUID.randomUUID().toString(),
                title = titleInput.text.toString(),
                username = usernameInput.text.toString(),
                password = passwordInput.text.toString(),
                website = existingItem?.website,
                notes = existingItem?.notes,
                createdAt = existingItem?.createdAt ?: Date(),
                lastModified = Date(),
                favorite = existingItem?.favorite ?: false
            )
            
            if (existingItem != null) {
                val index = allItems.indexOfFirst { it.id == existingItem.id }
                if (index != -1) {
                    allItems[index] = newItem
                }
            } else {
                allItems.add(newItem)
            }
            filterVaultItems("all")
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}

        private fun showCardDialog() {
        showCardDialog(null)
    }
    
    private fun showCardDialog(existingItem: VaultItem.Card?) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_card, null)
            
        val titleInput = dialogView.findViewById<TextInputEditText>(R.id.titleInput)
        val cardHolderInput = dialogView.findViewById<TextInputEditText>(R.id.cardHolderInput)
        val cardNumberInput = dialogView.findViewById<TextInputEditText>(R.id.cardNumberInput)
        val cvvInput = dialogView.findViewById<TextInputEditText>(R.id.cvvInput)
        
        existingItem?.let {
            titleInput.setText(it.title)
            cardHolderInput.setText(it.cardHolder)
            cardNumberInput.setText(it.cardNumber)
            cvvInput.setText(it.cvv)
        }
    
        MaterialAlertDialogBuilder(this)
            .setTitle(if (existingItem == null) R.string.dialog_add_title else R.string.dialog_edit_title)
            .setView(dialogView)
            .setPositiveButton(if (existingItem == null) R.string.add_item else R.string.save) { _, _ ->
                val newItem = VaultItem.Card(
                    id = existingItem?.id ?: UUID.randomUUID().toString(),
                    title = titleInput.text.toString(),
                    cardHolder = cardHolderInput.text.toString(),
                    cardNumber = cardNumberInput.text.toString(),
                    expiryMonth = 1, // TODO: Add expiry inputs
                    expiryYear = 2024,
                    cvv = cvvInput.text.toString(),
                    type = CardType.OTHER,
                    createdAt = existingItem?.createdAt ?: Date(),
                    lastModified = Date(),
                    favorite = existingItem?.favorite ?: false
                )
                
                if (existingItem != null) {
                    val index = allItems.indexOfFirst { it.id == existingItem.id }
                    if (index != -1) {
                        allItems[index] = newItem
                    }
                } else {
                    allItems.add(newItem)
                }
                filterVaultItems("all")
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

        private fun showNoteDialog() {
        showNoteDialog(null)
    }
    
    private fun showNoteDialog(existingItem: VaultItem.Note?) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_note, null)
            
        val titleInput = dialogView.findViewById<TextInputEditText>(R.id.titleInput)
        val contentInput = dialogView.findViewById<TextInputEditText>(R.id.contentInput)
        
        existingItem?.let {
            titleInput.setText(it.title)
            contentInput.setText(it.content)
        }
    
        MaterialAlertDialogBuilder(this)
            .setTitle(if (existingItem == null) R.string.dialog_add_title else R.string.dialog_edit_title)
            .setView(dialogView)
            .setPositiveButton(if (existingItem == null) R.string.add_item else R.string.save) { _, _ ->
                val newItem = VaultItem.Note(
                    id = existingItem?.id ?: UUID.randomUUID().toString(),
                    title = titleInput.text.toString(),
                    content = contentInput.text.toString(),
                    createdAt = existingItem?.createdAt ?: Date(),
                    lastModified = Date(),
                    favorite = existingItem?.favorite ?: false
                )
                
                if (existingItem != null) {
                    val index = allItems.indexOfFirst { it.id == existingItem.id }
                    if (index != -1) {
                        allItems[index] = newItem
                    }
                } else {
                    allItems.add(newItem)
                }
                filterVaultItems("all")
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

        private fun showIdentityDialog() {
        showIdentityDialog(null)
    }
    
    private fun showIdentityDialog(existingItem: VaultItem.Identity?) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_identity, null)
            
        val titleInput = dialogView.findViewById<TextInputEditText>(R.id.titleInput)
        val firstNameInput = dialogView.findViewById<TextInputEditText>(R.id.firstNameInput)
        val lastNameInput = dialogView.findViewById<TextInputEditText>(R.id.lastNameInput)
        val emailInput = dialogView.findViewById<TextInputEditText>(R.id.emailInput)
        
        existingItem?.let {
            titleInput.setText(it.title)
            firstNameInput.setText(it.firstName)
            lastNameInput.setText(it.lastName)
            emailInput.setText(it.email)
        }
    
        MaterialAlertDialogBuilder(this)
            .setTitle(if (existingItem == null) R.string.dialog_add_title else R.string.dialog_edit_title)
            .setView(dialogView)
            .setPositiveButton(if (existingItem == null) R.string.add_item else R.string.save) { _, _ ->
                val newItem = VaultItem.Identity(
                    id = existingItem?.id ?: UUID.randomUUID().toString(),
                    title = titleInput.text.toString(),
                    firstName = firstNameInput.text.toString(),
                    lastName = lastNameInput.text.toString(),
                    email = emailInput.text.toString(),
                    phone = existingItem?.phone,
                    address = existingItem?.address,
                    createdAt = existingItem?.createdAt ?: Date(),
                    lastModified = Date(),
                    favorite = existingItem?.favorite ?: false
                )
                
                if (existingItem != null) {
                    val index = allItems.indexOfFirst { it.id == existingItem.id }
                    if (index != -1) {
                        allItems[index] = newItem
                    }
                } else {
                    allItems.add(newItem)
                }
                filterVaultItems("all")
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun filterVaultItems(category: String) {
        filteredItems.clear()
        filteredItems.addAll(when (category) {
            "all" -> allItems
            "passwords" -> allItems.filterIsInstance<VaultItem.Login>()
            "cards" -> allItems.filterIsInstance<VaultItem.Card>()
            "notes" -> allItems.filterIsInstance<VaultItem.Note>()
            "identities" -> allItems.filterIsInstance<VaultItem.Identity>()
            else -> allItems
        })
        adapter.notifyDataSetChanged()
    }

    private fun openSettings() {
        // TODO: Launch settings activity
    }

    private fun openAbout() {
        // TODO: Show about dialog
    }

    private fun searchVaultItems(query: String) {
        val searchResults = allItems.filter { item ->
            when (item) {
                is VaultItem.Login -> 
                    item.title.contains(query, ignoreCase = true) ||
                    item.username.contains(query, ignoreCase = true)
                is VaultItem.Card -> 
                    item.title.contains(query, ignoreCase = true) ||
                    item.cardNumber.takeLast(4).contains(query)
                is VaultItem.Note -> 
                    item.title.contains(query, ignoreCase = true) ||
                    item.content.contains(query, ignoreCase = true)
                is VaultItem.Identity -> 
                    item.title.contains(query, ignoreCase = true) ||
                    "${item.firstName} ${item.lastName}".contains(query, ignoreCase = true)
            }
        }
        filteredItems.clear()
        filteredItems.addAll(searchResults)
        adapter.notifyDataSetChanged()
    }

    private fun resetSearch() {
        filteredItems.clear()
        filteredItems.addAll(allItems)
        adapter.notifyDataSetChanged()
    }
}