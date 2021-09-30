package com.example.sistematienda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Adaptar método a la clase
    /*
    /**
     * Método que conecta con el login.php para validar que exista el usuario correspondiente
     */
    private fun validarUsuario(URL:String){
        val rq:RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    Toast.makeText(this,response.toString(), Toast.LENGTH_LONG).show()
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
                //parametros["usuario"] = edtUsuario?.text.toString() añadir edtUsuario
                //parametros["password"] = edtPassword?.text.toString() añadir edtPassword
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
     */

}
