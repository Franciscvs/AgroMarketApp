package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class OtherUsersActivity : AppCompatActivity() {

    private lateinit var contenedorUsuarios: LinearLayout
    private lateinit var editBuscarUsuario: EditText
    private lateinit var buttonBuscarUsuario: Button
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var usuarioActual: String
    private var todosLosUsuarios = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users)

        contenedorUsuarios = findViewById(R.id.contenedorUsuarios)
        editBuscarUsuario = findViewById(R.id.editBuscarUsuario)
        buttonBuscarUsuario = findViewById(R.id.buttonBuscarUsuario)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        usuarioActual = prefs.getString("usuario_actual", "") ?: ""

        cargarUsuarios()
        mostrarUsuarios(todosLosUsuarios)

        buttonBuscarUsuario.setOnClickListener {
            val filtro = editBuscarUsuario.text.toString().trim().lowercase()
            val resultado = if (filtro.isEmpty()) {
                todosLosUsuarios
            } else {
                todosLosUsuarios.filter {
                    it.usuario.lowercase().contains(filtro) ||
                            it.nombres.lowercase().contains(filtro) ||
                            it.apellidos.lowercase().contains(filtro)
                }.toMutableList()
            }
            mostrarUsuarios(resultado)
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

    private fun cargarUsuarios() {
        val lista = Usuario.cargarUsuariosDesdePrefs(prefs)
        todosLosUsuarios = lista.filter { it.usuario != usuarioActual }.toMutableList()
    }

    private fun mostrarUsuarios(lista: List<Usuario>) {
        contenedorUsuarios.removeAllViews()

        if (lista.isEmpty()) {
            val texto = TextView(this).apply {
                text = "No se encontraron usuarios."
                textSize = 18f
                gravity = android.view.Gravity.CENTER
            }
            contenedorUsuarios.addView(texto)
            return
        }

        for (usuario in lista) {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(24, 24, 24, 24)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 16, 0, 16)
                layoutParams = params
                background = getDrawable(R.drawable.card_background)
            }

            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200)
                val uriStr = prefs.getString("imagen_usuario_${usuario.usuario}", null)
                if (!uriStr.isNullOrEmpty()) {
                    setImageURI(Uri.parse(uriStr))
                } else {
                    setImageResource(R.drawable.imagen_usuario)
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
    }
}
