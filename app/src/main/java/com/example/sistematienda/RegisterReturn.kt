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

class RegisterReturn : AppCompatActivity() {

    private lateinit var claveVenta:EditText
    private lateinit var claveProd:EditText
    private var idDev: Int = 0;
    private var ctd: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_return)
        val returnButton = findViewById<ImageButton>(R.id.returnReturns)
        val complete = findViewById<Button>(R.id.addReturn)
        claveVenta = findViewById(R.id.returnClaveVenta)
        claveProd = findViewById(R.id.returnClaveProd)

        returnButton.setOnClickListener{
            val intent = Intent(this, ReturnsActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        complete.setOnClickListener {
            if(claveVenta.text.isBlank() || claveProd.text.isBlank()){
                throwAlert("Llenar campos","Todos los campos tienen que estar llenos para poder buscar.")
            }
            else{
                checkSale("http://charlyffs.mywire.org:9000/checar_venta.php")
            }
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

    private fun checkSale(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    val jsonObject = JSONObject(response)
                    ctd = jsonObject.getString("cantidad").toInt()
                    throwAlertReturn("Confirmar","¿Está seguro que quiere realizar la devolución?")
                }
                else{
                    throwAlert("No existe registro de ese producto y/o/en esa venta.","No se encontró nada con esas especificaciones.")
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
                parametros["idProd"] = claveProd.text.toString()
                parametros["idVenta"] = claveVenta.text.toString()
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
    private fun throwAlertReturn(title:String, msg: String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(msg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button
            .setPositiveButton("Sí", DialogInterface.OnClickListener {
                    dialog, id ->
                    handleReturns()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id ->
                dialog.dismiss()
            })
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

    /**
     * Method that inserts a return in venta_devolucion table
     */
    private fun registrarVentaDevolucion(URL:String, clave:String,idVenta:String,cant:String,idv:String){
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
                parametros["idProd"] = clave
                parametros["idVenta"] = idVenta
                parametros ["cant"] = cant
                parametros ["idDev"] = idv
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
     * Method that inserts a return in venta_devolucion table
     */
    private fun registrarDevolucion(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                if(response.isNotEmpty()){
                    val jsonObject = JSONObject(response)
                    idDev = jsonObject.getString("idDevolucion").toInt()
                    Toast.makeText(this, idDev.toString(), Toast.LENGTH_LONG).show()
                    registrarVentaDevolucion("http://charlyffs.mywire.org:9000/registrar_venta_devolucion.php", claveProd.text.toString(),claveVenta.text.toString(),ctd.toString(), idDev.toString())
                    claveProd.text.clear()
                    claveVenta.text.clear()
                    ctd = 0
                    idDev = 0
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
                parametros["idEmp"] = Usuario.idUsuario
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

    private fun handleReturns(){
        registrarDevolucion("http://charlyffs.mywire.org:9000/registrar_devolucion.php")
    }
}