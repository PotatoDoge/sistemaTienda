package com.example.sistematienda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class ProductsActivty : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener{
            val intent = Intent(this,PrincipalActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

    // Method that manages when the menu is opened or closed
    override fun onBackPressed() {
        moveTaskToBack(false)
        finish()
    }
}