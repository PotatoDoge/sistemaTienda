package com.example.sistematienda

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text

class ShowProduct : AppCompatActivity() {

    private var table: TableLayout? = null
    private var categorias = arrayOf<String>()
    private var catSeleccionada:Int = 0;
    private var catSelectedValue:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_product)
        val returnButton = findViewById<ImageButton>(R.id.returnShowProd)
        val filtrar = findViewById<Button>(R.id.filtrarTablaProd)
        table = findViewById(R.id.productsTable)
        mostrarProductos("http://charlyffs.mywire.org:9000/llenar_tabla_prod.php","Todo")
        retrieveCategoryFromDatabase("http://charlyffs.mywire.org:9000/llenar_spinner_categorias.php")


        // Listeners
        returnButton.setOnClickListener{
            val intent = Intent(this, ProductsActivty::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        filtrar.setOnClickListener {
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

    private fun mostrarProductos(URL:String, filtro:String){
        table?.removeAllViews()
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    try {
                        val jsonArray = JSONArray(response)
                        for(i in 0 until jsonArray.length()){
                            val obj = jsonArray.getJSONObject(i)
                            val newFila = LayoutInflater.from(this).inflate(R.layout.table_row_showproducts,null,false)
                            val id = newFila.findViewById<View>(R.id.idPrd) as TextView
                            val nmb = newFila.findViewById<View>(R.id.nombrePrd) as TextView
                            val dsc = newFila.findViewById<View>(R.id.descPrd) as TextView
                            id.text = obj.getString("idProducto") // cambiar por valor de id del jsonObject
                            nmb.text = obj.getString("nombre")
                            dsc.text = obj.getString("descripcion")
                            table?.addView(newFila)
                        }
                    }
                    catch (e: Exception){
                        throwAlert("Tabla vacía","No hay productos registrados en la base de datos.")
                    }
                }
                else{
                    // no encontró
                    throwAlert("Vacío","No hay productos registrados en la base de datos.")
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
                parametros["nomCat"] = filtro
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
     * Method that retrieves categories' names from database so that the array can be filled and
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
                        val prueba = Array(jsonArray.length() + 1){""}
                        prueba[0] = "Todo"
                        for(i in 0 until jsonArray.length()){
                            val obj = jsonArray.getJSONObject(i)
                            prueba[i+1] = obj.getString("nombre")
                        }
                        categorias = prueba
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
     * Method that displays the alert with the available catgeories
     */
    private fun selectCategories(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Categorías")
        builder.setSingleChoiceItems(categorias, catSeleccionada) { _, which ->
            catSeleccionada = which
            catSelectedValue = categorias[which]
        }

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener {
                dialog,id ->
                mostrarProductos("http://charlyffs.mywire.org:9000/llenar_tabla_prod.php", catSelectedValue)
        }).setCancelable(false)

        val alert = builder.create()
        alert.show()
    }



}

