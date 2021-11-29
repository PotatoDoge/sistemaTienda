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

class ShowSales : AppCompatActivity() {

    private var table: TableLayout? = null
    private var categorias = arrayOf<String>()
    private var catSeleccionada:Int = 0;
    private var catSelectedValue:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_sales)
        val returnButton = findViewById<ImageButton>(R.id.returnShowSales)
        val filtro = findViewById<Button>(R.id.filtrarTablaSales)
        table = findViewById(R.id.salesTable)
        mostrarVentas("http://charlyffs.mywire.org:9000/llenar_tabla_sales.php","TODO")
        retrieveCategoryFromDatabase("http://charlyffs.mywire.org:9000/llenar_spinner_categorias.php")


        returnButton.setOnClickListener{
            val intent = Intent(this, SalesActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        filtro.setOnClickListener {
            selectCategories()
        }

    }

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
            mostrarVentas("http://charlyffs.mywire.org:9000/llenar_tabla_sales.php",catSelectedValue)
        }).setCancelable(false)

        val alert = builder.create()
        alert.show()
    }

    private fun mostrarVentas(URL:String, filtro:String){
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
                            val idVenta = newFila.findViewById<View>(R.id.idPrd) as TextView
                            val idProd = newFila.findViewById<View>(R.id.nombrePrd) as TextView
                            val cant = newFila.findViewById<View>(R.id.descPrd) as TextView
                            idVenta.text = obj.getString("idVenta")
                            idProd.text = obj.getString("idProducto")
                            cant.text = obj.getString("cantidad")
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

}