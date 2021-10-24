package com.example.sistematienda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class ProductsActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        // definir cardviews
        val returnBtn= findViewById<ImageButton>(R.id.regresarProducto)
        val addProd = findViewById<CardView>(R.id.nuevoProducto);
        val delProd = findViewById<CardView>(R.id.borrarProducto);
        val editProd = findViewById<CardView>(R.id.editarProducto);

        // definir funcionalidad de cardviews
        returnBtn.setOnClickListener{
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)

        }

        addProd.setOnClickListener{
            val intent = Intent(this, AddProduct::class.java)
            startActivity(intent)
            finish() // ver si ponerle finish o matarla desde la siguiiente ventana
        }

        editProd.setOnClickListener{
            val intent = Intent(this, EditProduct::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Method that manages when the menu is opened or closed
    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}