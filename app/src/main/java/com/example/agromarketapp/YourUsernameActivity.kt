package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class YourUsernameActivity : AppCompatActivity() {

    private lateinit var imageViewUsuario: ImageView
    private lateinit var textUsuario: TextView
    private lateinit var textNombres: TextView
    private lateinit var textApellidos: TextView
    private lateinit var textCorreo: TextView
    private lateinit var buttonEditar: Button
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_username)

        imageViewUsuario = findViewById(R.id.imageViewUsuario)
        textUsuario = findViewById(R.id.textUsuario)
        textNombres = findViewById(R.id.textNombres)
        textApellidos = findViewById(R.id.textApellidos)
        textCorreo = findViewById(R.id.textCorreo)
        buttonEditar = findViewById(R.id.buttonEditar)
        buttonVolver = findViewById(R.id.button2)
        buttonCerrarSesion = findViewById(R.id.button1)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        cargarDatos()

        buttonEditar.setOnClickListener {
            startActivity(Intent(this, EditUserActivity::class.java))
        }

        buttonVolver.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        buttonCerrarSesion.setOnClickListener {
            prefs.edit().remove("usuario_actual").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        val lista = Usuario.cargarUsuariosDesdePrefs(prefs)
        val usuario = lista.find { it.usuario == usuarioActual }

        usuario?.let {
            textUsuario.text = it.usuario
            textNombres.text = it.nombres
            textApellidos.text = it.apellidos
            textCorreo.text = it.correo

            val uri = prefs.getString("imagen_usuario_${it.usuario}", null)
            if (!uri.isNullOrEmpty()) {
                imageViewUsuario.setImageURI(Uri.parse(uri))
            } else {
                imageViewUsuario.setImageResource(R.drawable.imagen_usuario)
            }
        }
    }
}
