package com.charles.carritoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.charles.carritoapp.configs.ConexionCliente
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.modelos.Cliente
import org.json.JSONObject

var clientes = ArrayList<Cliente>()
lateinit var editTextUsuario: EditText
lateinit var editTextClave: EditText
lateinit var cedula: String
lateinit var clave: String
lateinit var idCliente: String
var failedAttempts = 0 // Variable para contar los intentos fallidos

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextUsuario = findViewById(R.id.editTextUsuario)
        editTextClave = findViewById(R.id.editTextTextClave)
    }

    fun registro(view: View) {
        val intent = Intent(this, RegistroClienteActivity::class.java)
        startActivity(intent)
    }

    fun login(view: View) {
        cedula = editTextUsuario.text.toString()
        clave = editTextClave.text.toString()

        val config = Config()
        val url = config.ipServidor + "Cliente"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { respuesta: JSONObject ->
                var bandera = false
                val datos = respuesta.getJSONArray("data")
                for (i in 0 until datos.length()) {
                    val item = datos.getJSONObject(i)
                    Log.i("Cliente", item.getString("contrasenia"))
                    if (cedula == item.getString("cedulaCli").toString() && clave == item.getString("contrasenia").toString()) {
                        bandera = true
                        idCliente = item.getString("idCliente").toString()
                    }
                }
                val conexion = ConexionCliente(this)
                val db = conexion.writableDatabase

                if (bandera) {
                    failedAttempts = 0 // Reiniciar el contador de intentos fallidos
                    db.execSQL("INSERT INTO usuario (id_usuario) VALUES ($idCliente)")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("idCliente", "1")
                    startActivity(intent)
                } else {
                    failedAttempts++
                    if (failedAttempts >= 3) {
                        Toast.makeText(this, "Usuario bloqueado por múltiples intentos fallidos", Toast.LENGTH_LONG).show()
                        // Aquí puedes agregar lógica adicional para manejar el bloqueo del usuario
                    } else {
                        Toast.makeText(this, "Usuario o contraseña incorrectos. Intentos fallidos: $failedAttempts", Toast.LENGTH_LONG).show()
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error en la conexión con el servidor", Toast.LENGTH_LONG).show()
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }
}
