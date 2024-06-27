package com.charles.carritoapp

import android.database.sqlite.SQLiteException
import com.charles.carritoapp.configs.Conexion
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReporteActivity : AppCompatActivity() {

    private lateinit var textViewReporte: TextView
    private lateinit var conexion: Conexion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte)
        textViewReporte = findViewById(R.id.textViewReporte)

        val idUsuario = intent.getIntExtra("idUsuario", -1)
        conexion = Conexion(this)

        if (idUsuario != -1) {
            val reporte = obtenerReporte(idUsuario)
            textViewReporte.text = reporte
        } else {
            textViewReporte.text = "Error al cargar el reporte"
        }
    }

    fun obtenerReporte(idUsuario: Int): String {
        val conexion = Conexion(this)
        val db = conexion.readableDatabase
        var reporteText = ""

        try {
            val query = "SELECT fecha, accion FROM carrito WHERE id_usuario = ?"
            val cursor = db.rawQuery(query, arrayOf(idUsuario.toString()))

            if (cursor.moveToFirst()) {
                do {
                    val fecha = cursor.getString(cursor.getColumnIndex("fecha"))
                    val accion = cursor.getString(cursor.getColumnIndex("accion"))
                    reporteText += "Fecha: $fecha, Acción: $accion\n"
                } while (cursor.moveToNext())
            } else {
                reporteText = "No se encontraron registros para el usuario $idUsuario"
            }
            cursor.close()
        } catch (e: SQLiteException) {
            Log.e("SQLiteException", "Error al ejecutar la consulta SQL: ${e.message}")
            reporteText = "Error al ejecutar la consulta SQL"
        } finally {
            db.close()
        }

        return reporteText
    }




    override fun onDestroy() {
        super.onDestroy()
        conexion.close()
    }
}