package com.example.agromarketapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PurchasedProductsActivity : AppCompatActivity() {

    private lateinit var contenedorCompras: LinearLayout
    private lateinit var buttonVolver: Button
    private lateinit var buttonCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchased_products)

        contenedorCompras = findViewById(R.id.contenedorCompras)
        buttonVolver = findViewById(R.id.buttonVolver)
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion)

        val prefs = getSharedPreferences("datos_usuario", MODE_PRIVATE)
        val usuarioActual = prefs.getString("usuario_actual", null)

        // Mostrar mensaje si se acaba de realizar una compra
        if (intent.getBooleanExtra("compra_realizada", false)) {
            val mensaje = TextView(this)
            mensaje.text = "¡Gracias por tu compra!"
            mensaje.textSize = 20f
            mensaje.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            mensaje.gravity = Gravity.CENTER
            contenedorCompras.addView(mensaje)
        }

        // Cargar productos comprados
        if (usuarioActual != null) {
            val comprasJson = prefs.getString("productos_comprados_$usuarioActual", null)
            val tipoLista = object : TypeToken<MutableList<Producto>>() {}.type
            val productos = if (comprasJson != null)
                Gson().fromJson<MutableList<Producto>>(comprasJson, tipoLista)
            else mutableListOf()

            for (producto in productos) {
                val layoutProducto = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 16, 0, 16)
                }

                // Imagen del producto
                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                        gravity = Gravity.CENTER
                    }
                    val uriStr = prefs.getString("imagen_producto_${usuarioActual}_${producto.nombre}", null)
                    if (!uriStr.isNullOrEmpty()) {
                        setImageURI(Uri.parse(uriStr))
                    } else {
                        setImageResource(R.drawable.imagen_producto) // imagen por defecto
                    }
                }

                // Texto con nombre y precio
                val texto = TextView(this).apply {
                    text = "${producto.nombre} - ${producto.precio}"
                    textSize = 18f
                    gravity = Gravity.CENTER
                }

                layoutProducto.addView(imageView)
                layoutProducto.addView(texto)

                contenedorCompras.addView(layoutProducto)
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
