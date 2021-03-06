package com.example.sistematienda

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LogInActivity: AppCompatActivity() {

    var edtUsuario: EditText? = null
    var edtPassword:EditText? = null

    // arreglar responsividad en esta ventana
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        edtUsuario = findViewById(R.id.edtUsuario)
        edtPassword = findViewById(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.enter_button)
        val btnReg = findViewById<Button>(R.id.regresar_button)

        btnLogin.setOnClickListener{
            validarUsuario("http://charlyffs.mywire.org:9000/validar_usuario.php")
            //val intent = Intent(this,PrincipalActivity::class.java)
            //startActivity(intent)
            //finish()
        }

        btnReg.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

    /**
     * Método que conecta con el login.php para validar que exista el usuario correspondiente
     */
    private fun validarUsuario(URL:String){
        val rq:RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    //Toast.makeText(this,response.toString(), Toast.LENGTH_LONG).show() descomentar para mostrar la respuesta del server
                    val jsonObject = JSONObject(response)
                    Usuario.tipoUsuario = jsonObject.getString("tipoEmpleado")
                    Usuario.idUsuario = jsonObject.getString("idEmpleado")
                    val intent = Intent(this,PrincipalActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this,"Usuario o contraseña incorrectas", Toast.LENGTH_LONG).show()
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
                parametros["usuario"] = edtUsuario?.text.toString()
                parametros["password"] = edtPassword?.text.toString()
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