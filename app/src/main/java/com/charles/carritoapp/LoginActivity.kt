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

        if (cedula.isBlank() || clave.isBlank()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val config = Config()
        val url = "${config.ipServidor}Cliente"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { respuesta: JSONObject ->
                handleLoginResponse(respuesta)
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error en la conexión con el servidor", Toast.LENGTH_LONG).show()
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }

    private fun handleLoginResponse(respuesta: JSONObject) {
        var isAuthenticated = false
        val datos = respuesta.getJSONArray("data")

        for (i in 0 until datos.length()) {
            val item = datos.getJSONObject(i)
            Log.i("Cliente", item.getString("contrasenia"))
            if (cedula == item.getString("cedulaCli") && clave == item.getString("contrasenia")) {
                isAuthenticated = true
                idCliente = item.getString("idCliente")
                break
            }
        }

        if (isAuthenticated) {
            onLoginSuccess(idCliente)
        } else {
          //  onLoginFailed()
        }
    }

    private fun onLoginSuccess(idCliente: String) {
        failedAttempts = 0 // Reiniciar el contador de intentos fallidos
        val conexion = ConexionCliente(this)
        val db = conexion.writableDatabase
        db.execSQL("INSERT INTO usuario (id_usuario) VALUES ($idCliente)")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("idCliente", idCliente)
        startActivity(intent)
    }

//    private fun onLoginFailed() {
//        failedAttempts++
//        if (failedAttempts >= 3) {
//            Toast.makeText(this, "Usuario bloqueado por múltiples intentos fallidos", Toast.LENGTH_LONG).show()
//           // finishAffinity();
//            // Aquí puedes agregar lógica adicional para manejar el bloqueo del usuario
//        } else {
//            Toast.makeText(this, "Usuario o contraseña incorrectos. Intentos fallidos: $failedAttempts", Toast.LENGTH_LONG).show()
//        }
//    }
}
