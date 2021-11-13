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
import org.json.JSONArray
import org.json.JSONObject
class EditProduct : AppCompatActivity() {
    private lateinit var prod: EditText
    private lateinit var descProd: EditText
    private lateinit var nombrProd: EditText

    private var categorias = arrayOf<String>()
    private var categoriasRegistradasEnProducto = arrayOf<String>()
    private var categoriasSeleccionadas = booleanArrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        // Elementos
        val returnButton = findViewById<ImageButton>(R.id.returnAdd)
        val emptyFields = findViewById<ImageButton>(R.id.eraseFields)
        val editCat= findViewById<Button>(R.id.editCat2Producto)
        val lookUpProduct = findViewById<Button>(R.id.buscarProducto)
        val updateProd = findViewById<Button>(R.id.actualizarProducto)
        prod = findViewById(R.id.addClaveProducto)
        descProd = findViewById(R.id.addDescripcionProducto)
        nombrProd = findViewById(R.id.addNombreProducto)
        retrieveCategoryFromDatabase("http://charlyffs.mywire.org:9000/llenar_spinner_categorias.php")



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
            // para las categorias, borrar los registros que haya en la tabla producto_cateogira de este id
            // insertar nuevas relaciones producto_categoria con este id
            if(checkIfFieldsEmpty(prod,descProd,nombrProd) && checkIfCategorySelected()){
                checkAvailability("http://charlyffs.mywire.org:9000/checar_disponibilidad_prod.php")
            }
        }

        editCat.setOnClickListener {
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
                    retrieveCategories("http://charlyffs.mywire.org:9000/llenar_categorias_activas.php", jsonObject.getString("idProducto"))
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

    /**
     * Method that retrieves categories' names from database so that the list can be filled and
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

    /**
     * Method that displays the alert with the available categories
     */
    private fun selectCategories(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Categorías")
        builder.setMultiChoiceItems(categorias, categoriasSeleccionadas) { dialog, which, isChecked ->
            // Update the clicked item checked status
            categoriasSeleccionadas[which] = isChecked
        }

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener {
                dialog, id -> dialog.dismiss()
        }).setCancelable(false)

        val alert = builder.create()
        alert.show()
    }


    /**
     * Method that marks as true the categories corresponding to this product
     */
    private fun retrieveCategories(URL:String, id: String){
        categoriasSeleccionadas = BooleanArray(categorias.size){false}
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    try {
                        val jsonArray = JSONArray(response)
                        for(i in 0 until jsonArray.length()){
                            for(x in categorias.indices){
                                if(categorias[x] == jsonArray.getJSONObject(i).getString("nombre")){
                                    categoriasSeleccionadas[x] = true
                                }
                            }

                        }
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
                parametros["idCat"] = id
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
     * Method that checks if at least one category is selected
     */
    private fun checkIfCategorySelected():Boolean{
        for(i in 0..categoriasSeleccionadas.size-1){
            if(categoriasSeleccionadas[i] == true) return true
        }
        throwAlert("Llenar campos", "Todos los campos tienen que estar llenos para seguir con el registro, y mínimo una categoría seleccionada")
        return false
    }

    /**
     * Method that checks if id exists in database. If it does not, it calls registerProductInDatabase method and handles products registration
     */
    private fun checkAvailability( URL: String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró articulo con el mismo nombre o clave
                    throwAlert("Ya existe","Ya existe un producto registrado con esa clave o con ese nombre. Intente con otra de favor.")
                }
                else{
                    // los sig dos métodos no están funcionando
                    updateProductInDatabase(prod.text.toString(),nombrProd.text.toString(),descProd.text.toString(),"http://charlyffs.mywire.org:9000/actualizar_producto.php")
                    emptyProductCategory("http://charlyffs.mywire.org:9000/borrar_registro_producto_categoria.php")
                    // EL PROBLEMA ES QUE NO ESTÁ BORRANDO BIEN DE LA BD CON EL emptyProductCategory
                    for(i in 0..categorias.size-1){
                        if(categoriasSeleccionadas[i] == true){
                            val nameCat = categorias[i]
                            insertIntoProductToCategoryTable(prod.text.toString(),nameCat,"http://charlyffs.mywire.org:9000/asignar_producto_categoria.php")
                        }
                    }
                    prod.text.clear()
                    nombrProd.text.clear()
                    descProd.text.clear()
                    prod.isEnabled = true
                    categoriasSeleccionadas = BooleanArray(categoriasSeleccionadas.size){false}
                    Toast.makeText(this,"Producto actualizado correctamente!", Toast.LENGTH_SHORT).show()
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
                parametros["idProd"] = ""
                parametros["prodName"] = nombrProd.text.toString()
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
     * Method that registers category id and product id in their in-between table
     */
    private fun insertIntoProductToCategoryTable(id:String, catName: String, URL: String){
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
                parametros["nombreCat"] = catName
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
     * Method that deletes records of a specific id in producto_categoria
     */
    private fun emptyProductCategory(URL:String){
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
                val pr = prod.text.toString()
                parametros["idProd"] = pr // EL PROBLEMA ES QUE POR ALGUNA RAZÓN EL valor de pr lo agarra mal, por lo que
                return parametros
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return HashMap()
            }
        }
        // Add the request to the RequestQueue.
        rq.add(stringRequest)
        Toast.makeText(this, prod.text.toString(), Toast.LENGTH_LONG).show()
    }

}