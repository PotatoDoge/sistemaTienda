package com.example.sistematienda

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception

class RegisterSale : AppCompatActivity() {

    private lateinit var clave:EditText
    private lateinit var cantidad:EditText
    private var table: TableLayout? = null
    private var productos: ArrayList<Venta> = ArrayList()
    private var clavesUnicas: ArrayList<String> = ArrayList()
    private var registrosBase: ArrayList<Venta> = ArrayList()
    private var finalProducts: ArrayList<Venta> = ArrayList()
    private var totalPrice: Double = 0.0
    private var idVenta: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_sale)
        val returnButton = findViewById<ImageButton>(R.id.returnSales)
        val addItem = findViewById<Button>(R.id.addSale)
        val registerSale = findViewById<Button>(R.id.finalizarVenta)
        clave = findViewById(R.id.salesClave)
        cantidad = findViewById(R.id.salesCant)
        table = findViewById(R.id.orderTable)

        returnButton.setOnClickListener{
            val intent = Intent(this, SalesActivity::class.java)
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        addItem.setOnClickListener {
            checkIfProductInInventory("http://charlyffs.mywire.org:9000/checar_disponibilidad_inv.php")
        }

        registerSale.setOnClickListener {
            if(productos.isEmpty()){
                throwAlert("Registrar algo","Se tiene que a??adir un producto como m??imo para registrar una venta.")
            }
            else{
                throwAlertEndSale("Finalizar compra","??Est?? seguro que quiere dar de la alta la compra en el sistema?")
            }
        }

    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    /**
     * Method that checks if product exists in inventory
     */
    private fun checkIfProductInInventory(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                // lo que responde
                if(response.isNotEmpty()){
                    // encontr?? producto en inv con esa clave
                    try{
                        val newVenta = Venta()
                        val jsonObject = JSONObject(response)
                        newVenta.id = clave.text.toString()
                        newVenta.cantidad = cantidad.text.toString().toInt()
                        newVenta.precio = jsonObject.getString("precioUnitario").toDouble() * cantidad.text.toString().toInt()
                        val stock = jsonObject.getString("existencia").toInt()
                        if(jsonObject.getString("disponible") == "ACTIVO"){
                            if(checkStock(newVenta.id,newVenta.cantidad,stock)){
                                productos.add(newVenta)
                                clave.text.clear()
                                cantidad.text.clear()

                                val newFila = LayoutInflater.from(this).inflate(R.layout.table_row_showproducts,null,false)
                                val id = newFila.findViewById<View>(R.id.idPrd) as TextView
                                val cant = newFila.findViewById<View>(R.id.nombrePrd) as TextView
                                val total = newFila.findViewById<View>(R.id.descPrd) as TextView
                                id.text = newVenta.id
                                cant.text = newVenta.cantidad.toString()
                                total.text = newVenta.precio.toString()
                                table?.addView(newFila)
                            }
                            else{
                                throwAlert("Cantidad insuficiente","No hay suficiente cantidad para ese producto para este registro.")
                            }
                        }
                        else{
                            throwAlert("Producto inactivo","Este producto est?? en inventario pero est?? INACTIVO en el sistema.")
                        }

                    }
                    catch(e: Exception){
                        throwAlert("Error.","La cantidad tiene que ser un dato num??rico entero")
                    }
                }
                else{
                    throwAlert("No existe en inventario.","No hay producto dado de alta en el inventario con esa clave.")
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
                val id = clave.text.toString()
                parametros["idProd"] = id
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
     * Method that checks if the storage has enough stock for the sale
     */
    private fun checkStock(id:String,cant:Int,stock:Int):Boolean{
        if(clavesUnicas.contains(id.lowercase())){
            for(v:Venta in registrosBase){
                if(v.id == id.lowercase()){
                    if((v.cantidad - cant) >= 0){
                        v.cantidad = v.cantidad - cant
                        return true
                    }
                }
            }
            return false
        }
        else{
            if((stock-cant)>=0){
                clavesUnicas.add(id.lowercase())
                val registro = Venta()
                registro.id = id.lowercase()
                registro.cantidad = stock-cant
                registro.precio = 0.0
                registrosBase.add(registro)
                return true
            }
            else{
               return false
            }
        }
    }

    /**
     * Method that shows an alert with the required title and msg
     */
    private fun throwAlertEndSale(title:String, msg: String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(msg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button
            .setPositiveButton("S??", DialogInterface.OnClickListener {
                    dialog, id -> registerSale()
                //lo que har?? al finalizar
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
     * Method that joins multiple entrees of the same product, in a single entree (to avoid duplication in the db)
     */
    private fun joinProducts(){
        val fps: ArrayList<String> = ArrayList()
        for(p:Venta in productos){
            if(fps.contains(p.id.lowercase())){
                for(v:Venta in finalProducts){
                    if(v.id.lowercase() == p.id.lowercase()){
                        v.cantidad = v.cantidad + p.cantidad
                        v.precio = v.precio + p.precio
                    }
                }
            }
            else{
                fps.add(p.id.lowercase())
                val finalP = Venta()
                finalP.id = p.id
                finalP.cantidad = p.cantidad
                finalP.precio = p.precio
                finalProducts.add(finalP)
            }
            totalPrice += p.precio
        }
        /*
        for(q:Venta in finalProducts){
            Toast.makeText(this, q.id + " " + q.precio + " " + q.cantidad, Toast.LENGTH_LONG).show()
        }
         */
    }

    /**
     * Methiod that inserts sale into 'VENTA' table in database
     */
    private fun insertIntoVenta(URL:String){
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, URL, com.android.volley.Response.Listener { response ->
                if(response.isNotEmpty()){
                    val jsonObject = JSONObject(response)
                    idVenta = jsonObject.getString("idVenta").toString().toInt()
                    Toast.makeText(this, idVenta.toString(), Toast.LENGTH_LONG).show()
                    for(v:Venta in finalProducts){
                        registerDescripcionVenta("http://charlyffs.mywire.org:9000/agregar_descripcionVenta.php", idVenta,v.id,v.cantidad,v.precio)
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
                val id = clave.text.toString()
                parametros["total"] = totalPrice.toString()
                parametros["idUsuario"] = Usuario.idUsuario
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


    private fun registerDescripcionVenta(URL:String,idVen:Int,idProd:String,cant:Int,precio:Double){
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
                parametros["idVenta"] = idVen.toString()
                parametros["idProd"] = idProd
                parametros["cant"] = cant.toString()
                parametros["precio"] = precio.toString()
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
     * Method that handles the logic regarding registering a sale
     */
    private fun registerSale(){
        finalProducts.clear()
        joinProducts()
        insertIntoVenta("http://charlyffs.mywire.org:9000/registrar_venta.php")
        SystemClock.sleep(200)
        clave.text.clear()
        cantidad.text.clear()
        idVenta = 0
        table?.removeAllViews()
        productos.clear()
        clavesUnicas.clear()
        registrosBase.clear()
        totalPrice = 0.0

    }
}