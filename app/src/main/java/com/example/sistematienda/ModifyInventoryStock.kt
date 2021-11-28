package com.example.sistematienda

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.lang.Exception

class ModifyInventoryStock : AppCompatActivity() {

    private lateinit var clave:EditText
    private lateinit var cantidad:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_inventory_stock)
        val returnInv = findViewById<ImageButton>(R.id.returnModify)
        clave = findViewById(R.id.addStockClaveInv)
        cantidad = findViewById(R.id.addStockInv)
        val modStock = findViewById<Button>(R.id.modificarExisProdInv)

        returnInv.setOnClickListener{
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        modStock.setOnClickListener {
            checkIfProductInInventory("http://charlyffs.mywire.org:9000/checar_disponibilidad_inv.php")
        }

    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    /**
     * Method that checks if product exists in inventory
     */
    private fun checkIfProductInInventory(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró producto en inv con esa clave
                    try{
                        val cla = clave.text.toString()
                        val cntd = cantidad.text.toString().toInt()
                        modifiyStock("http://charlyffs.mywire.org:9000/modificar_stock.php",cla,cntd.toString())
                        clave.text.clear()
                        cantidad.text.clear()
                        Toast.makeText(this, "La existencia del producto fue ajustada correctamente.", Toast.LENGTH_LONG).show()

                    }
                    catch(e:Exception){
                        throwAlert("Error.","El campo cantidad tiene que ser de valor numérico entero solamente.")
                    }
                }
                else{
                    throwAlert("No existe en inventario.","No hay producto dado de alta en el inventario con esa clave.")
                }
            },
            {
                // lo que pasa si hay error
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val parametros = HashMap<String,String>()
                val id = clave.text.toString()
                parametros["idProd"] = id
                return parametros
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return HashMap()
            }
        }
        // Add the request to the RequestQueue.
        rq.add(stringRequest)
    }

    /**
     * Method that shows an alert with the required title and msg
     */
    private fun throwAlert(title:String, msg: String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(msg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button
            .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, id -> dialog.dismiss()
            })
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

    /**
     * Method that modifies stock existence in the inventory given an id
     */
    private fun modifiyStock(URL: String, id:String,cant:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { _ -> },
            {
                // lo que pasa si hay error
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val parametros = HashMap<String,String>()
                parametros["idProd"] = id
                parametros["cant"] = cant
                return parametros
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return HashMap()
            }
        }
        // Add the request to the RequestQueue.
        rq.add(stringRequest)
    }

}