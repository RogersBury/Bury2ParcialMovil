package com.charles.carritoapp.configs

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ConexionCliente (var contexto: Context): SQLiteOpenHelper(contexto, "usuario", null,1) {
    override fun onCreate(p0: SQLiteDatabase?) {
        var tablaUsuario ="CREATE TABLE usuario( id_usuario INTEGER)"
        p0?.execSQL(tablaUsuario)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}