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

class StatusProduct : AppCompatActivity() {

    private lateinit var clave: EditText
    private lateinit var precio: EditText
    private var status = arrayOf("ACTIVO","INACTIVO")
    private var statusSeleccionado: Int = -1
    private var statusValor:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_product)
        val returnInv = findViewById<ImageButton>(R.id.returnInventory)
        val changeStatus = findViewById<Button>(R.id.editStatusProducto)
        val buscarProducto = findViewById<Button>(R.id.buscarProducto)
        val erase = findViewById<ImageButton>(R.id.eraseFields)
        val updateInv = findViewById<Button>(R.id.actualizarProducto)
        clave = findViewById(R.id.alterClaveProducto)
        precio = findViewById(R.id.editPrecioProducto)

        returnInv.setOnClickListener{
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        changeStatus.setOnClickListener {
            selectType()
        }

        buscarProducto.setOnClickListener {
            checkIfProductInInventory("http://charlyffs.mywire.org:9000/checar_disponibilidad_inv.php")
        }

        updateInv.setOnClickListener {
            if(precio.text.isBlank()){
                throwAlert("Llenar campos","Checar que todos los campos estén llenos")
            }
            else{
                updateInInventory("http://charlyffs.mywire.org:9000/actualizar_producto_en_inv.php", clave.text.toString(),precio.text.toString(),statusValor)
                clave.text.clear()
                clave.isEnabled = true
                statusSeleccionado = -1
                statusValor = ""
                precio.text.clear()
                Toast.makeText(this, "Producto en inventario actualizado correctamente", Toast.LENGTH_LONG).show()
            }
        }

        erase.setOnClickListener{
            clave.text.clear()
            clave.isEnabled = true
            statusSeleccionado = -1
            statusValor = ""
            precio.text.clear()
        }

    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    /**
     * Method that displays the alert with the status selection
     */
    private fun selectType(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tipos de Usuario")
        builder.setSingleChoiceItems(status, statusSeleccionado) { _, which ->
            statusSeleccionado = which
            statusValor = status[which]
        }

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener {
                dialog,id ->
            dialog.dismiss()
        }).setCancelable(false)

        val alert = builder.create()
        alert.show()
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
     * Method that checks if product exists in inventory
     */
    private fun checkIfProductInInventory(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró articulo con el mismo nombre o clave
                    val jsonObject = JSONObject(response)
                    clave.setText(jsonObject.getString("idProducto"))
                    clave.isEnabled = false
                    precio.setText(jsonObject.getDouble("precioUnitario").toString())
                    val statusProd = jsonObject.getString("disponible")
                    if(statusProd == "ACTIVO"){
                        statusSeleccionado = 0
                        statusValor = statusProd
                    }
                    else if(statusProd == "INACTIVO"){
                        statusSeleccionado = 1
                        statusValor = statusProd
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

    private fun updateInInventory(URL:String,id:String, pu:String,st:String){
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
                parametros["precio"] = pu
                parametros["status"] = st

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