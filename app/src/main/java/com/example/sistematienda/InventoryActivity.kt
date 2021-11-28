package com.example.sistematienda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.cardview.widget.CardView

class InventoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        val returnButton = findViewById<ImageButton>(R.id.regresarInventario)
        val registerProduct = findViewById<CardView>(R.id.registrarProducto)
        val showProducts = findViewById<CardView>(R.id.mostrarProducto)
        val disableProduct = findViewById<CardView>(R.id.statusProducto)
        val removeProduct = findViewById<CardView>(R.id.quitarProducto)
        val modifyInventory = findViewById<CardView>(R.id.modificarInventario)

        returnButton.setOnClickListener{
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        registerProduct.setOnClickListener{
            val intent = Intent(this, RegisterProduct::class.java)
            startActivity(intent)
            finish()
        }

        showProducts.setOnClickListener{
            val intent = Intent(this, ShowInventory::class.java)
            startActivity(intent)
            finish()
        }

        disableProduct.setOnClickListener{
            val intent = Intent(this, StatusProduct::class.java)
            startActivity(intent)
            finish()
        }

        removeProduct.setOnClickListener{
            val intent = Intent(this, RemoveProduct::class.java)
            startActivity(intent)
            finish()
        }

        modifyInventory.setOnClickListener{
            val intent = Intent(this, ModifyInventoryStock::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}