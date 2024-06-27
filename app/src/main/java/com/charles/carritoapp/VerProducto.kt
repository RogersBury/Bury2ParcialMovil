package com.charles.carritoapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.charles.carritoapp.configs.Conexion
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.modelos.Producto
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class VerProducto : AppCompatActivity() {

    lateinit var imagenVer :ImageView
    lateinit var nombreVer:TextView
    lateinit var precioVer:TextView
    lateinit var stockVer:TextView
    lateinit var btnMas :Button
    lateinit var  btnMenos:Button
    lateinit var  btnComprar: Button
    lateinit var producto:Producto
    lateinit var txtCantidad:TextView
    lateinit var idCliente:String
    lateinit var  btnImagenCompras:ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_producto)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var  id:String = intent.getStringExtra("Id")
        var  bandera:String = intent.getStringExtra("bandera")
        // idCliente= intent.getStringExtra("idCliente")




        imagenVer = findViewById(R.id.imagenVer)
        nombreVer= findViewById(R.id.nombreVer)
        precioVer= findViewById(R.id.precioVer)
        stockVer = findViewById(R.id.stockVer)
        txtCantidad= findViewById(R.id.editTextCantidad)
        btnComprar= findViewById(R.id.btnComprar)
        btnImagenCompras = findViewById(R.id.toolbarCar)

        btnImagenCompras.setOnClickListener {
            var intent = Intent(this, Carrito::class.java)
            intent.putExtra("idCliente", com.charles.carritoapp.idCliente)
            startActivity(intent)
        }

        recargar(this,id,bandera)

        var conexion = Conexion(this)
        var  db = conexion.writableDatabase
        btnComprar.setOnClickListener {
            val cantidad = txtCantidad.text.toString().toInt()

            if (cantidad > 0 && cantidad <= producto.stock.toInt()) {
                val db = conexion.writableDatabase
                val fechaActual = obtenerFechaActual() // Obtener la fecha actual en el formato deseado

                val values = ContentValues().apply {
                    put("id_producto", producto.id.toInt())
                    put("cantidad", cantidad)
                    put("id_Factura", 1) // Suponiendo que 1 es el ID de la factura para esta compra
                    put("fecha", fechaActual)
                }

                // Determinar la acción (insertar o actualizar)
                val cursor = db.rawQuery("SELECT * FROM carrito WHERE id_producto = ?", arrayOf(producto.id))
                if (cursor.moveToFirst()) {
                    // Actualizar registro existente
                    values.put("accion", "update")
                    db.update("carrito", values, "id_producto = ?", arrayOf(producto.id))
                } else {
                    // Insertar nuevo registro
                    values.put("accion", "insert")
                    db.insert("carrito", null, values)
                }

                // Registrar en el reporte
                db.insert("reporte", null, values)

                cursor.close()
                db.close()

                // Redirigir a la actividad del carrito con el ID del cliente
                val intent = Intent(this, Carrito::class.java)
                intent.putExtra("idCliente", "1")
                startActivity(intent)
            } else {
                // Mostrar mensaje de error si la cantidad es inválida
                AlertDialog.Builder(this)
                    .setTitle("Error en la compra")
                    .setMessage("La cantidad seleccionada no está disponible en stock.")
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }



    }

    fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }


    fun recargar(contexto: Context, id:String,bandera:String){
        var config = Config()
        var url = config.ipServidor+ "Producto/"+ id
        var jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { respuesta: JSONObject ->
                var item = respuesta.getJSONObject("data")
                producto = Producto(
                    item.getString("idProducto"),
                    item.getString("nombrePro"),
                    item.getString("precioPro"),
                    item.getString("cantidadPro"),
                    item.getString("imagenPro")
                )
                nombreVer.text = producto.nombre
                precioVer.text = "$" + String.format("%.2f", producto.precio.toString().toDouble())
                stockVer.text = producto.stock
                cargarImagen(producto.imagen)

                if (bandera != "0") {
                    txtCantidad.setText(bandera)
                }
                findViewById<TextView>(R.id.toolbarTitle).text = producto.nombre
            },
            Response.ErrorListener { },
        )

        val queue = Volley.newRequestQueue(contexto)
        queue.add(jsonObjectRequest)
    }

    fun cargarImagen(imagenURL:String){

        val queue =  Volley.newRequestQueue(this)
        var imageRequest = ImageRequest(imagenURL,
            Response.Listener { respuesta ->
                imagenVer.setImageBitmap(respuesta)

            },0,0,ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888
            ,Response.ErrorListener {

            })

        queue.add(imageRequest)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    fun btnMas (view:View){
        txtCantidad.setText((txtCantidad.text.toString().toInt() + 1 ).toString())
        // Toast.makeText(this,"mas" , Toast.LENGTH_SHORT).show()

    }

    fun btnMenos (view:View){

        if (txtCantidad.text.toString().toInt() > 2){
            txtCantidad.setText((txtCantidad.text.toString().toInt() - 1 ).toString())
        }


    }



}