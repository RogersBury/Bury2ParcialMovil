package com.charles.carritoapp.adapters

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.charles.carritoapp.R
import com.charles.carritoapp.VerProducto
import com.charles.carritoapp.modelos.Producto

class ProductoAdapter(var productos:ArrayList<Producto>) : RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoAdapter.ViewHolder {

        val vista  = LayoutInflater.from(parent.context).inflate(R.layout.item_producto,parent,false)
        return ViewHolder(vista)

    }

    override fun onBindViewHolder(holder: ProductoAdapter.ViewHolder, position: Int) {

        holder.binItems(productos[position])
    }

    override fun getItemCount(): Int {

        return  productos.size
    }

    fun setFilterList( _productos:ArrayList<Producto>){
        this.productos=_productos
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
     init {

     }
        fun binItems(producto: Producto){

            //configuracion cuando le dan clic
            itemView.setOnClickListener {

                var intent = Intent(itemView.context,VerProducto::class.java)
                intent.putExtra("Id",producto.id)
                intent.putExtra("bandera","0")
                itemView.context.startActivity( intent)
            }

            val codigo = itemView.findViewById<TextView>(R.id.textViewCodigo)
            codigo.text=producto.id

            val nombre= itemView.findViewById<TextView>(R.id.item_nombre)
            nombre.text =producto.nombre

            val precio= itemView.findViewById<TextView>(R.id.item_precio)
            precio.text = "$"+ String.format("%.2f", producto.precio.toString().toDouble())

            val stock= itemView.findViewById<TextView>(R.id.item_stock)
            stock.text =producto.stock

            val imagen= itemView.findViewById<ImageView>(R.id.item_imagen)
            val queue =  Volley.newRequestQueue(itemView.context)
            var imageRequest = ImageRequest(producto.imagen,
                Response.Listener { respuesta ->
                    imagen.setImageBitmap(respuesta)

                },0,0,ImageView.ScaleType.FIT_XY,Bitmap.Config.ARGB_8888
                ,Response.ErrorListener {

                })

            queue.add(imageRequest)
        }

    }
}