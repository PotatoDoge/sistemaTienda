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

class EditUser : AppCompatActivity() {

    private lateinit var idUser: EditText
    private lateinit var nombreUser: EditText
    private lateinit var apellidoUser: EditText
    private lateinit var telefonoUser: EditText
    private lateinit var correoUser: EditText
    private lateinit var passwordUser: EditText

    private var tiposUsuario = arrayOf("ADMIN","NORMAL")
    private var tipoUsuarioSeleccionado: Int = -1
    private var tipoUsuarioValor:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        val returnButton = findViewById<ImageButton>(R.id.returnEditUser)
        val updateUser = findViewById<Button>(R.id.updateUsuario)
        val buscarUser = findViewById<Button>(R.id.buscarUsuario)
        val tipoUsuario = findViewById<Button>(R.id.editTipoUsuario)
        val eraseFields = findViewById<ImageButton>(R.id.eraseFields)

        idUser = findViewById(R.id.editClaveUsuario)
        nombreUser = findViewById(R.id.editNombreUsuario)
        apellidoUser = findViewById(R.id.editApellidoUsuario)
        telefonoUser = findViewById(R.id.editTelefonoUsuario)
        correoUser = findViewById(R.id.editCorreoUsuario)
        passwordUser = findViewById(R.id.editPasswordUsuario)

        buscarUser.setOnClickListener {
            checkAvailability("http://charlyffs.mywire.org:9000/checar_disponibilidad_empleado.php")
        }

        returnButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        tipoUsuario.setOnClickListener {
            selectType()
        }

        updateUser.setOnClickListener {
            if(checkFields()){
                if(idUser.text.length < 20 && nombreUser.text.length < 50 && apellidoUser.text.length < 50 && telefonoUser.text.length < 20 &&
                    correoUser.text.length < 60 && passwordUser.text.length < 25){
                    actualizareDatos()
                }
                else{
                    throwAlert("Valor incorrecto de caracteres","El valor máximo de caracteres es:\n-Clave: 20\n-Nombre: 50\n-Apellido: 50\n-Telefono: 20\n-Correo: 60\n-Password: 25")
                }
            }
            else{
                throwAlert("Llenar campos","Todo los campos tienen que estar llenos y un tipo de usuario seleccionado")
            }
        }

        eraseFields.setOnClickListener{
            nombreUser.text.clear()
            apellidoUser.text.clear()
            telefonoUser.text.clear()
            correoUser.text.clear()
            tipoUsuarioSeleccionado = -1
            tipoUsuarioValor = ""
            passwordUser.text.clear()
            idUser.text.clear()
            idUser.isEnabled = true
        }
    }

    private fun actualizareDatos(){
        updateUser(idUser.text.toString(),nombreUser.text.toString(),apellidoUser.text.toString(),correoUser.text.toString(),telefonoUser.text.toString(),
            tipoUsuarioValor, passwordUser.text.toString(),"http://charlyffs.mywire.org:9000/actualizar_empleado.php")
        nombreUser.text.clear()
        apellidoUser.text.clear()
        telefonoUser.text.clear()
        correoUser.text.clear()
        tipoUsuarioSeleccionado = -1
        tipoUsuarioValor = ""
        passwordUser.text.clear()
        idUser.text.clear()
        idUser.isEnabled = true
        Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_LONG).show()

    }

    override fun onBackPressed() {
        moveTaskToBack(false)
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
                    tipoUsuarioValor = jsonObject.getString("tipoEmpleado")
                    idUser.isEnabled = false
                    if(tipoUsuarioValor == "ADMIN") tipoUsuarioSeleccionado = 0
                    else if(tipoUsuarioValor == "NORMAL") tipoUsuarioSeleccionado = 1
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
     * Method that displays the alert with the available types of users
     */
    private fun selectType(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tipos de Usuario")
        builder.setSingleChoiceItems(tiposUsuario, tipoUsuarioSeleccionado) { _, which ->
            tipoUsuarioSeleccionado = which
            tipoUsuarioValor = tiposUsuario[which]
        }

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener {
                dialog,id ->
            dialog.dismiss()
        }).setCancelable(false)

        val alert = builder.create()
        alert.show()
    }

    private fun updateUser(id:String, nombre:String, apellido:String,correo:String,tel:String,tipo:String,psw:String,URL: String){
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
                parametros["nombre"] = nombre
                parametros["apellido"] = apellido
                parametros["correo"] = correo
                parametros["tel"] = tel
                parametros["tipo"] = tipo
                parametros["psw"] = psw
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

    private fun checkFields():Boolean{
        if(tipoUsuarioSeleccionado == -1 || idUser.text.isBlank() || nombreUser.text.isBlank() || apellidoUser.text.isBlank() || telefonoUser.text.isBlank() || correoUser.text.isBlank() || passwordUser.text.isBlank()){
            return false
        }
        return true;
    }


}