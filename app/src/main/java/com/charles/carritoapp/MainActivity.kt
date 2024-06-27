package com.charles.carritoapp

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.charles.carritoapp.configs.Conexion
import com.charles.carritoapp.configs.ConexionCliente
import com.charles.carritoapp.vistas.ComprasFragment
import com.charles.carritoapp.vistas.HomeFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        // Cargar la imagen guardada en el header
        val headerView = navView.getHeaderView(0)
        val imageViewHeader = headerView.findViewById<ImageView>(R.id.imageViewHeader)
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("userImageUrl", null)
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)

                .into(imageViewHeader)
        }

        supportFragmentManager.beginTransaction().replace(R.id.mainContent, HomeFragment()).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Manejar la selección de elementos del menú de navegación aquí
        when (item.itemId) {
            R.id.nav_first_fragment -> {
                supportFragmentManager.beginTransaction().replace(R.id.mainContent, HomeFragment()).commit()
                title = "Import"
            }
            R.id.nav_third_fragment -> {
                supportFragmentManager.beginTransaction().replace(R.id.mainContent, ComprasFragment()).commit()
                title = "Carrito"
            }
            R.id.cerrarSesion -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Cerrar Sesión")
                builder.setMessage("Desea salir de la Aplicación")
                builder.setPositiveButton("Aceptar") { dialog, which ->
                    val conexionCl = ConexionCliente(this)
                    val dbCl = conexionCl.writableDatabase
                    dbCl.execSQL("delete from usuario")

                    val conexion = Conexion(this)
                    val db = conexion.writableDatabase
                    db.execSQL("delete from carrito")

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                builder.setNegativeButton("Cancelar") { dialog, which ->
                    Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
            R.id.reporte -> {
                generarReporte()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private lateinit var conexionCliente: ConexionCliente

    private fun generarReporte() {
        val db = conexionCliente.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT id_usuario FROM usuario", null)

        if (cursor != null && cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(cursor.getColumnIndex("id_usuario"))
            val intent = Intent(this, ReporteActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)
        }
        cursor?.close()
    }
}
