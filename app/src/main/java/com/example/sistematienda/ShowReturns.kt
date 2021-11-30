package com.example.sistematienda

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class ShowReturns : AppCompatActivity() {

    private var table: TableLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_returns)
        val returnButton = findViewById<ImageButton>(R.id.returnShowReturns)
        table = findViewById(R.id.returnsTable)
        showReturns("http://charlyffs.mywire.org:9000/llenar_tabla_dev.php")

        returnButton.setOnClickListener{
            val intent = Intent(this, ReturnsActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
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

    private fun showReturns(URL:String){
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
                        throwAlert("Tabla vacía","No hay devoluciones registradas en la base de datos.")
                    }
                }
                else{
                    // no encontró
                    throwAlert("Vacío","No hay devoluciones registradas en la base de datos.")
                }
            },
            {
                // lo que pasa si hay error
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                return HashMap<String, String>()
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