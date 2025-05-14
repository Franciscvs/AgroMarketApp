package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OtherUsersActivity : AppCompatActivity() {

    private lateinit var contenedorUsuarios: LinearLayout
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users)

        contenedorUsuarios = findViewById(R.id.contenedorUsuarios)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)

        // Cargar lista de usuarios
        val listaUsuarios = Usuario.cargarUsuariosDesdePrefs(prefs)

        // Mostrar solo los otros usuarios
        val otros = listaUsuarios.filter { it.usuario != usuarioActual }

        contenedorUsuarios.removeAllViews()

        for (usuario in otros) {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(24, 24, 24, 24)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 16, 0, 16)
                layoutParams = params
                background = getDrawable(R.drawable.card_background) // fondo opcional si lo tienes
            }

            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200)
                val uriStr = prefs.getString("imagen_usuario_${usuario.usuario}", null)
                if (!uriStr.isNullOrEmpty()) {
                    setImageURI(Uri.parse(uriStr))
                } else {
                    setImageResource(R.drawable.imagen_usuario) // imagen por defecto
                }
            }

            val datos = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val nombre = TextView(this).apply {
                text = "Usuario: ${usuario.usuario}"
                textSize = 18f
            }

            val nombreCompleto = TextView(this).apply {
                text = "Nombre: ${usuario.nombres} ${usuario.apellidos}"
                textSize = 16f
            }

            val botonVer = Button(this).apply {
                text = "Ver perfil"
                setOnClickListener {
                    val intent = Intent(this@OtherUsersActivity, ViewUserActivity::class.java)
                    intent.putExtra("usuario_propietario", usuario.usuario)
                    startActivity(intent)
                }
            }

            datos.addView(nombre)
            datos.addView(nombreCompleto)
            datos.addView(botonVer)

            layout.addView(imageView)
            layout.addView(datos)

            contenedorUsuarios.addView(layout)
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
