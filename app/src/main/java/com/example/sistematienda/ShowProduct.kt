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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_product)
        val returnButton = findViewById<ImageButton>(R.id.returnShowProd)
        table = findViewById(R.id.productsTable)
        mostrarProductos("http://charlyffs.mywire.org:9000/llenar_tabla_prod.php")

        // Listeners
        returnButton.setOnClickListener{
            val intent = Intent(this, ProductsActivty::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
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

    private fun mostrarProductos(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
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

