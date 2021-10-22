package com.example.sistematienda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class InventoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        val returnButton = findViewById<ImageButton>(R.id.regresarInventario)
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