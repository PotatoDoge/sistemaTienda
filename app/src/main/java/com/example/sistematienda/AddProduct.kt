package com.example.sistematienda

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class AddProduct : AppCompatActivity() {

    private var registered: Boolean = false
    private lateinit var nuevoProd: EditText
    private lateinit var nuevaDescProd: EditText
    private lateinit var nuevoNombrProd: EditText
    private lateinit var catProd: Button
    private var categorias = arrayOf<String>()
    private var categoriasSeleccionadas = booleanArrayOf()
    private var catSelec= ArrayList<Boolean>()
    private var catList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        // Elementos
        val returnButton = findViewById<ImageButton>(R.id.returnAdd)
        val emptyFields = findViewById<ImageButton>(R.id.eraseFields)
        val agregarProd = findViewById<Button>(R.id.agregarProducto)

        nuevoProd = findViewById(R.id.addClaveProducto)
        nuevaDescProd = findViewById(R.id.addDescripcionProducto)
        nuevoNombrProd = findViewById(R.id.addNombreProducto)
        catProd = findViewById(R.id.addCat2Producto)
        //fillSpiner() // método que llena el spinner con categorías llenadas de la base de datos
        retrieveCategoryFromDatabase("http://charlyffs.mywire.org:9000/llenar_spinner_categorias.php")


        // Listeners
        returnButton.setOnClickListener{
            val intent = Intent(this, ProductsActivty::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        emptyFields.setOnClickListener{
            nuevoProd.text.clear()
            nuevaDescProd.text.clear()
            nuevoNombrProd.text.clear()
            Toast.makeText(this, "Campos vaciados", Toast.LENGTH_SHORT).show()
        }

        agregarProd.setOnClickListener {
            if(checkIfFieldsEmpty(nuevoProd,nuevaDescProd,nuevoNombrProd)){
                val id = nuevoProd.text.toString()
                val name = nuevoNombrProd.text.toString()
                val desc = nuevaDescProd.text.toString()
                checkAvailability(id, name,desc,"http://charlyffs.mywire.org:9000/checar_disponibilidad.php")
            }
        }

        catProd.setOnClickListener {
            selectCategories()
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
     * Method that checks if fields are empty
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
    private fun checkAvailability(id: String, name:String, dsc:String, URL: String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró articulo con el mismo nombre
                    throwAlert("Ya existe","Ya existe un producto registrado con esa clave. Intente con otra de favor.")
                }
                else{
                    registerProductInDatabase(id,name,dsc,"http://charlyffs.mywire.org:9000/agregar_producto.php")
                    Toast.makeText(this,"Producto registrado correctamente", Toast.LENGTH_SHORT).show()
                    nuevoProd.text.clear()
                    nuevaDescProd.text.clear()
                    nuevoNombrProd.text.clear()
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
     * Method that registers products in the database
     */
    private fun registerProductInDatabase(id: String, name: String, desc: String, URL: String){
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

    /**
     * Method that fills category's spinner

    private fun fillSpiner(){
        categoriasSpinner.add("SA")
        retrieveCategoryFromDatabase("http://charlyffs.mywire.org:9000/llenar_spinner_categorias.php")
        val adapt = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categoriasSpinner)
        catProd.adapter = adapt

        // ver como poner un pop u  emnu con las que hay para ahí seleccionar todas con radio buttons
    }
     */

    /**
     * Method that retrieves categories' names from database so that the spinner can be filled and
     * fills in the arrays that are displayed on the aleret
     */
    private fun retrieveCategoryFromDatabase(URL:String){

        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    try {
                        val jsonArray = JSONArray(response)
                        val prueba = Array<String>(jsonArray.length()){""}
                        val prueba2 = BooleanArray(jsonArray.length()){false}
                        for(i in 0 until jsonArray.length()){
                            val obj = jsonArray.getJSONObject(i)
                            prueba[i] = obj.getString("nombre")
                            //catList.add(obj.getString("nombre"))
                            //catSelec.add(false)
                        }
                        categorias = prueba
                        categoriasSeleccionadas = prueba2
                    }
                    catch (e: Exception){
                        throwAlert("Error",e.toString())
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

    private fun selectCategories(){
        // checar si dejar esto dentro o en el oncreate
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Categorías")
        builder.setMultiChoiceItems(categorias, categoriasSeleccionadas) { dialog, which, isChecked ->
            // Update the clicked item checked status
            categoriasSeleccionadas[which] = isChecked

            // Get the clicked item
            val cat = categorias[which]
        }

        builder.setPositiveButton("OK") { _, _ ->
            // Do something when click positive button
            for (i in 0 until categorias.size) {
                val checked = categoriasSeleccionadas[i]
                // si es true, insertar en la base de datos de prod y cat
                // mandar nombre a array para que cheque si está en db
                // en otro método iterar para meter a la tabla entre producto y cat
                //adaptar esto a editar producto
            }
        }

        val alert = builder.create()
        alert.show()
    }

}