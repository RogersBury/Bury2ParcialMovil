package com.charles.carritoapp.adapters

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.charles.carritoapp.R
import com.charles.carritoapp.VerProducto
import com.charles.carritoapp.configs.Conexion
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.configs.DeslizarItemCarrito
import com.charles.carritoapp.modelos.ItemCarrito
import com.charles.carritoapp.modelos.Producto
import org.json.JSONObject
import kotlin.math.log

class CarritoAdapter (val productos:ArrayList<ItemCarrito> , val idCliente:String) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoAdapter.ViewHolder {

        val vista  = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito,parent,false)
        return ViewHolder(vista)

    }

    override fun onBindViewHolder(holder: CarritoAdapter.ViewHolder, position: Int) {

        holder.binItems(productos[position])
    }

    override fun getItemCount(): Int {

        return  productos.size
    }

    fun delete(position: Int, contexto: Context){
        var conexion = Conexion(contexto)
        var  db = conexion.writableDatabase
        val ItemProducto = productos[position]
       db.delete("carrito","id_producto="+ItemProducto.id,null)
        productos.removeAt(position);
        notifyDataSetChanged()
    }

    fun editar(position: Int, contexto: Context){
        val ItemProducto = productos[position]
        var conexion = Conexion(contexto)
        var  db = conexion.writableDatabase

        var intent = Intent( contexto,VerProducto::class.java)
        intent.putExtra("Id",ItemProducto.id)
        intent.putExtra("bandera",ItemProducto.cantidad)
        intent.putExtra("idCliente", idCliente)
        db.delete("carrito","id_producto="+ItemProducto.id,null)

        productos.removeAt(position)

        notifyDataSetChanged()
        contexto.startActivity( intent)

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        init {

        }

        fun binItems(producto: ItemCarrito){

            val nombre= itemView.findViewById<TextView>(R.id.nombreCarrito)
            val precio= itemView.findViewById<TextView>(R.id.precioCarrito)
            val cantidad= itemView.findViewById<EditText>(R.id.editTextCantidadItem)
            val imagen= itemView.findViewById<ImageView>(R.id.imagenCarrito)
            val total = itemView.findViewById<TextView>(R.id.textViewtotalCarrito)
            val txtCantidadItem= itemView.findViewById<EditText>(R.id.editTextCantidadItem)
            txtCantidadItem.setText(producto.cantidad)
            recargar(itemView.context,producto.id,nombre,precio,imagen,producto.cantidad,total)


        }

        fun recargar(contexto: Context, id:String, nombreVer:TextView,precioVer:TextView,imagen:ImageView, cantidad:String,totalVer:TextView){
            var config = Config()
            var url = config.ipServidor+ "Producto/"+ id

            var jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { respuesta: JSONObject ->
                    var item = respuesta.getJSONObject("data")
                  var  producto = Producto(
                        item.getString("idProducto"),
                        item.getString("nombrePro"),
                        item.getString("precioPro"),
                        item.getString("cantidadPro"),
                        item.getString("imagenPro"))
                    nombreVer.text = producto.nombre
                    precioVer.text = "$ "+ String.format("%.2f", producto.precio.toString().toDouble())
                    var total:Double = producto.precio.toDouble() * cantidad.toDouble()
                    totalVer.text = "Total "+"$ "+ String.format("%.2f", total.toString().toDouble())

                    cargarImagen(producto.imagen,imagen,contexto)
                },
                Response.ErrorListener {  },)

            val queue = Volley.newRequestQueue(contexto)
            queue.add(jsonObjectRequest)
        }
        fun cargarImagen(imagenURL:String,imagenVer:ImageView, contexto:Context){

            val queue =  Volley.newRequestQueue(contexto)
            var imageRequest = ImageRequest(imagenURL,
                Response.Listener { respuesta ->
                    imagenVer.setImageBitmap(respuesta)
                },0,0,ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888
                ,Response.ErrorListener {

                })
            queue.add(imageRequest)
        }

    }



}
