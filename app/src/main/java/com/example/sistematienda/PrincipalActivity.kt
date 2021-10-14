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


open class PrincipalActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))

        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)

        val nav = findViewById<NavigationView>(R.id.navigation_view)
        nav.setNavigationItemSelectedListener(this)
        nav.menu.getItem(0).isChecked = true
        toggle.syncState()
    }

    // Method that manages when the menu is opened or closed
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            moveTaskToBack(false); // evita que el usuario accidentalmente se salga de la sesión
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu1 -> Toast.makeText(this, "ITEM 1", Toast.LENGTH_SHORT).show()
            R.id.menu2 -> Toast.makeText(this, "ITEM 2", Toast.LENGTH_SHORT).show()
            R.id.menu3 -> Toast.makeText(this, "ITEM 3", Toast.LENGTH_SHORT).show()
            R.id.menu4 -> Toast.makeText(this, "ITEM 4", Toast.LENGTH_SHORT).show()
            R.id.menu5 -> Toast.makeText(this, "ITEM 5", Toast.LENGTH_SHORT).show()
            R.id.menu6 -> Toast.makeText(this, "ITEM 6", Toast.LENGTH_SHORT).show()
            R.id.menu7 -> signOut() // cerrar sesión
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Método que muestra la alerta de cerrar sesión
     */
    fun signOut(){
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
}