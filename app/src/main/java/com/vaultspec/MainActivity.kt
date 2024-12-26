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
    private lateinit var passwordList: RecyclerView
    private lateinit var addPasswordFab: ExtendedFloatingActionButton
    private lateinit var adapter: PasswordAdapter
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        
        passwordList = findViewById(R.id.passwordList)
        addPasswordFab = findViewById(R.id.addPasswordFab)
        
        setupRecyclerView()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = PasswordAdapter()
        passwordList.layoutManager = LinearLayoutManager(this)
        passwordList.adapter = adapter
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        passwordList.layoutAnimation = animation
    }

    private fun setupFab() {
        addPasswordFab.setOnClickListener {
            showAddPasswordDialog()
        }
    }

    private fun showAddPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_password, null)
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Password")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogView.findViewById<TextInputEditText>(R.id.titleInput).text.toString()
                val username = dialogView.findViewById<TextInputEditText>(R.id.usernameInput).text.toString()
                val password = dialogView.findViewById<TextInputEditText>(R.id.passwordInput).text.toString()
                
                adapter.addItem(PasswordItem(title, username, password))
                passwordList.scheduleLayoutAnimation()
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