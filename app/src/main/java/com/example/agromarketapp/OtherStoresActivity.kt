package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OtherStoresActivity : AppCompatActivity() {

    private lateinit var contenedorTiendas: LinearLayout
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_stores)

        contenedorTiendas = findViewById(R.id.contenedorTiendas)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)

        // Cargar lista de usuarios
        val listaUsuarios = Usuario.cargarUsuariosDesdePrefs(prefs)

        // Mostrar solo las tiendas de otros usuarios
        val otrosUsuarios = listaUsuarios.filter { it.usuario != usuarioActual }

        contenedorTiendas.removeAllViews()

        for (usuario in otrosUsuarios) {
            val tiendaJson = prefs.getString("tienda_${usuario.usuario}", null)
            if (!tiendaJson.isNullOrEmpty()) {
                val tienda = Gson().fromJson(tiendaJson, Tienda::class.java)

                val layout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(24, 24, 24, 24)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 16, 0, 16)
                    layoutParams = params
                    background = getDrawable(R.drawable.card_background) // fondo opcional
                }

                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(200, 200)
                    val uri = prefs.getString("imagen_tienda_${usuario.usuario}", null)
                    if (!uri.isNullOrEmpty()) {
                        setImageURI(Uri.parse(uri))
                    } else {
                        setImageResource(R.drawable.imagen_tienda) // imagen por defecto
                    }
                }

                val datos = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 0, 0, 0)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val nombre = TextView(this).apply {
                    text = tienda.nombre
                    textSize = 18f
                }

                val categoria = TextView(this).apply {
                    text = "Categoría: ${tienda.categoria}"
                    textSize = 16f
                }

                val botonVer = Button(this).apply {
                    text = "Ver tienda"
                    setOnClickListener {
                        val intent = Intent(this@OtherStoresActivity, ViewStoreActivity::class.java)
                        intent.putExtra("usuario_propietario", usuario.usuario)
                        startActivity(intent)
                    }
                }

                datos.addView(nombre)
                datos.addView(categoria)
                datos.addView(botonVer)

                layout.addView(imageView)
                layout.addView(datos)

                contenedorTiendas.addView(layout)
            }
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
