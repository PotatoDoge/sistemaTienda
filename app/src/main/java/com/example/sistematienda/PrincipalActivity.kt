package com.example.sistematienda

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView


class PrincipalActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))

        // related to the navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        val nav = findViewById<NavigationView>(R.id.navigation_view)
        nav.setNavigationItemSelectedListener(this)
        nav.menu.getItem(0).isChecked = true
        toggle.syncState()
    }

    /**
     * Method that manages if the nav drawer is opened or closed
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            moveTaskToBack(false); // evita que el usuario accidentalmente se salga de la sesión
        }
    }

    /**
     * Method that manages what happens when you click an item menu in the nav drawer
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.inicio -> Toast.makeText(this, "INICIO", Toast.LENGTH_SHORT).show()
            R.id.menu2 -> showProductsActions()
            R.id.menu3 -> Toast.makeText(this, "ITEM 3", Toast.LENGTH_SHORT).show()
            R.id.menu4 -> Toast.makeText(this, "ITEM 4", Toast.LENGTH_SHORT).show()
            R.id.menu5 -> Toast.makeText(this, "ITEM 5", Toast.LENGTH_SHORT).show()
            R.id.menu6 -> Toast.makeText(this, "ITEM 6", Toast.LENGTH_SHORT).show()
            R.id.exit -> signOut() // cerrar sesión
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        return true
    }

    /**
     * Método que muestra la alerta de cerrar sesión
     */
    private fun signOut(){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("¿Quiere cerrar sesión?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button
            .setPositiveButton("Salir", DialogInterface.OnClickListener {
                    dialog, id ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    this.finish()
                    this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
            })
            // negative button
            .setNegativeButton("Cancelar", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Cerrar Sesión")
        alert.show()
    }

    /**
     * Method that takes you to the product's activity
     */
    private fun showProductsActions(){
        val intent = Intent(this, ProductsActivty::class.java)
        startActivity(intent)
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}