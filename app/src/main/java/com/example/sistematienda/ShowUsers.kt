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

class ShowUsers : AppCompatActivity() {

    private var table: TableLayout? = null
    private var categorias = arrayOf<String>()
    private var catSeleccionada:Int = 0;
    private var catSelectedValue:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)
        val returnButton = findViewById<ImageButton>(R.id.returnShowUser)
        val filtrar = findViewById<Button>(R.id.filtrarTablaUsuario)
        table = findViewById(R.id.usersTable)
        mostrarUsuarios("http://charlyffs.mywire.org:9000/llenar_tabla_usuario.php","Todo")

        returnButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        filtrar.setOnClickListener { // FILTRAR POR TIPOS DE USUARIO
        }

    }

    private fun mostrarUsuarios(URL: String, filtro:String){
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
                            val tipo = newFila.findViewById<View>(R.id.descPrd) as TextView
                            id.text = obj.getString("idEmpleado") // cambiar por valor de id del jsonObject
                            nmb.text = obj.getString("name")
                            tipo.text = obj.getString("tipoEmpleado")
                            table?.addView(newFila)
                        }
                    }
                    catch (e: Exception){
                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                        throwAlert("Tabla vacía","No hay usuarios registrados en la base de datos.")
                    }
                }
                else{
                    // no encontró
                    Toast.makeText(this, "EN EL ELSE", Toast.LENGTH_LONG).show()
                    throwAlert("Vacío","No hay usuarios registrados en la base de datos.")
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
     * Blocks 'going back' gestures
     */
    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}