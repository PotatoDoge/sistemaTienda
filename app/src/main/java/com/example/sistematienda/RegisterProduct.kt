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

class RegisterProduct : AppCompatActivity() {

    private lateinit var clave:EditText
    private lateinit var precio:EditText
    private lateinit var existenciaInicial: EditText
    private var status = arrayOf("ACTIVO","INACTIVO")
    private var statusSeleccionado: Int = -1
    private var statusValor:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_product)
        val returnButton = findViewById<ImageButton>(R.id.returnRegister)
        clave = findViewById(R.id.registerClaveProducto)
        precio = findViewById(R.id.registerPrecioUnitarioProducto)
        existenciaInicial = findViewById(R.id.registerExistenciaProducto)
        val disponibilidadButton = findViewById<Button>(R.id.registerDisponibilidadProducto)
        val register = findViewById<Button>(R.id.registerProducto)
        val erase  =findViewById<ImageButton>(R.id.eraseFields)

        returnButton.setOnClickListener{
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        disponibilidadButton.setOnClickListener {
            selectType()
        }

        register.setOnClickListener {
            if(clave.text.isBlank() || precio.text.isBlank() || existenciaInicial.text.isBlank() || statusSeleccionado == -1){
                throwAlert("Llenar campos","Todos los campos tienen que estar llenos para continuar, y el status seleccionado")
            }
            else{
                checkIfProductExists("http://charlyffs.mywire.org:9000/checar_disponibilidad_prod.php")
            }
        }

        erase.setOnClickListener{
            clave.text.clear()
            precio.text.clear()
            existenciaInicial.text.clear()
            statusValor = ""
            statusSeleccionado = -1
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
     * Method that checks if a product is registered in product's table
     */
    private fun checkIfProductExists(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró articulo con el mismo nombre o clave
                        checkIfProductInInventory("http://charlyffs.mywire.org:9000/checar_disponibilidad_inv.php")
                }
                else{
                    // NO ENCONTRÓ CON LA MISMA CLAVE
                    throwAlert("No existe","No existe un producto registrado con esa clave.Intente con otra de favor.")
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
     * Method that checks if product exists in inventory
     */
    private fun checkIfProductInInventory(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró articulo con el mismo nombre o clave
                    throwAlert("Ya existe","Ya existe esa clave registrada en el inventario.")
                }
                else{
                    // NO ENCONTRÓ CON LA MISMA CLAVE
                    try{
                        val id = clave.text.toString()
                        val prc = precio.text.toString().toDouble()
                        val cant = existenciaInicial.text.toString().toInt()
                        val stat = statusValor
                        insertIntoInventario(id,prc,cant,stat,"http://charlyffs.mywire.org:9000/agregar_producto_a_inventario.php")
                        Toast.makeText(this, "Producto añadido al inventario correctamente", Toast.LENGTH_LONG).show()
                        clave.text.clear()
                        precio.text.clear()
                        existenciaInicial.text.clear()
                        statusValor = ""
                        statusSeleccionado = -1

                    }
                    catch (e:Exception){
                        throwAlert("Error.","Hubo un error al insertar los datos, favor de checar que precio sea un valor decimal o entero, y existencia uno entero.")
                    }
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
     * Method that inserts products in the inventory table
     */
    private fun insertIntoInventario(id:String,precio:Double,cant:Int, st:String, URL:String){
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
                parametros["precio"] = precio.toString()
                parametros["cant"] = cant.toString()
                parametros["stat"] = st
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