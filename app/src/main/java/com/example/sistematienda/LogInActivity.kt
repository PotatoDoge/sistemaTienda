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

class LogInActivity: AppCompatActivity() {

    var edtUsuario: EditText? = null
    var edtPassword:EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        edtUsuario = findViewById(R.id.edtUsuario)
        edtPassword = findViewById(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.enter_button)

        btnLogin.setOnClickListener{
            validarUsuario("http://charlyffs.mywire.org:9000/validar_usuario.php")
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
                    //Toast.makeText(this,response.toString(), Toast.LENGTH_LONG).show()descomentar para mostrar la respuesta del server
                    edtUsuario?.setText("")
                    edtPassword?.setText("")
                    val intent = Intent(this,PrincipalActivity::class.java)
                    startActivity(intent)
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