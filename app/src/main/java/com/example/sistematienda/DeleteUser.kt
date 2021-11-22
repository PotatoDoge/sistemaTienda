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

class DeleteUser : AppCompatActivity() {

    private lateinit var idUser: EditText
    private lateinit var nombreUser: EditText
    private lateinit var apellidoUser: EditText
    private lateinit var telefonoUser: EditText
    private lateinit var correoUser: EditText
    private lateinit var passwordUser: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_user)
        val returnButton = findViewById<ImageButton>(R.id.returnDeleteUser)
        val deleteUser = findViewById<Button>(R.id.deleteUsuario)
        val buscarUser = findViewById<Button>(R.id.buscarUsuario)
        idUser = findViewById(R.id.deleteClaveUsuario)
        nombreUser = findViewById(R.id.deleteNombreUsuario)
        apellidoUser = findViewById(R.id.deleteApellidoUsuario)
        telefonoUser = findViewById(R.id.deleteTelefonoUsuario)
        correoUser = findViewById(R.id.deleteCorreoUsuario)
        passwordUser = findViewById(R.id.deletePasswordUsuario)

        buscarUser.setOnClickListener {
            checkAvailability("http://charlyffs.mywire.org:9000/checar_disponibilidad_empleado.php")
        }

        returnButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        deleteUser.setOnClickListener {
            if(idUser.text.isBlank()){
                throwAlert("Buscar clave","Por favor primero busque una clave de usuario para borrar.")
            }
            else{
                throwAlertDelete("¿Seguro?","Está seguro que quiere borrar a este usuario?")
            }
        }

    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    /**
     * Method that shows an alert with the required title and msg
     */
    private fun throwAlertDelete(title:String, msg: String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(msg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button
            .setPositiveButton("Sí", DialogInterface.OnClickListener {
                    dialog, id -> borrarUsuario()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id -> dialog.dismiss()
            })
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

    private fun borrarUsuario(){
        deleteUserFromEmpleado("http://charlyffs.mywire.org:9000/borrar_empleado.php",idUser.text.toString())
        nombreUser.text.clear()
        apellidoUser.text.clear()
        telefonoUser.text.clear()
        correoUser.text.clear()
        passwordUser.text.clear()
        idUser.text.clear()
        idUser.isEnabled = true
        Toast.makeText(this, "Empleado borrado con éxito", Toast.LENGTH_LONG).show()
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
     * Method that checks if id exists in database. If it does not, it calls registerProductInDatabase method and handles products registration
     */
    private fun checkAvailability(URL: String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontró usuario con misma clave
                    val jsonObject = JSONObject(response)
                    nombreUser.setText(jsonObject.getString("nombre"))
                    apellidoUser.setText(jsonObject.getString("apellido"))
                    telefonoUser.setText(jsonObject.getString("telefono"))
                    correoUser.setText(jsonObject.getString("correo"))
                    passwordUser.setText(jsonObject.getString("password"))
                    idUser.isEnabled = false
                }
                else{
                    throwAlert("Usuario no válido","No se encontró a un usuario con esa clave. Intente con otra de favor.")
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
                parametros["idUsuario"] = idUser.text.toString()
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

    private fun deleteUserFromEmpleado(URL:String,id:String){
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