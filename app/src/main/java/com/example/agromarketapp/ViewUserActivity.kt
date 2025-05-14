package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ViewUserActivity : AppCompatActivity() {

    private lateinit var imageViewUsuario: ImageView
    private lateinit var textUsuario: TextView
    private lateinit var textNombres: TextView
    private lateinit var textApellidos: TextView
    private lateinit var textCorreo: TextView
    private lateinit var textContrasena: TextView
    private lateinit var buttonVerTienda: Button
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    private lateinit var usuarioPropietario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_user)

        imageViewUsuario = findViewById(R.id.imageViewUsuario)
        textUsuario = findViewById(R.id.textUsuario)
        textNombres = findViewById(R.id.textNombres)
        textApellidos = findViewById(R.id.textApellidos)
        textCorreo = findViewById(R.id.textCorreo)
        textContrasena = findViewById(R.id.textContrasena)
        buttonVerTienda = findViewById(R.id.buttonVerTienda)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioPropietario = intent.getStringExtra("usuario_propietario") ?: return

        // Cargar lista de usuarios
        val listaUsuarios = Usuario.cargarUsuariosDesdePrefs(prefs)
        val usuario = listaUsuarios.find { it.usuario == usuarioPropietario }

        usuario?.let {
            textUsuario.text = "Usuario: ${it.usuario}"
            textNombres.text = "Nombres: ${it.nombres}"
            textApellidos.text = "Apellidos: ${it.apellidos}"
            textCorreo.text = "Correo: ${it.correo}"
            textContrasena.text = "Contraseña: ${it.contrasena}"

            // Cargar imagen del usuario
            val uriStr = prefs.getString("imagen_usuario_${it.usuario}", null)
            if (!uriStr.isNullOrEmpty()) {
                imageViewUsuario.setImageURI(Uri.parse(uriStr))
            }
        }

        // Ir a la tienda del usuario propietario
        buttonVerTienda.setOnClickListener {
            val intent = Intent(this, ViewStoreActivity::class.java)
            intent.putExtra("usuario_propietario", usuarioPropietario)
            startActivity(intent)
        }

        buttonVolver.setOnClickListener {
            finish()
        }

        buttonCerrarSesion.setOnClickListener {
            prefs.edit().remove("usuario_actual").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}
