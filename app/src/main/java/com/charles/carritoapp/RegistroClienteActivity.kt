package com.charles.carritoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.databinding.ActivityRegistroClienteBinding
import org.json.JSONObject

class RegistroClienteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLoadImage.setOnClickListener {
            val imageUrl = binding.editTextImageUrl.text.toString()
            if (imageUrl.isNotEmpty()) {
                loadImage(imageUrl)
            } else {
                Toast.makeText(this, "Por favor ingrese una URL de imagen", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCliienteRegistrar.setOnClickListener {
            var cedula = binding.editTextClienteCedula.text.toString()
            var clave = binding.editTextClienteClave.text.toString()
            var bandera = false

            if (validarCampos(binding)) {
                if (validarCedula(cedula)) {
                    if (validarClave(clave)) {
                        bandera = true
                    } else {
                        Toast.makeText(this, "La clave debe tener mínimo 4 caracteres, " +
                                "mayúscula, minúscula, número y carácter especial", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Cédula incorrecta", Toast.LENGTH_LONG).show()
                }
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Campos Incompletos")
                builder.setMessage("Llene todos los campos")
                builder.setPositiveButton("Aceptar") { dialog, which -> }
                builder.show()
            }

            if (bandera) {
                registrarCliente()
            }
        }
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)

            .into(binding.imageViewUser)
    }

    private fun registrarCliente() {
        val config = Config()
        val url = config.ipServidor + "Cliente"

        val params = HashMap<String, String>()
        params["cedulaCli"] = binding.editTextClienteCedula.text.toString()
        params["nombreCli"] = binding.editTextClienteNombre.text.toString()
        params["apellidoCli"] = binding.editTextClienteApellido.text.toString()
        params["direccionCli"] = binding.editTextClienteDireccion.text.toString()
        params["contrasenia"] = binding.editTextClienteClave.text.toString()
        params["imageUrl"] = binding.editTextImageUrl.text.toString()
        val jsonObject = JSONObject(params as Map<*, *>?)

        // Volley post request with parameters
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                // Process the json
                Toast.makeText(applicationContext, "Cliente Insertado con éxito", Toast.LENGTH_LONG).show()
                guardarImagenLocal(params["imageUrl"]!!)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }, { error ->
                // Error in request
                Toast.makeText(applicationContext, "No se pudo insertar", Toast.LENGTH_LONG).show()
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

    private fun guardarImagenLocal(imageUrl: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userImageUrl", imageUrl)
        editor.apply()
    }

    private fun validarCampos(binding: ActivityRegistroClienteBinding): Boolean {
        if (binding.editTextClienteCedula.text.toString().isEmpty()) return false
        if (binding.editTextClienteApellido.text.toString().isEmpty()) return false
        if (binding.editTextClienteDireccion.text.toString().isEmpty()) return false
        if (binding.editTextClienteNombre.text.toString().isEmpty()) return false
        if (binding.editTextClienteClave.text.toString().isEmpty()) return false
        return true
    }

    private fun validarCedula(cedula: String): Boolean {
        // Validar cédula
        var cedulaCorrecta = false
        try {
            if (cedula.length == 10) {
                val tercerDigito = cedula.substring(2, 3).toInt()
                if (tercerDigito < 6) {
                    val coefValCedula = intArrayOf(2, 1, 2, 1, 2, 1, 2, 1, 2)
                    val verificador = cedula.substring(9, 10).toInt()
                    var suma = 0
                    var digito = 0
                    for (i in 0 until cedula.length - 1) {
                        digito = cedula.substring(i, i + 1).toInt() * coefValCedula[i]
                        suma += digito % 10 + digito / 10
                    }
                    if (suma % 10 == 0 && suma % 10 == verificador) {
                        cedulaCorrecta = true
                    } else if (10 - suma % 10 == verificador) {
                        cedulaCorrecta = true
                    } else {
                        cedulaCorrecta = false
                    }
                } else {
                    cedulaCorrecta = false
                }
            } else {
                cedulaCorrecta = false
            }
        } catch (nfe: NumberFormatException) {
            cedulaCorrecta = false
        } catch (err: Exception) {
            cedulaCorrecta = false
        }
        if (!cedulaCorrecta) {
            println("La Cédula ingresada es Incorrecta")
        }
        return cedulaCorrecta
    }

    private fun validarClave(clave: String): Boolean {
        var mayuscula = false
        var numero = false
        var minuscula = false
        var caracter = false
        var bandera = false

        if (clave.length >= 4) {
            for (item in clave) {
                if (Character.isDigit(item)) numero = true
                if (Character.isUpperCase(item)) mayuscula = true
                if (Character.isLowerCase(item)) minuscula = true
                if (!Character.isLetterOrDigit(item)) caracter = true
            }
        } else {
            bandera = false
        }

        if (numero && mayuscula && minuscula && caracter) bandera = true

        return bandera
    }

}
