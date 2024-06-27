package com.charles.carritoapp.vistas

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.charles.carritoapp.R
import com.charles.carritoapp.adapters.ProductoAdapter
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.modelos.Producto
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() ,SearchView.OnQueryTextListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var productos = ArrayList<Producto>()
    private var recyclerView:RecyclerView?=null;
    lateinit var _adapter:ProductoAdapter
    lateinit var idCliente:String

   private var txtBuscar:SearchView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view= inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.listaProductos)
        recyclerView?.layoutManager = GridLayoutManager(view.context,2)
        txtBuscar = view.findViewById(R.id.txtBuscar)
        txtBuscar!!.clearFocus()
        txtBuscar!!.setOnQueryTextListener(this)

            recargar(view.context)
        return  view
    }



    fun recargar(contexto:Context){
        var config = Config()
        var url = config.ipServidor+ "Producto"
        var jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { respuesta:JSONObject ->
                var datos = respuesta.getJSONArray("data")
                for (i in 0 until datos.length()){
                        val item = datos.getJSONObject(i)
                   // Log.i("Producto",item.getString("nombrePro"))
                    if (item.getString("cantidadPro").toInt()>0){
                        productos.add(Producto(
                            item.getString("idProducto"),
                            item.getString("nombrePro"),
                            item.getString("precioPro"),
                            item.getString("cantidadPro"),
                            item.getString("imagenPro")))
                    }

                }
               // var adapater = ProductoAdapter(productos)
                _adapter= ProductoAdapter(productos)
                recyclerView?.adapter = _adapter

                // Toast.makeText(contexto,respuesta.getString("data") , Toast.LENGTH_SHORT).show()

            },
            Response.ErrorListener {  },)

        val queue = Volley.newRequestQueue(contexto)
        queue.add(jsonObjectRequest)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(p0: String): Boolean {
        filterList(p0)
        return true
    }

    private fun filterList(text: String) {
        var filtarLista = ArrayList<Producto>()

        for ( item in productos){
            if (item.nombre.toLowerCase().contains(text.toLowerCase()) || item.id.contains(text)){
                filtarLista.add(item)
            }
        }

        if (filtarLista.isEmpty()){
            Toast.makeText(this.context,"NO existen datos",Toast.LENGTH_LONG)
        }else{
            _adapter.setFilterList(filtarLista)
        }

    }


}