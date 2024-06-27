package com.charles.carritoapp.configs

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Conexion(context: Context) : SQLiteOpenHelper(context, "carrito", null, 3) {

    override fun onCreate(db: SQLiteDatabase?) {
        crearTablaCarrito(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS carrito")
        db?.execSQL("DROP TABLE IF EXISTS reporte")
        crearTablaCarrito(db)
    }

    private fun crearTablaCarrito(db: SQLiteDatabase?) {
        val tablaCarrito = "CREATE TABLE carrito(id INTEGER PRIMARY KEY AUTOINCREMENT, id_producto INTEGER, cantidad INTEGER, id_Factura INTEGER, fecha TEXT, accion TEXT)"
        val tablaReporte = "CREATE TABLE reporte(id INTEGER PRIMARY KEY AUTOINCREMENT, id_producto INTEGER, cantidad INTEGER, id_Factura INTEGER, fecha TEXT, accion TEXT)"

        db?.execSQL(tablaCarrito)
        db?.execSQL(tablaReporte)
    }
}