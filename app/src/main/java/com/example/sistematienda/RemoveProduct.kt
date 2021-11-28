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
import org.json.JSONObject
import java.lang.Exception

class RemoveProduct : AppCompatActivity() {

    private lateinit var clave:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_product)
        val returnSettings = findViewById<ImageButton>(R.id.returnInv)
        val deleteProd = findViewById<Button>(R.id.borrarProdInv)
        clave = findViewById(R.id.deleteProdInv)

        returnSettings.setOnClickListener{
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        deleteProd.setOnClickListener {
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
                    // mostrar alert que si dice que sí, se borra de la tabla inventario
                    throwAlertDelete("¿Estás seguro?","Si borras esto, se borra la información del producto en el inventario (existencia, precio, disponibilidad). El producto seguriá existiendo en el sistema, pero ya no estará dado de alta en el inventario.")

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
     * Method that shows an alert with the required title and msg
     */
    private fun throwAlertDelete(title:String, msg: String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(msg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button
            .setPositiveButton("Sí", DialogInterface.OnClickListener {
                    dialog, id ->
                    deleteProductFromInventory("http://charlyffs.mywire.org:9000/borrar_producto_en_inv.php",clave.text.toString())
                    Toast.makeText(this, "Producto borrado correctamente", Toast.LENGTH_LONG).show()
                    clave.text.clear()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id ->
                    dialog.dismiss()

            })
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

    private fun deleteProductFromInventory(URL:String, id:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { _ ->
            },
            {
                // lo que pasa si hay error
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val parametros = HashMap<String,String>()
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

}