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

class AddCategory : AppCompatActivity() {

    private lateinit var nuevaCat: EditText
    private lateinit var nuevaDescCat: EditText
    private lateinit var nuevoNombrCat: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        // Elementos
        val returnButton = findViewById<ImageButton>(R.id.returnAddCat)
        val emptyFields = findViewById<ImageButton>(R.id.eraseFields)
        val agregarCat= findViewById<Button>(R.id.agregarCategoria)

        nuevaCat = findViewById(R.id.addClaveCat)
        nuevaDescCat = findViewById(R.id.addDescripcionCategoria)
        nuevoNombrCat = findViewById(R.id.addNombreCat)

        // Listeners
        returnButton.setOnClickListener{
            val intent = Intent(this, ProductsActivty::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        emptyFields.setOnClickListener{
            nuevaCat.text.clear()
            nuevaDescCat.text.clear()
            nuevoNombrCat.text.clear()
            Toast.makeText(this, "Campos vaciados", Toast.LENGTH_SHORT).show()
        }

        agregarCat.setOnClickListener{
            if(checkIfFieldsEmpty(nuevaCat, nuevaDescCat, nuevoNombrCat)){
                checkAvailability(nuevaCat.text.toString(),nuevoNombrCat.text.toString(), nuevaDescCat.text.toString(), "http://charlyffs.mywire.org:9000/checar_disponibilidad_cat.php")
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
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
     * Method that checks if id exists in database. If it does not, it calls registerCategoryInDatabase method and handles products registration
     */
    private fun checkAvailability(id: String, name:String, dsc:String, URL: String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró articulo con el mismo nombre
                    throwAlert("Ya existe","Ya existe una categoría registrada con esa clave o nombre. Intente con otra de favor.")
                }
                else{
                    registerCategoryInDatabase(id,name,dsc,"http://charlyffs.mywire.org:9000/agregar_categoria.php")
                    Toast.makeText(this,"Categoría registrada correctamente", Toast.LENGTH_SHORT).show()
                    nuevaCat.text.clear()
                    nuevaDescCat.text.clear()
                    nuevoNombrCat.text.clear()
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
                parametros["idCat"] = id
                parametros["catName"] = name
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
     * Method that registers categories in the database
     */
    private fun registerCategoryInDatabase(id: String, name: String, desc: String, URL: String){
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
}