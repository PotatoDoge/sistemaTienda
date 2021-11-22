package com.example.sistematienda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //Toast.makeText(this, Usuario.tipoUsuario, Toast.LENGTH_LONG).show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val addUser = findViewById<CardView>(R.id.agregarUsuario)
        val showUsers = findViewById<CardView>(R.id.mostrarUsuarios)
        val editUser = findViewById<CardView>(R.id.editarUsuario)
        val deleteUser = findViewById<CardView>(R.id.borrarUsuario)
        val returnButton = findViewById<ImageButton>(R.id.regresarAjustes)

        addUser.setOnClickListener{
            val intent = Intent(this, AddUser::class.java)
            startActivity(intent)
            finish()
        }

        deleteUser.setOnClickListener{
            val intent = Intent(this, DeleteUser::class.java)
            startActivity(intent)
            finish()
        }

        showUsers.setOnClickListener{
            val intent = Intent(this, ShowUsers::class.java)
            startActivity(intent)
            finish()
        }

        returnButton.setOnClickListener{
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}