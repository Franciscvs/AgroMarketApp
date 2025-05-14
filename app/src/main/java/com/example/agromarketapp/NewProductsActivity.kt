package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NewProductsActivity : AppCompatActivity() {

    private lateinit var contenedorProductos: LinearLayout
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_products)

        contenedorProductos = findViewById(R.id.contenedorProductos)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)

        // Cargar lista de todos los usuarios
        val listaUsuarios = Usuario.cargarUsuariosDesdePrefs(prefs)

        contenedorProductos.removeAllViews()

        for (usuario in listaUsuarios) {
            val productos = Producto.cargarProductosDesdePrefs(prefs, usuario.usuario)
            for (producto in productos) {
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
                    val uriStr = prefs.getString("imagen_producto_${usuario.usuario}_${producto.nombre}", null)
                    if (!uriStr.isNullOrEmpty()) {
                        setImageURI(Uri.parse(uriStr))
                    } else {
                        setImageResource(R.drawable.imagen_producto) // imagen por defecto
                    }
                }

                val datos = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 0, 0, 0)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val nombre = TextView(this).apply {
                    text = producto.nombre
                    textSize = 18f
                }

                val precio = TextView(this).apply {
                    text = "Precio: ${producto.precio}"
                    textSize = 16f
                }

                val botonVer = Button(this).apply {
                    text = "Ver producto"
                    setOnClickListener {
                        val intent = Intent(this@NewProductsActivity, ViewProductActivity::class.java)
                        intent.putExtra("producto", Gson().toJson(producto))
                        intent.putExtra("usuario_propietario", usuario.usuario)
                        startActivity(intent)
                    }
                }

                datos.addView(nombre)
                datos.addView(precio)
                datos.addView(botonVer)

                layout.addView(imageView)
                layout.addView(datos)

                contenedorProductos.addView(layout)
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
