package com.charles.carritoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.charles.carritoapp.adapters.CarritoAdapter
import com.charles.carritoapp.configs.Conexion
import com.charles.carritoapp.configs.ConexionCliente
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.configs.DeslizarItemCarrito
import com.charles.carritoapp.modelos.DetalleVenta
import com.charles.carritoapp.modelos.ItemCarrito
import com.charles.carritoapp.modelos.Producto
import org.json.JSONObject
import java.util.*


class Carrito : AppCompatActivity() {

    lateinit var btnPagar: Button
    lateinit var txtTotal: TextView
    lateinit var txtSubtotal: TextView
    lateinit var txtIVA: TextView
    var totalSalida = 0.0;
    lateinit var idCliente: String
    var clienteID: Int = 0
    //  lateinit var listaCompra: ArrayList<DetalleVenta>

    val listaCompra: MutableList<DetalleVenta> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)
        lateinit var producto: Producto
        btnPagar = findViewById(R.id.btnPagarCarrito)
        txtTotal = findViewById(R.id.textViewTotal)
        txtSubtotal = findViewById(R.id.textViewSubtotal)
        txtIVA = findViewById(R.id.textViewIVA)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        idCliente = intent.getStringExtra("idCliente")


        var listaProductos = ArrayList<ItemCarrito>()
        var lista = findViewById<RecyclerView>(R.id.listaCarrito)

        var conexion = Conexion(this)
        var db = conexion.writableDatabase
        var sql = "Select * from carrito"
        var respuesta = db.rawQuery(sql, null)

        if (respuesta.moveToFirst()) {
            do {
                listaProductos.add(
                    ItemCarrito(
                        respuesta.getString(1),
                        "",
                        "",
                        "",
                        respuesta.getString(2)
                    )
                )
            } while (respuesta.moveToNext())
        }

        var adapter = CarritoAdapter(listaProductos, idCliente)

        lista.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        var deslizar = object : DeslizarItemCarrito(this) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        adapter.delete(viewHolder.adapterPosition, this@Carrito)
                        recreate()
                    }
                    ItemTouchHelper.RIGHT -> {
                        adapter.editar(viewHolder.adapterPosition, this@Carrito)
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(deslizar)
        touchHelper.attachToRecyclerView(lista)
        lista.adapter = adapter
        var decoracin = DividerItemDecoration(this,LinearLayoutManager(this).orientation)
        lista?.addItemDecoration(decoracin)

        listacorrer(listaProductos)


        btnPagar.setOnClickListener {
            pagar()
        }
    }


    private fun listacorrer(listaProductos: ArrayList<ItemCarrito>) {

        var i = 0;
        for (item in listaProductos) {
            var config = Config()
            var url = config.ipServidor + "Producto/" + item.id
            var _cantidad = item.cantidad;
            var jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { respuesta: JSONObject ->
                    var item = respuesta.getJSONObject("data")
                    val producto = Producto(
                        item.getString("idProducto"),
                        item.getString("nombrePro"),
                        item.getString("precioPro"),
                        item.getString("cantidadPro"),
                        item.getString("imagenPro")
                    )
                    listaCompra.add(
                        DetalleVenta(
                            producto.id.toInt(),
                            producto.precio.toDouble(),
                            _cantidad.toInt()
                        )
                    )
                    var total: Float = producto.precio.toFloat() * _cantidad.toFloat()
                    Log.i("total", total.toString())
                    totalSalida = totalSalida + total
                    var iva = totalSalida * 0.12;
                    var subtotal = totalSalida - iva
                    txtTotal.text = String.format("%.2f", totalSalida.toString().toDouble())
                    txtIVA.text = String.format("%.2f", iva.toString().toDouble())
                    txtSubtotal.text = String.format("%.2f", subtotal.toString().toDouble())


                    i++

                },
                Response.ErrorListener { },
            )
            val queue = Volley.newRequestQueue(this)
            queue.add(jsonObjectRequest)

        }

    }


    fun pagar() {



        var conexionCl = ConexionCliente(this)
        var dbCl = conexionCl.writableDatabase

        var sql = "Select * from usuario"

        var respuesta = dbCl.rawQuery(sql, null)
        if (respuesta.moveToFirst()) {
            do {
                clienteID = respuesta.getString(0).toInt()
                // Log.i("cliente",respuesta.getString(0))
            } while (respuesta.moveToNext())
        }
   // registrarMaestro()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Compra Exitosa")
        builder.setMessage("Desea terminar su compra")
        builder.setPositiveButton("Aceptar") { dialog, which ->
           generarCompra()

        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }
        builder.show()

    }

    fun generarCompra(){
        var conexion = Conexion(this)
        var db = conexion.writableDatabase
        registrarMaestro()
        db.execSQL("delete from carrito")
         var intent = Intent(this,MainActivity::class.java)
        intent.putExtra("idCliente", com.charles.carritoapp.idCliente)

        startActivity(intent)
    }

    fun btnAtrasCarrit(view: View) {

        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("idCliente", com.charles.carritoapp.idCliente)
        startActivity(intent)
    }


    fun registrarMaestro() {

        var config = Config()
        var url = config.ipServidor + "VentaTelefono"
        //var envio: ArrayList<DetalleVenta> = listaCompra as ArrayList<DetalleVenta>
        val params = HashMap<String, Any>()
        val total = txtTotal.text.toString().toFloat()
        val subtotal = txtSubtotal.text.toString().toFloat()
        val iva = txtIVA.text.toString().toFloat()
        params["IdCliente"] = clienteID
        params["Total"] = total
        params["Subtotal"] = subtotal
        params["Iva"] = iva
        val jsonObject = JSONObject(params as Map<*, *>?)

        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            Response.Listener {
                registrarDetalle()
                Toast.makeText(applicationContext, "Venta Insertado con exito", Toast.LENGTH_LONG)
                    .show()

            }, Response.ErrorListener { error ->
                Log.i("error", error.toString())
                Toast.makeText(
                    applicationContext,
                    "No se Inserto la venta con exito",
                    Toast.LENGTH_LONG
                ).show()

            })

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }


    fun registrarDetalle() {
        var config = Config()
        var url = config.ipServidor + "Venta"
        var jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { respuesta: JSONObject ->
                var datos = respuesta.getJSONArray("data")
                val numFac = datos.getJSONObject(0).getString("idVenta").toInt()
                postdetalle(numFac)
            },
            Response.ErrorListener { },
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)

    }

    private fun postdetalle(numFac: Int) {

        var config = Config()
        var url = config.ipServidor + "DetalleVenta"
       for (item in listaCompra){
           val params = HashMap<String, Any>()
           params["IdVentPer"] = numFac
           params["IdPro"] = item.IdPro
           params["Cantidad"] = item.Cantidad
           params["Precio"] = item.Precio
           val jsonObject = JSONObject(params as Map<*, *>?)
           val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
               Response.Listener {

               }, Response.ErrorListener {
               })
           request.retryPolicy = DefaultRetryPolicy(
               DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
               // 0 means no retry
               0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
               1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
           )
           val queue = Volley.newRequestQueue(this)
           queue.add(request)
       }

    }


}