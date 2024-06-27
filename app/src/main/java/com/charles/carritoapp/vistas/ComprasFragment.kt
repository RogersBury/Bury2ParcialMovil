package com.charles.carritoapp.vistas

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.charles.carritoapp.R
import com.charles.carritoapp.adapters.VentaAdapter
import com.charles.carritoapp.configs.ConexionCliente
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.modelos.Venta
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ComprasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComprasFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var recyclerView: RecyclerView?=null;
    var ventas = ArrayList<Venta>()
    var clienteID: Int = 0


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
        // Inflate the layout for this fragment

       var view = inflater.inflate(R.layout.fragment_compras, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.listaVentas)
        recyclerView?.layoutManager = GridLayoutManager(view.context,1)
        recargar(view.context)
        return  view
    }


    fun recargar(contexto: Context){

        var conexionCl = ConexionCliente(contexto)
        var dbCl = conexionCl.writableDatabase

        var sql = "Select * from usuario"

        var respuesta = dbCl.rawQuery(sql, null)
        if (respuesta.moveToFirst()) {
            do {
                clienteID = respuesta.getString(0).toInt()
            } while (respuesta.moveToNext())
        }

        var config = Config()
        var url = config.ipServidor+ "Venta"
        var jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { respuesta: JSONObject ->
                var datos = respuesta.getJSONArray("data")
                for (i in 0 until datos.length()){
                    val item = datos.getJSONObject(i)
                    // Log.i("Producto",item.getString("nombrePro"))
                    if (item.getString("idCliente").toInt() == clienteID){
                        ventas.add(
                            Venta(
                            item.getString("idVenta"),
                            item.getString("fechaVen"),
                            item.getString("idCliente"),
                            item.getString("total"),
                            item.getString("subtotal"),
                                item.getString("iva"))
                        )
                    }

                }
                 var adapater = VentaAdapter(ventas)
                recyclerView?.adapter = adapater
                var decoracin = DividerItemDecoration(contexto,LinearLayoutManager(contexto).orientation)
                recyclerView?.addItemDecoration(decoracin)

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
         * @return A new instance of fragment ComprasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ComprasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}