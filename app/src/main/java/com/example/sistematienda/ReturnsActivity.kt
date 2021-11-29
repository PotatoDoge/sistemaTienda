package com.example.sistematienda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.cardview.widget.CardView

class ReturnsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_returns)
        val returnButton = findViewById<ImageButton>(R.id.regresarDevolucion)
        val newReturn = findViewById<CardView>(R.id.registrarDevolucion)
        returnButton.setOnClickListener{
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        newReturn.setOnClickListener{
            val intent = Intent(this, RegisterReturn::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}