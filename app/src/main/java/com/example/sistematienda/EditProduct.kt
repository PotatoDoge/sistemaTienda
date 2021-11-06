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
class EditProduct : AppCompatActivity() {
    private lateinit var prod: EditText
    private lateinit var descProd: EditText
    private lateinit var nombrProd: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        // Elementos
        val returnButton = findViewById<ImageButton>(R.id.returnAdd)
        val emptyFields = findViewById<ImageButton>(R.id.eraseFields)
        val lookUpProduct = findViewById<Button>(R.id.buscarProducto)
        val updateProd = findViewById<Button>(R.id.actualizarProducto)
        prod = findViewById(R.id.addClaveProducto)
        descProd = findViewById(R.id.addDescripcionProducto)
        nombrProd = findViewById(R.id.addNombreProducto)


        // Listeners
        returnButton.setOnClickListener{
            val intent = Intent(this, ProductsActivty::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        emptyFields.setOnClickListener{
            prod.text.clear()
            descProd.text.clear()
            nombrProd.text.clear()
            prod.isEnabled = true
            Toast.makeText(this, "Campos vaciados", Toast.LENGTH_SHORT).show()
        }

        lookUpProduct.setOnClickListener {
            lookUpFields("http://charlyffs.mywire.org:9000/mostrar_info_producto.php",prod.text.toString())
        }

        updateProd.setOnClickListener {
            if(checkIfFieldsEmpty(prod,descProd,nombrProd)){
                updateProductInDatabase(
                    prod.text.toString(),nombrProd.text.toString(),descProd.text.toString(),"http://charlyffs.mywire.org:9000/actualizar_producto.php"
                )
                Toast.makeText(this,"Producto actualizado correctamente!", Toast.LENGTH_SHORT).show()
                prod.text.clear()
                descProd.text.clear()
                nombrProd.text.clear()
                prod.isEnabled = true
            }
        }
    }

    /**
     * Blocks 'going back' gestures
     */
    override fun onBackPressed() {
        moveTaskToBack(false)
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
     * Methd that checks if fields are empty
     */
    private fun checkIfFieldsEmpty(f1: EditText, f2:EditText, f3:EditText): Boolean{
        if(f1.text.isBlank() || f2.text.isBlank() || f3.text.isBlank()){
            throwAlert("Llenar campos", "Todos los campos tienen que estar llenos para seguir con el registro")
            return false
        }
        return true
    }

    /**
     * Method that checks if id exists in database. If it does not, it calls registerProductInDatabase method and handles products registration
     */

    /**
     * Method that registers products in the database
     */
    private fun updateProductInDatabase(id: String, name: String, desc: String, URL: String){
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
                parametros["id"] = id
                parametros["nombre"] = name
                parametros["desc"] = desc
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
     * Method that retrieves info from the product and fills in the blank fields
     */
    private fun lookUpFields(URL: String, id: String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró articulo con esa clave
                    val jsonObject = JSONObject(response)
                    prod.setText(jsonObject.getString("idProducto"))
                    descProd.setText(jsonObject.getString("descripcion"))
                    nombrProd.setText(jsonObject.getString("nombre"))
                    prod.isEnabled = false
                }
                else{
                    // no encontró
                    throwAlert("No existe","No existe un prodcuto registrado con esa clave. Intente con otra de favor.")
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
                parametros["id"] = id
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