package com.charles.carritoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.charles.carritoapp.configs.Conexion
import com.charles.carritoapp.configs.ConexionCliente
import com.charles.carritoapp.vistas.ComprasFragment
import com.charles.carritoapp.vistas.HomeFragment


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer_layout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var nav_view: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    lateinit var  idLCiente:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         toolbar =findViewById(R.id.toolbar)
         drawer_layout = findViewById(R.id.drawer_layout)
         nav_view = findViewById(R.id.nav_view)
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
       idCliente= intent.getStringExtra("idCliente")
        val toggle = ActionBarDrawerToggle(
             this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
         drawer_layout.addDrawerListener(toggle)
         toggle.syncState()

         nav_view.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().replace(R.id.mainContent,HomeFragment()).commit()


    }


    fun verCompras(view: View){
        var intent = Intent(this, Carrito::class.java)
        intent.putExtra("idCliente", idCliente)
        startActivity(intent)
    }

override fun onBackPressed() {
 if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
     drawer_layout.closeDrawer(GravityCompat.START)
 } else {
     super.onBackPressed()
 }
}

override fun onCreateOptionsMenu(menu: Menu): Boolean {
 // Inflate the menu; this adds items to the action bar if it is present.
 //menuInflater.inflate(R.menu.main, menu)
 return true
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
 // Handle action bar item clicks here. The action bar will
 // automatically handle clicks on the Home/Up button, so long
 // as you specify a parent activity in AndroidManifest.xml.
 when (item.itemId) {
     R.id.action_settings -> return true
     else -> return super.onOptionsItemSelected(item)
 }
}

override fun onNavigationItemSelected(item: MenuItem): Boolean {
 // Handle navigation view item clicks here.
 when (item.itemId) {
     R.id.nav_first_fragment -> {
         supportFragmentManager.beginTransaction().replace(R.id.mainContent,HomeFragment()).commit()
         setTitle("Import")
     }

     R.id.nav_third_fragment -> {
         supportFragmentManager.beginTransaction().replace(R.id.mainContent,ComprasFragment()).commit()
         setTitle("Carrito")
     }

     R.id.cerrarSesion -> {
         val builder = AlertDialog.Builder(this)
         builder.setTitle("Cerrar Sesión")
         builder.setMessage("Desea sallir de la Aplicacion")
         builder.setPositiveButton("Aceptar") { dialog, which ->
             var conexionCl = ConexionCliente(this)
             var  dbCl = conexionCl.writableDatabase
              dbCl.execSQL("delete from usuario")

             var conexion = Conexion(this)
             var  db = conexion.writableDatabase

             db.execSQL("delete from carrito")

             var intent = Intent(this,LoginActivity::class.java)
             startActivity(intent)
         }
         builder.setNegativeButton("Cancelar") { dialog, which ->
             Toast.makeText(applicationContext,
                 android.R.string.no, Toast.LENGTH_SHORT).show()
         }
         builder.show()
     }

 }

 drawer_layout.closeDrawer(GravityCompat.START)
 return true
}
/*fun setupDrawer(): ActionBarDrawerToggle {
 return  ActionBarDrawerToggle(this, mDrawer,toolbar,"Open","Close")
}*/
/*override fun onOptionsItemSelected(item: MenuItem): Boolean {
 var fragment:Fragment

  when (item.itemId) {
     android.R.id.home -> {
         mDrawer.openDrawer(GravityCompat.START);

         true
     }
     R.id.nav_first_fragment -> {
         supportFragmentManager.beginTransaction().replace(R.id.flContent,ComprasFragment()).commit()
         true
     }
      R.id.nav_second_fragment -> {
          supportFragmentManager.beginTransaction().replace(R.id.flContent,FavsFragment()).commit()
          true
      }
      R.id.nav_third_fragment -> {
          supportFragmentManager.beginTransaction().replace(R.id.flContent,ComprasFragment()).commit()
          true
      }

     else -> super.onOptionsItemSelected(item)
 }
 item.setChecked(true)
 setTitle(item.title)
 mDrawer.closeDrawers()
 return true
}*/


}