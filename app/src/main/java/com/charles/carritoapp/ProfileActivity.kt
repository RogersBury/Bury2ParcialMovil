package com.charles.carritoapp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.modelos.Cliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ProfileActivity : AppCompatActivity() {

    private lateinit var cliente: Cliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Obtener el perfil de usuario
        getUserProfile()
    }

    private fun getUserProfile() {
        lifecycleScope.launch {
            val config = Config()
            val url = URL("${config.ipServidor}user/profile")
            withContext(Dispatchers.IO) {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                try {
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)

                        cliente = Cliente(
                            idCliente = jsonResponse.getInt("idCliente"),
                            cedulaCli = jsonResponse.getString("cedulaCli"),
                            nombreCli = jsonResponse.getString("nombreCli"),
                            apellidoCli = jsonResponse.getString("apellidoCli"),
                            direccionCli = jsonResponse.getString("direccionCli"),
                            contrasenia = jsonResponse.getString("contrasenia"),
                            imagenUrl = jsonResponse.getString("imagenUrl")
                        )

                        withContext(Dispatchers.Main) {
                            // Mostrar los datos en la interfaz
                            findViewById<TextView>(R.id.nameTextView).text = cliente.nombreCli
                            findViewById<EditText>(R.id.nameEditText).setText(cliente.nombreCli)
                            findViewById<TextView>(R.id.emailTextView).text = cliente.cedulaCli
                            findViewById<EditText>(R.id.emailEditText).setText(cliente.cedulaCli)
                            findViewById<TextView>(R.id.pointsTextView).text = cliente.direccionCli
                            findViewById<EditText>(R.id.pointsEditText).setText(cliente.direccionCli)

                            // Cargar la imagen del perfil
                            val imageView = findViewById<ImageView>(R.id.profileImageView)
                            Glide.with(this@ProfileActivity)
                                .load(cliente.imagenUrl)
                                .placeholder(R.drawable.ic_placeholder)
                                .into(imageView)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Manejar el error, mostrar un mensaje al usuario, etc.
                } finally {
                    connection.disconnect()
                }
            }
        }
    }

    fun onSaveChanges(view: View) {
        // Guardar los cambios localmente
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("name", findViewById<EditText>(R.id.nameEditText).text.toString())
            putString("email", findViewById<EditText>(R.id.emailEditText).text.toString())
            putString("points", findViewById<EditText>(R.id.pointsEditText).text.toString())
            apply()
        }
    }
}
