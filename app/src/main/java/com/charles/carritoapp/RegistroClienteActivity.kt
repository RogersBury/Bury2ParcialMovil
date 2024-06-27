package com.charles.carritoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.charles.carritoapp.configs.Config
import com.charles.carritoapp.databinding.ActivityRegistroClienteBinding
import org.json.JSONObject


class RegistroClienteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_registro_cliente)

        val binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

    binding.buttonCliienteRegistrar.setOnClickListener {
        var cedula = binding.editTextClienteCedula.text.toString()
        var clave= binding.editTextClienteClave.text.toString()
        var bandera:Boolean= false



        if(validarCampos(binding)) {
            if(validarCedula(cedula)){
                if(validarClave(clave)){

                    bandera= true
                }else{
                    Toast.makeText(this,"La clave de tener minimo 4 caracteres, " +
                            "mayuscula, minuscula,numero,y  caracter especial",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Cedula incorrecta",Toast.LENGTH_LONG).show()
            }

        }else{

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Campos Incompletos")
            builder.setMessage("LLene todos los campos")
            builder.setPositiveButton("Aceptar") { dialog, which ->
            }
            builder.show()
        }




        if(bandera){
            var config = Config()
            var url = config.ipServidor+ "Cliente"

            val params = HashMap<String,String>()
            params["cedulaCli"] =  binding.editTextClienteCedula.text.toString()
            params["nombreCli"] = binding.editTextClienteNombre.text.toString()
            params["apellidoCli"] = binding.editTextClienteApellido.text.toString()
            params["direccionCli"] = binding.editTextClienteDireccion.text.toString()
            params["contrasenia"] = binding.editTextClienteClave.text.toString()
            val jsonObject = JSONObject(params as Map<*, *>?)

            // Volley post request with parameters
            val request = JsonObjectRequest(
                Request.Method.POST,url, jsonObject,
                Response.Listener {
                    // Process the json
                    Toast.makeText(applicationContext, "Cliente Insertado con exito", Toast.LENGTH_LONG).show()
                    var inte = Intent(this,LoginActivity::class.java)
                    startActivity(inte)

                }, Response.ErrorListener{
                    // Error in request
                    Toast.makeText(applicationContext, "No se Inserto con exito", Toast.LENGTH_LONG).show()

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

    private fun validarCampos(binding: ActivityRegistroClienteBinding): Boolean {

        if (binding.editTextClienteCedula.text.toString().equals("")) return  false
        if (binding.editTextClienteApellido.text.toString().equals("")) return  false
        if (binding.editTextClienteDireccion.text.toString().equals("")) return  false
        if (binding.editTextClienteNombre.text.toString().equals("")) return  false
        if (binding.editTextClienteClave.text.toString().equals("")) return  false

        return true
    }


    private fun validarCedula(cedula: String): Boolean {

        var cedulaCorrecta = false

        try {
            if (cedula.length === 10) // ConstantesApp.LongitudCedula
            {
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
           // println("Una excepcion ocurrio en el proceso de validadcion")
            cedulaCorrecta = false
        }
        if (!cedulaCorrecta) {
            println("La Cédula ingresada es Incorrecta")
        }
        return cedulaCorrecta
    }

    private fun validarClave( clave: String): Boolean {

        var mayuscula = false
        var numero= false;
        var minuscula = false;
        var caracter = false;

        var bandera=false


        if (clave.length>=4){
            for (item in clave)
            {
                Log.i("clave",item.toString())
                if (Character.isDigit(item))   numero = true
                if (Character.isUpperCase(item)) mayuscula = true
                if (Character.isLowerCase(item)) minuscula = true
                if(!Character.isLetterOrDigit(item)) caracter = true
            }

        }else{
            bandera=false
        }

        if (numero && mayuscula && minuscula && caracter)  bandera = true

        return bandera
    }



}